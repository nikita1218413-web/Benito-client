package ru.benito.visuals.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.combat.CustomHitBox;

/**
 * CustomHitBox — раздувает bounding box сущностей,
 * чтобы F3+B-хитбокс и клик-рейкаст были больше.
 * Хук на реально существующий метод calculateBoundingBox.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "calculateBoundingBox", at = @At("RETURN"), cancellable = true)
    private void benito$expand(CallbackInfoReturnable<Box> cir) {
        if (BenitoClient.get() == null) return;
        CustomHitBox mod = BenitoClient.get().modules().get(CustomHitBox.class);
        if (mod == null || !mod.isEnabled()) return;

        Entity self = (Entity) (Object) this;
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        // Не трогаем собственного игрока — чтобы камера не сбивалась
        if (mc != null && self == mc.player) return;

        float e = mod.getExpand();
        Box b = cir.getReturnValue();
        if (b == null) return;
        cir.setReturnValue(b.expand(e, e, e));
    }
}
