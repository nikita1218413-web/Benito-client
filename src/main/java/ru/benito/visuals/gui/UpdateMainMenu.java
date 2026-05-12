package ru.benito.visuals.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import ru.benito.visuals.render.RenderUtils;

/**
 * UpdateMainMenu — кастомное главное меню.
 *
 * Добавляет в стандартный TitleScreen две кастомные кнопки:
 *   1) "Зайти на FunTime"  — прямой коннект на play.funtime.su
 *   2) "Настройки Benito"  — открывает PanelGuiBeta (ClickGUI)
 *
 * Подключение — через TitleScreenMixin (см. mixin/).
 */
public final class UpdateMainMenu {

    public static final String FUNTIME_ADDRESS = "play.funtime.su";

    private UpdateMainMenu() {}

    /** Вызывается из TitleScreenMixin на init(). */
    public static void inject(TitleScreen screen) {
        int centerX = screen.width / 2;
        int y = screen.height / 4 + 120;

        ButtonWidget join = ButtonWidget
                .builder(Text.translatable("benito.menu.join"), b -> joinFuntime(screen))
                .dimensions(centerX - 100, y, 200, 20)
                .build();

        ButtonWidget settings = ButtonWidget
                .builder(Text.translatable("benito.menu.settings"),
                        b -> MinecraftClient.getInstance().setScreen(new PanelGuiBeta()))
                .dimensions(centerX - 100, y + 24, 200, 20)
                .build();

        addWidget(screen, join);
        addWidget(screen, settings);
    }

    /** Рисует лого Benito поверх стандартного TitleScreen. */
    public static void renderBrand(DrawContext ctx, int screenW, int screenH) {
        int x = 10;
        int y = screenH - 28;
        String s = "§lBenito Visuals §r§7v1.0 §8· §f" + UpdateMainMenu.FUNTIME_ADDRESS;
        RenderUtils.text(ctx, s, x, y, 0xFFFFFFFF, true);
    }

    private static void joinFuntime(Screen parent) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ServerInfo info = new ServerInfo("FunTime", FUNTIME_ADDRESS, ServerInfo.ServerType.OTHER);
        net.minecraft.client.gui.screen.multiplayer.ConnectScreen.connect(
                parent, mc, ServerAddress.parse(FUNTIME_ADDRESS), info, false, null);
    }

    /** Добавить виджет в экран через публичное API addDrawableChild. */
    private static void addWidget(TitleScreen screen, ButtonWidget w) {
        ((ScreenAccess) screen).benito$addDrawable(w);
    }

    /** Контракт, который реализуется через TitleScreenMixin (invoker accessor). */
    public interface ScreenAccess {
        void benito$addDrawable(net.minecraft.client.gui.widget.ClickableWidget widget);
    }
}
