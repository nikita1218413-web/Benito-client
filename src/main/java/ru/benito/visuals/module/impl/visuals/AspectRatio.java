package ru.benito.visuals.module.impl.visuals;

import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * AspectRatio — меняет соотношение сторон экрана.
 * Включение принудительно пересчитывает размеры окна в WindowMixin.
 * ratio = width / height. 0 = использовать системное значение.
 */
public final class AspectRatio extends Module {

    private float ratio = 16f / 9f;

    public AspectRatio() {
        super("AspectRatio", "AspectRatio",
                "Соотношение сторон экрана", Category.VISUALS);
    }

    public float getRatio() { return ratio; }
    public void  setRatio(float r) { this.ratio = Math.max(1.0f, Math.min(3.5f, r)); }
}
