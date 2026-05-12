package ru.benito.visuals.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.impl.funtime.ItemRadius;

/**
 * WorldRenderer hook — точка отрисовки мировых оверлеев (радиус донат-предметов).
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V",
                    shift = At.Shift.BEFORE),
            require = 0)
    private void benito$renderOverlays(net.minecraft.client.util.ObjectAllocator allocator,
                                       net.minecraft.client.render.RenderTickCounter tickCounter,
                                       boolean renderBlockOutline,
                                       net.minecraft.client.render.Camera camera,
                                       GameRenderer gameRenderer,
                                       LightmapTextureManager lightmapTextureManager,
                                       Matrix4f positionMatrix,
                                       Matrix4f projectionMatrix,
                                       CallbackInfo ci) {
        if (BenitoClient.get() == null) return;
        ItemRadius ir = BenitoClient.get().modules().get(ItemRadius.class);
        if (ir != null && ir.isEnabled()) ir.renderWorld();
    }
}
