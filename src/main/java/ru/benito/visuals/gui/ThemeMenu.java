package ru.benito.visuals.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.render.RenderUtils;

/**
 * Подэкран выбора акцентного цвета. Рисуется внутри PanelGuiBeta.
 * Содержит палитру из 10 заранее подобранных цветов.
 */
public final class ThemeMenu {

    private static final int[] PRESETS = {
            0xFF6B4CFF, // фиолетово-синий (по умолчанию)
            0xFFE94560, // коралловый
            0xFF00C2A8, // мятный
            0xFFFFB547, // янтарный
            0xFF3B82F6, // синий
            0xFF10B981, // изумруд
            0xFFF472B6, // розовый
            0xFF8B5CF6, // лиловый
            0xFFEF4444, // красный
            0xFF22D3EE, // циан
    };

    private static int hoverIndex = -1;

    private ThemeMenu() {}

    public static void render(DrawContext ctx, int x, int y, int w, int h, Theme theme) {
        RenderUtils.text(ctx, Text.translatable("benito.gui.theme").getString(),
                x + 4, y + 2, 0xFFFFFFFF, true);
        RenderUtils.text(ctx, Text.translatable("benito.gui.accent").getString(),
                x + 4, y + 18, 0xFF9A9AA8, false);

        int cellSize = 34;
        int gap = 6;
        int startY = y + 40;

        int mouseX = (int) (MinecraftClient.getInstance().mouse.getX() / MinecraftClient.getInstance().getWindow().getScaleFactor());
        int mouseY = (int) (MinecraftClient.getInstance().mouse.getY() / MinecraftClient.getInstance().getWindow().getScaleFactor());
        hoverIndex = -1;

        for (int i = 0; i < PRESETS.length; i++) {
            int col = i % 6;
            int row = i / 6;
            int cx = x + 4 + col * (cellSize + gap);
            int cy = startY + row * (cellSize + gap);

            boolean hover = mouseX >= cx && mouseX < cx + cellSize && mouseY >= cy && mouseY < cy + cellSize;
            if (hover) hoverIndex = i;

            RenderUtils.roundedRect(ctx, cx, cy, cellSize, cellSize, 6, PRESETS[i]);
            if (PRESETS[i] == theme.getAccent()) {
                RenderUtils.outline(ctx, cx - 1, cy - 1, cellSize + 2, cellSize + 2, 0xFFFFFFFF);
            } else if (hover) {
                RenderUtils.outline(ctx, cx, cy, cellSize, cellSize, 0xFFFFFFFF);
            }
        }

        // Превью акцента
        int previewY = startY + ((PRESETS.length + 5) / 6) * (cellSize + gap) + 12;
        RenderUtils.text(ctx, "Превью:", x + 4, previewY, 0xFFFFFFFF, true);
        RenderUtils.roundedRect(ctx, x + 60, previewY - 2, 120, 14, 4, theme.getAccent());
    }

    public static boolean mouseClicked(double mx, double my, int x, int y, int w, int h, Theme theme) {
        if (hoverIndex < 0 || hoverIndex >= PRESETS.length) return false;

        theme.setAccent(PRESETS[hoverIndex]);
        BenitoClient.get().config().save();

        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player
                    .sendMessage(Text.translatable("benito.msg.theme.changed"), true);
        }
        return true;
    }
}
