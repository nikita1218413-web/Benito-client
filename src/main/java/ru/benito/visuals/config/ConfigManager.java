package ru.benito.visuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import ru.benito.visuals.BenitoMod;
import ru.benito.visuals.gui.Theme;
import ru.benito.visuals.module.Module;
import ru.benito.visuals.module.ModuleManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Простое сохранение/загрузка состояний модулей и темы в JSON.
 * Путь: config/benito_visuals.json
 */
public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final ModuleManager moduleManager;
    private final Theme theme;
    private final Path file;

    public ConfigManager(ModuleManager moduleManager, Theme theme) {
        this.moduleManager = moduleManager;
        this.theme = theme;
        this.file = FabricLoader.getInstance()
                .getConfigDir()
                .resolve(BenitoMod.MOD_ID + ".json");
    }

    public void load() {
        if (!Files.exists(file)) return;
        try {
            String json = Files.readString(file);
            JsonObject root = GSON.fromJson(json, JsonObject.class);
            if (root == null) return;

            if (root.has("theme")) {
                JsonObject t = root.getAsJsonObject("theme");
                if (t.has("accent")) theme.setAccent(t.get("accent").getAsInt());
                if (t.has("background")) theme.setBackground(t.get("background").getAsInt());
            }

            if (root.has("modules")) {
                JsonObject mods = root.getAsJsonObject("modules");
                for (Module m : moduleManager.getModules()) {
                    if (!mods.has(m.getName())) continue;
                    JsonObject mj = mods.getAsJsonObject(m.getName());
                    if (mj.has("enabled")) m.setEnabled(mj.get("enabled").getAsBoolean());
                    if (mj.has("bind")) m.setBind(mj.get("bind").getAsInt());
                }
            }
        } catch (IOException e) {
            BenitoMod.LOGGER.warn("[Benito] Не удалось загрузить конфиг: {}", e.getMessage());
        }
    }

    public void save() {
        try {
            JsonObject root = new JsonObject();

            JsonObject t = new JsonObject();
            t.addProperty("accent", theme.getAccent());
            t.addProperty("background", theme.getBackground());
            root.add("theme", t);

            JsonObject mods = new JsonObject();
            for (Module m : moduleManager.getModules()) {
                JsonObject mj = new JsonObject();
                mj.addProperty("enabled", m.isEnabled());
                mj.addProperty("bind", m.getBind());
                mods.add(m.getName(), mj);
            }
            root.add("modules", mods);

            if (!Files.exists(file.getParent())) Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(root));
        } catch (IOException e) {
            BenitoMod.LOGGER.warn("[Benito] Не удалось сохранить конфиг: {}", e.getMessage());
        }
    }
}
