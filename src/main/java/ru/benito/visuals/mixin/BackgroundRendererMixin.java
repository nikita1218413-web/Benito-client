package ru.benito.visuals.mixin;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.visuals.FixCustomFog;

/**
 * FixCustomFog — подменяет результат applyFog на "дальний" fog,
 * убирая эффект "густого" кастомного тумана.
 * В 1.21.4 applyFog возвращает record Fog (публичный) — без проблем с доступом.
 */
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Inject(method = "applyFog", at = @At("RETURN"), cancellable = true)
    private static void benito$extendFog(Camera camera,
                                         BackgroundRenderer.FogType type,
                                         Vector4f colour,
                                         float viewDistance,
                                         boolean thickFog,
                                         float tickDelta,
                                         CallbackInfoReturnable<Fog> cir) {
        if (BenitoClient.get() == null) return;
        FixCustomFog mod = BenitoClient.get().modules().get(FixCustomFog.class);
        if (mod == null || !mod.isEnabled()) return;

        Fog original = cir.getReturnValue();
        if (original == null) return;

        float farStart = viewDistance * 4f;
        float farEnd   = viewDistance * 8f;
        Fog clear = new Fog(
                farStart, farEnd,
                FogShape.CYLINDER,
                original.red(), original.green(), original.blue(), 0f);
        cir.setReturnValue(clear);
    }
}
