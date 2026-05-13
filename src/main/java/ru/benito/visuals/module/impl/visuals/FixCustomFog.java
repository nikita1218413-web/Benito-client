package ru.benito.visuals.module.impl.visuals;

import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * FixCustomFog — убирает/сильно уменьшает кастомный туман.
 * Вся логика — в BackgroundRendererMixin (подмена результата applyFog).
 */
public final class FixCustomFog extends Module {

    public FixCustomFog() {
        super("FixCustomFog", "FixCustomFog",
                "Убирает кастомный туман", Category.VISUALS);
    }
}
