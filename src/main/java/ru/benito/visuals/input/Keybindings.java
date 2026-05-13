package ru.benito.visuals.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.module.Module;
import ru.benito.visuals.module.impl.funtime.CoordInvite;

/**
 * Хоткеи мода. GUI и прочие глобальные бинды.
 */
public final class Keybindings {

    // INSERT — по умолчанию, т.к. Right Shift часто конфликтует со sneak
    public static final KeyBinding OPEN_GUI = new KeyBinding(
            "key.benito.gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_INSERT,
            "category.benito"
    );

    public static final KeyBinding COORD_INVITE = new KeyBinding(
            "key.benito.coords",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            "category.benito"
    );

    private Keybindings() {}

    public static void register() {
        KeyBindingHelper.registerKeyBinding(OPEN_GUI);
        KeyBindingHelper.registerKeyBinding(COORD_INVITE);
    }

    /**
     * Обрабатывает динамические бинды модулей (setBind) каждый клиентский тик.
     */
    public static void handle(MinecraftClient mc) {
        if (mc.currentScreen != null) return;

        // Фикс-бинд CoordInvite
        if (COORD_INVITE.wasPressed()) {
            Module m = BenitoClient.get().modules().getByName("CoordInvite");
            if (m instanceof CoordInvite ci && m.isEnabled()) {
                ci.sendCoords();
            }
        }

        // Динамические бинды модулей (toggle по нажатию)
        long handle = mc.getWindow().getHandle();
        for (Module module : BenitoClient.get().modules().getModules()) {
            int key = module.getBind();
            if (key <= 0) continue;
            if (GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS) {
                if (!module.isKeyHeld()) {
                    module.toggle();
                    module.setKeyHeld(true);
                }
            } else {
                module.setKeyHeld(false);
            }
        }
    }
}
