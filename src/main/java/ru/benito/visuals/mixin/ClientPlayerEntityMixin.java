package ru.benito.visuals.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.event.Events;
import ru.benito.visuals.module.impl.combat.ShiftTap;

/**
 * Mixin над ClientPlayerEntity.
 *   1) Трекаем атаку через ClientPlayerInteractionManager#attackEntity. Проще — хук через MinecraftClientMixin,
 *      но для простоты вешаемся на метод isSneaking() — переопределяем значение, если ShiftTap активен.
 *
 * Также прокидываем событие ATTACK_ENTITY через хук на swingHand().
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

    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"))
    private void benito$onSwing(Hand hand, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        // Этот хук не даёт таргет, но используется для обнаружения "удара".
        // Реальный таргет эмитится через MinecraftClientMixin#doAttack (ниже).
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void benito$onAttack(Entity target, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;
        boolean crit = self.fallDistance > 0.0f
                && !self.isOnGround()
                && !self.isClimbing()
                && !self.isTouchingWater()
                && !self.hasVehicle();
        Events.ATTACK_ENTITY.invoker().onAttack(target, crit);
    }
}
