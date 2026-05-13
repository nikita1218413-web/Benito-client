package ru.benito.visuals.gui;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import ru.benito.visuals.BenitoMod;
import ru.benito.visuals.render.RenderUtils;

/**
 * UpdateMainMenu — кастомное главное меню.
 *
 * Реализация через Fabric Screen API (ScreenEvents.AFTER_INIT) — надёжнее Mixin'а,
 * который может не сработать из-за reobfusc / маппинга.
 */
public final class UpdateMainMenu {

    public static final String FUNTIME_ADDRESS = "play.funtime.su";

    private UpdateMainMenu() {}

    /** Регистрация обработчика — вызывается один раз из BenitoClient. */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof TitleScreen title)) return;
            injectButtons(title);
            ScreenEvents.afterRender(screen).register((scr, ctx, mouseX, mouseY, delta) ->
                    renderBrand(ctx, scaledWidth, scaledHeight));
        });
    }

    private static void injectButtons(TitleScreen screen) {
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

        try {
            Screens.getButtons(screen).add(join);
            Screens.getButtons(screen).add(settings);
        } catch (Throwable t) {
            BenitoMod.LOGGER.warn("[Benito] Не удалось добавить кнопки в главное меню: {}", t.getMessage());
        }
    }

    /** Рисует лого Benito поверх стандартного TitleScreen. */
    public static void renderBrand(DrawContext ctx, int screenW, int screenH) {
        int x = 10;
        int y = screenH - 28;
        String s = "§lBenito Visuals §r§7v" + BenitoMod.MOD_VERSION + " §8· §f" + FUNTIME_ADDRESS;
        RenderUtils.text(ctx, s, x, y, 0xFFFFFFFF, true);
    }

    private static void joinFuntime(Screen parent) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            ServerInfo info = new ServerInfo("FunTime", FUNTIME_ADDRESS, ServerInfo.ServerType.OTHER);
            ServerAddress addr = ServerAddress.parse(FUNTIME_ADDRESS);
            ConnectScreen.connect(parent, mc, addr, info, false, null);
        } catch (Throwable t) {
            BenitoMod.LOGGER.error("[Benito] Ошибка коннекта на FunTime", t);
        }
    }
}
