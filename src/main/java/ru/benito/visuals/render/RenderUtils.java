package ru.benito.visuals.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Утилиты рендера 2D (GUI / HUD) для Minecraft 1.21.4 на базе DrawContext.
 * 3D-оверлеи в 1.21.4 требуют глубокой интеграции в новую систему рендера,
 * поэтому мировые эффекты (ItemRadius/HitEffect) реализованы через частицы.
 */
public final class RenderUtils {

    private RenderUtils() {}

    public static void fill(DrawContext ctx, int x, int y, int w, int h, int argb) {
        ctx.fill(x, y, x + w, y + h, argb);
    }

    public static void outline(DrawContext ctx, int x, int y, int w, int h, int argb) {
        ctx.fill(x,           y,           x + w,     y + 1,     argb);
        ctx.fill(x,           y + h - 1,   x + w,     y + h,     argb);
        ctx.fill(x,           y + 1,       x + 1,     y + h - 1, argb);
        ctx.fill(x + w - 1,   y + 1,       x + w,     y + h - 1, argb);
    }

    public static void rect(DrawContext ctx, int x, int y, int w, int h, int fill, int border) {
        fill(ctx, x, y, w, h, fill);
        outline(ctx, x, y, w, h, border);
    }

    public static void roundedRect(DrawContext ctx, int x, int y, int w, int h, int radius, int argb) {
        if (radius <= 0) { fill(ctx, x, y, w, h, argb); return; }
        if (radius * 2 > w) radius = w / 2;
        if (radius * 2 > h) radius = h / 2;

        ctx.fill(x + radius,     y,              x + w - radius, y + h,          argb);
        ctx.fill(x,              y + radius,     x + radius,     y + h - radius, argb);
        ctx.fill(x + w - radius, y + radius,     x + w,          y + h - radius, argb);

        for (int i = 0; i < radius; i++) {
            int inset = radius - i;
            ctx.fill(x + inset,          y + i,              x + w - inset,          y + i + 1,          argb);
            ctx.fill(x + inset,          y + h - i - 1,      x + w - inset,          y + h - i,          argb);
        }
    }

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
}
