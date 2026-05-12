package ru.benito.visuals.module.impl.funtime;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * CoordInvite — отправка своих координат в чат командой.
 * По умолчанию биндится на Y (см. {@link ru.benito.visuals.input.Keybindings}).
 */
public final class CoordInvite extends Module {

    /** Команда для отправки; "%s" для X/Y/Z вставляются в порядке. */
    private String template = "/msg %target% Мои координаты: %x% %y% %z%";
    private String target   = "";

    public CoordInvite() {
        super("CoordInvite", "CoordInvite",
                "Отправка координат другу", Category.FUNTIME);
    }

    public void sendCoords() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.getNetworkHandler() == null) return;

        BlockPos pos = p.getBlockPos();
        String msg;
        if (target.isBlank()) {
            msg = "Мои координаты: " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        } else {
            msg = template
                    .replace("%target%", target)
                    .replace("%x%", String.valueOf(pos.getX()))
                    .replace("%y%", String.valueOf(pos.getY()))
                    .replace("%z%", String.valueOf(pos.getZ()));
        }

        if (msg.startsWith("/")) {
            mc.getNetworkHandler().sendChatCommand(msg.substring(1));
        } else {
            mc.getNetworkHandler().sendChatMessage(msg);
        }
    }

    public String getTarget() { return target; }
    public void   setTarget(String t) { this.target = t == null ? "" : t; }
    public String getTemplate() { return template; }
    public void   setTemplate(String t) { this.template = t; }
}
