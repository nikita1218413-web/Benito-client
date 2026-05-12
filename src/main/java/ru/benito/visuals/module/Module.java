package ru.benito.visuals.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.benito.visuals.BenitoClient;

/**
 * Базовый класс всех модулей.
 * Технические поля — на английском, пользовательские тексты — на русском.
 */
public abstract class Module {

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;        // Техническое имя (используется для конфига/поиска)
    private final String displayName; // Отображаемое имя (русское)
    private final String description; // Русское описание
    private final Category category;

    private boolean enabled = false;
    private int bind = -1;            // GLFW keycode, -1 = не задан
    private boolean keyHeld = false;

    protected Module(String name, String displayName, String description, Category category) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.category = category;
    }

    /* ---------- Lifecycle ---------- */

    public final void toggle() { setEnabled(!enabled); }

    public final void setEnabled(boolean value) {
        if (this.enabled == value) return;
        this.enabled = value;
        try {
            if (value) onEnable(); else onDisable();
        } catch (Throwable t) {
            // Модуль не должен валить клиент
            BenitoClient.get();
            t.printStackTrace();
        }
        notifyToggle();
    }

    protected void onEnable()  {}
    protected void onDisable() {}

    /** Вызывается каждый клиентский тик, если модуль включён. */
    public void onTick() {}

    /** Вызывается при рендере HUD. */
    public void onHudRender(DrawContext ctx, float tickDelta) {}

    /* ---------- Notifications ---------- */

    protected void notifyToggle() {
        if (mc.player == null) return;
        String key = enabled ? "benito.msg.enabled" : "benito.msg.disabled";
        mc.player.sendMessage(Text.translatable(key, displayName), true);
    }

    /* ---------- Getters / Setters ---------- */

    public String   getName()        { return name; }
    public String   getDisplayName() { return displayName; }
    public String   getDescription() { return description; }
    public Category getCategory()    { return category; }
    public boolean  isEnabled()      { return enabled; }
    public int      getBind()        { return bind; }
    public void     setBind(int key) { this.bind = key; }
    public boolean  isKeyHeld()      { return keyHeld; }
    public void     setKeyHeld(boolean v) { this.keyHeld = v; }
}
