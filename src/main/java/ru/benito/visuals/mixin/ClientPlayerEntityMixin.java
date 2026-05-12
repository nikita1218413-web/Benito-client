package ru.benito.visuals.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.combat.ShiftTap;

/**
 * ClientPlayerEntity — форсированное "не-сникинг" состояние при ShiftTap.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "isSneaking", at = @At("RETURN"), cancellable = true)
    private void benito$maybeUnsneak(CallbackInfoReturnable<Boolean> cir) {
        if (BenitoClient.get() == null) return;
        ShiftTap st = BenitoClient.get().modules().get(ShiftTap.class);
        if (st != null && st.isEnabled() && st.shouldForceUnsneak()) {
            cir.setReturnValue(false);
        }
    }
}
