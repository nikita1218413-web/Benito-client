package ru.benito.visuals.mixin;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.visuals.FixCustomFog;

/**
 * FixCustomFog — при активном модуле туман ставится "далеко",
 * чтобы перекрыть кастомные серверные настройки.
 *
 * В 1.21.4 applyFog возвращает запись Fog. Оставляем оригинальное поведение,
 * но при включённом FixCustomFog заменяем результат на "no fog".
 */
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Inject(method = "applyFog", at = @At("RETURN"), cancellable = true)
    private static void benito$disableFog(Camera camera,
                                          BackgroundRenderer.FogType type,
                                          float viewDistance,
                                          boolean thickFog,
                                          float tickDelta,
                                          CallbackInfoReturnable<BackgroundRenderer.FogData> cir) {
        if (BenitoClient.get() == null) return;
        FixCustomFog mod = BenitoClient.get().modules().get(FixCustomFog.class);
        if (mod == null || !mod.isEnabled()) return;

        BackgroundRenderer.FogData data = cir.getReturnValue();
        if (data == null) return;
        // Уводим границы тумана далеко за зону видимости
        data.fogStart = viewDistance * 4f;
        data.fogEnd   = viewDistance * 8f;
        data.fogShape = FogShape.CYLINDER;
        cir.setReturnValue(data);
    }
}
