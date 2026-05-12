package ru.benito.visuals.module.impl.combat;

import net.minecraft.client.MinecraftClient;
import ru.benito.visuals.event.Events;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * ShiftTap — при нанесении удара кратковременно "отжимает" Shift,
 * чтобы сбросить knockback-резист от крадущегося режима.
 *
 * Реализация: слушаем событие ATTACK_ENTITY, запоминаем тик удара.
 * В onTick() в течение 2 тиков эмулируем "не-сникинг" через оверрайд поля
 * в ClientPlayerEntityMixin (см. методы get/set).
 */
public final class ShiftTap extends Module {

    private int releaseTicks = 0;

    public ShiftTap() {
        super("ShiftTap", "ShiftTap",
                "Автоматическое отжатие Shift при ударе", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        Events.ATTACK_ENTITY.register((target, crit) -> {
            if (!isEnabled()) return;
            releaseTicks = 2; // пропускаем sneaking 2 тика
        });
    }

    @Override
    public void onTick() {
        if (releaseTicks > 0) releaseTicks--;
    }

    /** Используется ClientPlayerEntityMixin → решает: нужно ли игнорить sneak. */
    public boolean shouldForceUnsneak() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return releaseTicks > 0 && mc.player != null && mc.player.isSneaking();
    }
}
