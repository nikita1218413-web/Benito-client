package ru.benito.visuals.module.impl.visuals;

import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * ViewModel — сдвиг позиции первой руки (held item).
 * Применяется в HeldItemRendererMixin до applyEquipOffset.
 * Значения — смещения в "модельных" единицах (1 = 16 пикселей).
 */
public final class ViewModel extends Module {

    private float x = 0.0f;
    private float y = 0.0f;
    private float z = 0.0f;

    public ViewModel() {
        super("ViewModel", "ViewModel",
                "Позиция рук (X / Y / Z)", Category.VISUALS);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }

    public void setX(float v) { this.x = clamp(v); }
    public void setY(float v) { this.y = clamp(v); }
    public void setZ(float v) { this.z = clamp(v); }

    private static float clamp(float v) { return Math.max(-2f, Math.min(2f, v)); }
}
