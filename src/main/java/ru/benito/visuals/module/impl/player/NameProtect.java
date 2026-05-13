package ru.benito.visuals.module.impl.player;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * NameProtect — заменяет собственный никнейм на "Benito User" в ИСХОДЯЩИХ
 * чат-сообщениях и командах (то, что ты пишешь — не увидят реальный ник).
 *
 * Регистрация MODIFY_CHAT / MODIFY_COMMAND — один раз в конструкторе.
 */
public final class NameProtect extends Module {

    private String replacement = "Benito User";

    public NameProtect() {
        super("NameProtect", "NameProtect",
                "Замена своего ника в исходящих сообщениях", Category.PLAYER);

        ClientSendMessageEvents.MODIFY_CHAT.register(msg -> {
            if (!isEnabled()) return msg;
            return protect(msg);
        });
        ClientSendMessageEvents.MODIFY_COMMAND.register(cmd -> {
            if (!isEnabled()) return cmd;
            return protect(cmd);
        });
    }

    public String protect(String raw) {
        if (raw == null || raw.isEmpty()) return raw;
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
