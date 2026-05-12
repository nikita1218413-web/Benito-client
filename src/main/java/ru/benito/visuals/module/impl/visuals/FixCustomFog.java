package ru.benito.visuals.module.impl.visuals;

import net.minecraft.client.MinecraftClient;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * FixCustomFog — снижает эффект кастомного тумана, выставляя клиентский
 * render-distance на максимум пока модуль включён (альтернатива Mixin'у,
 * т.к. BackgroundRenderer.FogData в 1.21.4 имеет package-private доступ).
 */
public final class FixCustomFog extends Module {

    private int savedDistance = -1;

    public FixCustomFog() {
        super("FixCustomFog", "FixCustomFog",
                "Убирает кастомный туман", Category.VISUALS);
    }

    @Override
    protected void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        savedDistance = mc.options.getViewDistance().getValue();
        mc.options.getViewDistance().setValue(32);
    }

    @Override
    protected void onDisable() {
        if (savedDistance > 0) {
            MinecraftClient.getInstance().options.getViewDistance().setValue(savedDistance);
        }
    }
}
