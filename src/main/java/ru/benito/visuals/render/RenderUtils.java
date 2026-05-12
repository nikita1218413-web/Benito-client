package ru.benito.visuals.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * Утилиты рендера для Minecraft 1.21.4 (DrawContext + BufferBuilder API).
 * Содержит 2D-примитивы для GUI/HUD и 3D-кольца/сферы для world-overlay (ItemRadius).
 */
public final class RenderUtils {

    private RenderUtils() {}

    /* ====================== 2D (GUI / HUD) ====================== */

    /** Прямоугольник с заливкой (ARGB). */
    public static void fill(DrawContext ctx, int x, int y, int w, int h, int argb) {
        ctx.fill(x, y, x + w, y + h, argb);
    }

    /** Прямоугольная рамка толщиной 1px. */
    public static void outline(DrawContext ctx, int x, int y, int w, int h, int argb) {
        ctx.fill(x,           y,           x + w,     y + 1,     argb); // top
        ctx.fill(x,           y + h - 1,   x + w,     y + h,     argb); // bottom
        ctx.fill(x,           y + 1,       x + 1,     y + h - 1, argb); // left
        ctx.fill(x + w - 1,   y + 1,       x + w,     y + h - 1, argb); // right
    }

    /** Прямоугольник с рамкой + заливкой. */
    public static void rect(DrawContext ctx, int x, int y, int w, int h, int fill, int border) {
        fill(ctx, x, y, w, h, fill);
        outline(ctx, x, y, w, h, border);
    }

    /** Псевдо-скруглённый прямоугольник (дешёвый: два прямоугольника + 4 "выемки"). */
    public static void roundedRect(DrawContext ctx, int x, int y, int w, int h, int radius, int argb) {
        if (radius <= 0) { fill(ctx, x, y, w, h, argb); return; }
        if (radius * 2 > w) radius = w / 2;
        if (radius * 2 > h) radius = h / 2;

        ctx.fill(x + radius,     y,              x + w - radius, y + h,          argb);
        ctx.fill(x,              y + radius,     x + radius,     y + h - radius, argb);
        ctx.fill(x + w - radius, y + radius,     x + w,          y + h - radius, argb);

        // Углы имитируем ступенькой — глазом незаметно при радиусе <= 4 px
        for (int i = 0; i < radius; i++) {
            int inset = radius - i;
            ctx.fill(x + inset,          y + i,              x + w - inset,          y + i + 1,          argb);
            ctx.fill(x + inset,          y + h - i - 1,      x + w - inset,          y + h - i,          argb);
        }
    }

    /** Горизонтальный градиент. */
    public static void gradientH(DrawContext ctx, int x, int y, int w, int h, int left, int right) {
        ctx.fillGradient(x, y, x + w, y + h, left, right);
    }

    /** Линия текста. */
    public static void text(DrawContext ctx, String s, int x, int y, int color, boolean shadow) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (shadow) ctx.drawTextWithShadow(mc.textRenderer, s, x, y, color);
        else        ctx.drawText(mc.textRenderer, s, x, y, color, false);
    }

    public static int textWidth(String s) {
        return MinecraftClient.getInstance().textRenderer.getWidth(s);
    }

    public static int textHeight() {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }

    /* ====================== 3D (WORLD OVERLAY) ====================== */

    /**
     * Рисует круг/кольцо в мире на горизонтальной плоскости.
     * Используется ItemRadius для отображения радиуса донат-предметов.
     *
     * @param center   центр круга (абсолютные координаты мира)
     * @param radius   радиус
     * @param segments количество сегментов (например 64)
     * @param argb     цвет ARGB
     */
    public static void drawWorldCircle(Vec3d center, double radius, int segments, int argb) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d cam = camera.getPos();

        float a = ((argb >> 24) & 0xFF) / 255f;
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >>  8) & 0xFF) / 255f;
        float b = ( argb        & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f mat = RenderSystem.getModelViewStack();
        mat.pushMatrix();
        mat.translate(
                (float) (center.x - cam.x),
                (float) (center.y - cam.y),
                (float) (center.z - cam.z));
        RenderSystem.applyModelViewMatrix();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        double lineWidth = 0.05;
        for (int i = 0; i <= segments; i++) {
            double theta = (Math.PI * 2.0) * i / segments;
            float cx =  (float) (Math.cos(theta) * radius);
            float cz =  (float) (Math.sin(theta) * radius);
            float cxi = (float) (Math.cos(theta) * (radius - lineWidth));
            float czi = (float) (Math.sin(theta) * (radius - lineWidth));

            buf.vertex(cx, 0f, cz).color(r, g, b, a);
            buf.vertex(cxi, 0f, czi).color(r, g, b, a);
        }
        BufferRenderer.drawWithGlobalProgram(buf.end());

        mat.popMatrix();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    /**
     * Сфера-wireframe (параллели + меридианы). Вариант отображения радиуса "сферой".
     */
    public static void drawWorldSphere(Vec3d center, double radius, int rings, int argb) {
        // Горизонтальные кольца
        for (int i = 1; i < rings; i++) {
            double phi = Math.PI * i / rings - Math.PI / 2.0;
            double r = Math.cos(phi) * radius;
            double y = Math.sin(phi) * radius;
            drawWorldCircle(center.add(0, y, 0), r, 48, argb);
        }
    }
}
