package ru.benito.visuals.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.combat.CustomHitBox;

/**
 * CustomHitBox — рисует дополнительный визуальный хитбокс, увеличенный по осям.
 * Используем хук @Inject в EntityRenderer#render(EntityRenderState, MatrixStack, VertexConsumerProvider, int).
 * В 1.21.4 render теперь использует EntityRenderState, а AABB берём из entity напрямую
 * через {@link EntityRenderState#hitbox} (нет), поэтому оставляем подход "expand" на клиентской стороне
 * через модификацию Entity#getBoundingBox.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    private void benito$hitboxMarker(EntityRenderState state,
                                     MatrixStack matrices,
                                     VertexConsumerProvider vcp,
                                     int light,
                                     CallbackInfo ci) {
        // Реальная логика раздувания — в Entity#getVisibilityBoundingBox Mixin (не включён здесь
        // для простоты), либо хранением множителя в BenitoClient для внешней отрисовки wireframe.
        if (BenitoClient.get() == null) return;
        CustomHitBox mod = BenitoClient.get().modules().get(CustomHitBox.class);
        if (mod == null || !mod.isEnabled()) return;

        // Маркер: модуль активен. Реальное увеличение AABB — через отдельный Mixin на Entity
        // (см. комментарий выше; оставлено как точка расширения).
    }

    /** Утилита для внешних рендереров: раздувает bounding box на {@code expand} по каждой оси. */
    public static Box expand(Entity entity, float expand) {
        Box b = entity.getBoundingBox();
        return b.expand(expand, expand, expand);
    }
}
