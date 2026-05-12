package ru.benito.visuals.mixin;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.visuals.AspectRatio;

/**
 * AspectRatio — переопределяет framebuffer height так, чтобы width/height = target ratio.
 */
@Mixin(Window.class)
public abstract class WindowMixin {

    @Inject(method = "getFramebufferHeight", at = @At("RETURN"), cancellable = true)
    private void benito$adjustHeight(CallbackInfoReturnable<Integer> cir) {
        if (BenitoClient.get() == null) return;
        AspectRatio ar = BenitoClient.get().modules().get(AspectRatio.class);
        if (ar == null || !ar.isEnabled()) return;

        Window self = (Window) (Object) this;
        int w = self.getFramebufferWidth();
        float target = ar.getRatio();
        if (target <= 0) return;

        int adjusted = Math.max(1, Math.round(w / target));
        cir.setReturnValue(adjusted);
    }
}
