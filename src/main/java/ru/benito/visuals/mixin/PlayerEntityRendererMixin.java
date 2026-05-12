package ru.benito.visuals.mixin;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.player.NameProtect;

/**
 * NameProtect — подменяет отображаемое имя локального игрока на "Benito User".
 * Перехватываем первый аргумент у renderLabelIfPresent и меняем Text.
 */
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @ModifyArg(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
            index = 1)
    private Text benito$protectName(Text original) {
        if (BenitoClient.get() == null) return original;
        NameProtect np = BenitoClient.get().modules().get(NameProtect.class);
        if (np == null || !np.isEnabled()) return original;
        return Text.literal(np.protect(original.getString()));
    }
}
