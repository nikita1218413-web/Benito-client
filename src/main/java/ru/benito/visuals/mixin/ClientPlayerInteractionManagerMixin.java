package ru.benito.visuals.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benito.visuals.event.Events;

/**
 * Эмитит Events.ATTACK_ENTITY при ударе игрока.
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void benito$onAttack(PlayerEntity attacker, Entity target, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean crit = attacker == mc.player
                && attacker.fallDistance > 0.0f
                && !attacker.isOnGround()
                && !attacker.isClimbing()
                && !attacker.isTouchingWater()
                && !attacker.hasVehicle();
        Events.ATTACK_ENTITY.invoker().onAttack(target, crit);
    }
}
