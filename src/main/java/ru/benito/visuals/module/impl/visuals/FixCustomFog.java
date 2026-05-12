package ru.benito.visuals.module.impl.visuals;

import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * FixCustomFog — отключает кастомный серверный туман для лучшей видимости.
 * Логика работы — в BackgroundRendererMixin:
 *   при включённом модуле применяется максимальное расстояние и отключается
 *   установка "тонкого" тумана.
 */
public final class FixCustomFog extends Module {

    public FixCustomFog() {
        super("FixCustomFog", "FixCustomFog",
                "Убирает кастомный туман", Category.VISUALS);
    }
}
