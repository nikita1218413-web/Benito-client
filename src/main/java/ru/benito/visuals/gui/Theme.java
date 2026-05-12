package ru.benito.visuals.gui;

/**
 * Тема оформления (акцент, фон).
 * Цвета — ARGB int. Изменяется через ThemeMenu.
 */
public final class Theme {

    // Значения по умолчанию: насыщенный сине-фиолетовый акцент + тёмный фон
    private int accent     = 0xFF6B4CFF;
    private int background = 0xE01A1A22;

    public int getAccent()     { return accent; }
    public int getBackground() { return background; }

    public void setAccent(int argb)     { this.accent = argb; }
    public void setBackground(int argb) { this.background = argb; }

    /** Полу-прозрачный вариант акцента для заливки. */
    public int getAccentFill(int alpha) {
        return (alpha << 24) | (accent & 0x00FFFFFF);
    }

    /** Цвет окантовки (слегка светлее акцента). */
    public int getAccentBorder() {
        return 0xFFFFFFFF & accent;
    }

    /** Цвет текста по умолчанию (белый). */
    public int getTextColor() {
        return 0xFFFFFFFF;
    }
}
