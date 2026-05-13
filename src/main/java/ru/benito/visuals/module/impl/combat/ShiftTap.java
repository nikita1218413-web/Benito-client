package ru.benito.visuals.module.impl.combat;

import ru.benito.visuals.event.Events;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * ShiftTap — при ударе кратко "отжимает" Shift для сброса KB-резиста.
 */
public final class ShiftTap extends Module {

    private int releaseTicks = 0;

    public ShiftTap() {
        super("ShiftTap", "ShiftTap",
                "Автоматическое отжатие Shift при ударе", Category.COMBAT);

        // Регистрируем ОДИН раз — избегаем дублирования листенеров
        Events.ATTACK_ENTITY.register((target, crit) -> {
            if (!isEnabled()) return;
            releaseTicks = 2;
        });
    }

    @Override
    public void onTick() {
        if (releaseTicks > 0) releaseTicks--;
    }

    /** Используется ClientPlayerEntityMixin. НЕ вызываем isSneaking()
     *  изнутри — это привело бы к StackOverflow через наш же хук. */
    public boolean shouldForceUnsneak() {
        return releaseTicks > 0;
    }
}
