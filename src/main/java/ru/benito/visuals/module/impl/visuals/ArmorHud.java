package ru.benito.visuals.module.impl.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;
import ru.benito.visuals.render.RenderUtils;

/**
 * ArmorHud — отрисовывает процент прочности экипированной брони
 * над стандартным статус-баром (hotbar). Для 1.21.4 использует DrawContext
 * и API DataComponents (ItemStack#getMaxDamage, #getDamage).
 */
public final class ArmorHud extends Module {

    public ArmorHud() {
        super("ArmorHud", "ArmorHud",
                "Процент прочности брони", Category.VISUALS);
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();

        // Центр над хотбаром
        int baseX = sw / 2 - 91;
        int baseY = sh - 55;

        // 4 слота брони: HEAD, CHEST, LEGS, FEET
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = p.getEquippedStack(slots[i]);
            if (stack.isEmpty() || !stack.isDamageable()) continue;

            int percent = (int) Math.round(
                    (1.0 - (double) stack.getDamage() / stack.getMaxDamage()) * 100.0);
            percent = Math.max(0, Math.min(100, percent));

            int color = percent > 66 ? 0xFF59E060 : percent > 33 ? 0xFFF0D040 : 0xFFE05060;
            String s = percent + "%";

            int x = baseX + i * 20 + (20 - RenderUtils.textWidth(s)) / 2;
            RenderUtils.text(ctx, s, x, baseY, color, true);
        }
    }
}
