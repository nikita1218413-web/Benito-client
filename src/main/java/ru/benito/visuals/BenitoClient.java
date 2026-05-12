package ru.benito.visuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import ru.benito.visuals.config.ConfigManager;
import ru.benito.visuals.gui.PanelGuiBeta;
import ru.benito.visuals.gui.Theme;
import ru.benito.visuals.input.Keybindings;
import ru.benito.visuals.module.ModuleManager;

/**
 * Клиентский entrypoint.
 * Собирает менеджеры, регистрирует события и биндинги.
 */
public final class BenitoClient implements ClientModInitializer {

    private static BenitoClient INSTANCE;

    private ModuleManager moduleManager;
    private Theme theme;
    private ConfigManager configManager;

    public static BenitoClient get() {
        return INSTANCE;
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        this.theme = new Theme();
        this.moduleManager = new ModuleManager();
        this.configManager = new ConfigManager(moduleManager, theme);

        Keybindings.register();

        // Tick — прокидываем в менеджер (модули получают onTick)
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        // HUD — рендер оверлеев включенных модулей
        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;
            moduleManager.onHudRender(ctx, tickDelta.getTickDelta(false));
        });

        configManager.load();
        BenitoMod.LOGGER.info("[Benito] Клиент инициализирован, модулей: {}", moduleManager.getModules().size());
    }

    private void onClientTick(MinecraftClient mc) {
        Keybindings.handle(mc);

        if (mc.player == null) return;

        moduleManager.onTick();

        // Открытие ClickGUI по биндингу
        if (Keybindings.OPEN_GUI.wasPressed()) {
            mc.setScreen(new PanelGuiBeta());
        }
    }

    public ModuleManager modules() { return moduleManager; }
    public Theme theme()           { return theme; }
    public ConfigManager config()  { return configManager; }
}
