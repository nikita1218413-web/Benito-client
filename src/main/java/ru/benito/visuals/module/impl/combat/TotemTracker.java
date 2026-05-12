package ru.benito.visuals.module.impl.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

import java.util.HashMap;
import java.util.Map;

/**
 * TotemTracker — отслеживает использование Totem of Undying другими игроками.
 * Логика "сработал тотем":
 *   1) Игрок держал тотем в off-hand или main-hand в прошлом тике.
 *   2) Сейчас он жив, но у него полное HP, а тотема больше нет.
 * Точное событие сложно перехватить без доп. packet-хуков, поэтому ориентируемся
 * на изменение инвентаря + статус Particle (TotemParticleS2CPacket) не нужен — достаточно стате.
 */
public final class TotemTracker extends Module {

    private final Map<Integer, Boolean> lastHadTotem = new HashMap<>();
    private final Map<Integer, Boolean> lastWasEnchanted = new HashMap<>();

    public TotemTracker() {
        super("TotemTracker", "TotemTracker",
                "Логирование капнувших тотемов", Category.COMBAT);
    }

    @Override
    protected void onDisable() {
        lastHadTotem.clear();
        lastWasEnchanted.clear();
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;
            int id = p.getId();

            ItemStack off = p.getOffHandStack();
            ItemStack main = p.getMainHandStack();
            boolean hasTotem = off.isOf(Items.TOTEM_OF_UNDYING) || main.isOf(Items.TOTEM_OF_UNDYING);
            boolean enchanted = isEnchanted(off) || isEnchanted(main);

            Boolean had = lastHadTotem.get(id);
            if (had != null && had && !hasTotem && isAlive(p) && isHealed(p)) {
                boolean wasEnch = Boolean.TRUE.equals(lastWasEnchanted.get(id));
                String key = wasEnch ? "benito.msg.totem.enchanted" : "benito.msg.totem.regular";
                mc.inGameHud.getChatHud().addMessage(Text.translatable(key, p.getGameProfile().getName()));
            }

            lastHadTotem.put(id, hasTotem);
            if (hasTotem) lastWasEnchanted.put(id, enchanted);
        }
    }

    private boolean isEnchanted(ItemStack stack) {
        if (!stack.isOf(Items.TOTEM_OF_UNDYING)) return false;
        ItemEnchantmentsComponent comp = stack.get(DataComponentTypes.ENCHANTMENTS);
        return comp != null && !comp.isEmpty();
    }

    private boolean isAlive(LivingEntity e) {
        return !e.isDead() && e.getHealth() > 0f;
    }

    private boolean isHealed(LivingEntity e) {
        return e.getHealth() >= 1.0f && e.getHealth() <= e.getMaxHealth();
    }
}
