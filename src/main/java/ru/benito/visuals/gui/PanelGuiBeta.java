package ru.benito.visuals.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;
import ru.benito.visuals.render.RenderUtils;

import java.util.List;

/**
 * PanelGuiBeta — современное русскоязычное ClickGUI.
 *
 * Лейаут (резиновый, 800x500 @ GUI-scale):
 *   +-------------------------------+----------------------------+
 *   | LEFT SIDEBAR                  | MODULE LIST                |
 *   |   - Бой                       |  [toggle] ShiftTap   [⚙]   |
 *   |   - Визуалы                   |  [toggle] CustomHitBox     |
 *   |   - Игрок                     |  ...                       |
 *   |   - Разное                    |                            |
 *   |   - FunTime                   |                            |
 *   |   - Тема                      |                            |
 *   +-------------------------------+----------------------------+
 */
public final class PanelGuiBeta extends Screen {

    private static final int WIDTH  = 520;
    private static final int HEIGHT = 340;
    private static final int SIDEBAR_W = 140;

    private Category active = Category.COMBAT;
    private boolean themeOpen = false;
    private int scroll = 0;

    public PanelGuiBeta() {
        super(Text.translatable("benito.gui.title"));
    }

    /* ---------- Layout helpers ---------- */

    private int originX() { return (this.width  - WIDTH)  / 2; }
    private int originY() { return (this.height - HEIGHT) / 2; }

    /* ---------- Render ---------- */

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        Theme theme = BenitoClient.get().theme();
        int ox = originX();
        int oy = originY();

        // Общий фон (полупрозрачная заливка)
        ctx.fill(0, 0, this.width, this.height, 0x80000000);

        // Основной прямоугольник
        RenderUtils.roundedRect(ctx, ox, oy, WIDTH, HEIGHT, 6, theme.getBackground());
        RenderUtils.outline(ctx, ox, oy, WIDTH, HEIGHT, theme.getAccent());

        // Заголовок
        RenderUtils.text(ctx,
                Text.translatable("benito.gui.title").getString(),
                ox + 12, oy + 10, 0xFFFFFFFF, true);
        RenderUtils.fill(ctx, ox + 10, oy + 24, WIDTH - 20, 1, theme.getAccent());

        // Sidebar
        renderSidebar(ctx, ox, oy, mouseX, mouseY, theme);

        // Content
        int contentX = ox + SIDEBAR_W + 10;
        int contentY = oy + 32;
        int contentW = WIDTH - SIDEBAR_W - 20;
        int contentH = HEIGHT - 42;

        if (themeOpen) {
            ThemeMenu.render(ctx, contentX, contentY, contentW, contentH, theme);
        } else {
            renderModules(ctx, contentX, contentY, contentW, contentH, mouseX, mouseY, theme);
        }
    }

    private void renderSidebar(DrawContext ctx, int ox, int oy, int mx, int my, Theme theme) {
        int x = ox + 6;
        int y = oy + 32;
        int w = SIDEBAR_W - 6;

        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            Category c = cats[i];
            boolean hover = mx >= x && mx < x + w && my >= y && my < y + 24;
            boolean sel   = !themeOpen && c == active;

            int bg = sel ? theme.getAccentFill(180) :
                     hover ? 0x40FFFFFF : 0x00000000;
            if (bg != 0) RenderUtils.roundedRect(ctx, x, y, w, 22, 4, bg);

            RenderUtils.text(ctx, Text.translatable(c.getTranslationKey()).getString(),
                    x + 10, y + 7, sel ? 0xFFFFFFFF : 0xFFC8C8D0, false);
            y += 26;
        }

        // Кнопка "Тема"
        boolean thHover = mx >= x && mx < x + w && my >= y && my < y + 24;
        boolean thSel   = themeOpen;
        int thBg = thSel ? theme.getAccentFill(180) : thHover ? 0x40FFFFFF : 0x00000000;
        if (thBg != 0) RenderUtils.roundedRect(ctx, x, y, w, 22, 4, thBg);
        RenderUtils.text(ctx, Text.translatable("benito.gui.theme").getString(),
                x + 10, y + 7, thSel ? 0xFFFFFFFF : 0xFFC8C8D0, false);
    }

    private void renderModules(DrawContext ctx, int x, int y, int w, int h, int mx, int my, Theme theme) {
        List<Module> list = BenitoClient.get().modules().getByCategory(active);
        int rowH = 28;
        int listY = y;
        int yy = listY - scroll;

        for (Module m : list) {
            boolean hover = mx >= x && mx < x + w && my >= yy && my < yy + rowH;
            int bg = hover ? 0x30FFFFFF : 0x20FFFFFF;
            RenderUtils.roundedRect(ctx, x, yy, w, rowH - 2, 4, bg);

            // Левый акцент-бар для включённых
            if (m.isEnabled()) {
                RenderUtils.fill(ctx, x, yy, 3, rowH - 2, theme.getAccent());
            }

            RenderUtils.text(ctx, m.getDisplayName(), x + 10, yy + 5, 0xFFFFFFFF, true);
            RenderUtils.text(ctx, m.getDescription(),  x + 10, yy + 15, 0xFF8A8A98, false);

            // Тоггл справа
            int tx = x + w - 44;
            int ty = yy + 6;
            int trackBg = m.isEnabled() ? theme.getAccentFill(220) : 0xFF3A3A48;
            RenderUtils.roundedRect(ctx, tx, ty, 34, 14, 7, trackBg);
            int knobX = m.isEnabled() ? tx + 22 : tx + 2;
            RenderUtils.roundedRect(ctx, knobX, ty + 2, 10, 10, 5, 0xFFFFFFFF);

            yy += rowH;
        }
    }

    /* ---------- Input ---------- */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        Theme theme = BenitoClient.get().theme();

        int ox = originX();
        int oy = originY();

        // Sidebar click
        int sx = ox + 6;
        int sy = oy + 32;
        int sw = SIDEBAR_W - 6;

        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            if (mouseX >= sx && mouseX < sx + sw && mouseY >= sy && mouseY < sy + 24) {
                active = cats[i];
                themeOpen = false;
                return true;
            }
            sy += 26;
        }
        if (mouseX >= sx && mouseX < sx + sw && mouseY >= sy && mouseY < sy + 24) {
            themeOpen = true;
            return true;
        }

        // ThemeMenu input
        int cx = ox + SIDEBAR_W + 10;
        int cy = oy + 32;
        int cw = WIDTH - SIDEBAR_W - 20;
        int ch = HEIGHT - 42;

        if (themeOpen) {
            if (ThemeMenu.mouseClicked(mouseX, mouseY, cx, cy, cw, ch, theme)) return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        // Module list click (toggle)
        List<Module> list = BenitoClient.get().modules().getByCategory(active);
        int rowH = 28;
        int yy = cy - scroll;
        for (Module m : list) {
            if (mouseX >= cx && mouseX < cx + cw && mouseY >= yy && mouseY < yy + rowH) {
                m.toggle();
                BenitoClient.get().config().save();
                return true;
            }
            yy += rowH;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizAmount, double verticalAmount) {
        int listSize = BenitoClient.get().modules().getByCategory(active).size() * 28;
        int visible  = HEIGHT - 42;
        int maxScroll = Math.max(0, listSize - visible);
        scroll -= (int) (verticalAmount * 18);
        if (scroll < 0) scroll = 0;
        if (scroll > maxScroll) scroll = maxScroll;
        return true;
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void close() {
        BenitoClient.get().config().save();
        MinecraftClient.getInstance().setScreen(null);
    }
}
