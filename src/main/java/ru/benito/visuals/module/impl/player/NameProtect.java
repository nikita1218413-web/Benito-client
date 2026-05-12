package ru.benito.visuals.module.impl.player;

import net.minecraft.client.MinecraftClient;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * NameProtect — заменяет собственный никнейм на "Benito User" для стрима/скринов.
 * Замена выполняется в таблице имен PlayerEntityRendererMixin и в чате/хот-барах
 * через утилиту {@link #protect(String)}.
 */
public final class NameProtect extends Module {

    private String replacement = "Benito User";

    public NameProtect() {
        super("NameProtect", "NameProtect",
                "Замена ника на Benito User", Category.PLAYER);
    }

    /** Заменяет реальный ник игрока на сконфигурированный, если модуль включён. */
    public String protect(String raw) {
        if (!isEnabled() || raw == null) return raw;
        String self = ownName();
        if (self == null || self.isBlank()) return raw;
        return raw.replace(self, replacement);
    }

    private String ownName() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.player == null ? null : mc.player.getGameProfile().getName();
    }

    public String getReplacement() { return replacement; }
    public void setReplacement(String r) { this.replacement = r == null ? "Benito User" : r; }
}
