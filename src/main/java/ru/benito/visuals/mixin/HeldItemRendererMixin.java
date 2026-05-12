package ru.benito.visuals.mixin;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.visuals.ViewModel;

/**
 * ViewModel — сдвигает руки по X/Y/Z перед рендером предмета в руке.
 */
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Inject(method = "renderFirstPersonItem",
            at = @At("HEAD"))
    private void benito$applyOffset(net.minecraft.client.network.AbstractClientPlayerEntity player,
                                    float tickDelta,
                                    float pitch,
                                    net.minecraft.util.Hand hand,
                                    float swingProgress,
                                    net.minecraft.item.ItemStack item,
                                    float equipProgress,
                                    MatrixStack matrices,
                                    net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
                                    int light,
                                    CallbackInfo ci) {
        if (BenitoClient.get() == null) return;
        ViewModel vm = BenitoClient.get().modules().get(ViewModel.class);
        if (vm == null || !vm.isEnabled()) return;
        matrices.translate(vm.getX(), vm.getY(), vm.getZ());
    }
}
