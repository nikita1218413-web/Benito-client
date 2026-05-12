package ru.benito.visuals.module.impl.combat;

import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * CustomHitBox — увеличивает визуальный хитбокс сущностей.
 * Активирует доп. Render Layer через EntityRendererMixin#render (after HEAD).
 * Значение expand — полунабухание AABB.
 */
public final class CustomHitBox extends Module {

    /** Насколько визуально "раздуть" хитбокс по каждой оси. */
    private float expand = 0.30f;

    public CustomHitBox() {
        super("CustomHitBox", "CustomHitBox",
                "Визуальное расширение хитбоксов", Category.COMBAT);
    }

    public float getExpand() { return expand; }
    public void  setExpand(float v) { this.expand = Math.max(0f, Math.min(1.5f, v)); }
}
