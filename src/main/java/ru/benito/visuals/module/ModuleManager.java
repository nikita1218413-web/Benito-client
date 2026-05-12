package ru.benito.visuals.module;

import net.minecraft.client.gui.DrawContext;
import ru.benito.visuals.module.impl.combat.CustomHitBox;
import ru.benito.visuals.module.impl.combat.HitEffect;
import ru.benito.visuals.module.impl.combat.ShiftTap;
import ru.benito.visuals.module.impl.combat.TotemTracker;
import ru.benito.visuals.module.impl.funtime.AhHelper;
import ru.benito.visuals.module.impl.funtime.CoordInvite;
import ru.benito.visuals.module.impl.funtime.ItemRadius;
import ru.benito.visuals.module.impl.player.NameProtect;
import ru.benito.visuals.module.impl.visuals.ArmorHud;
import ru.benito.visuals.module.impl.visuals.AspectRatio;
import ru.benito.visuals.module.impl.visuals.FixCustomFog;
import ru.benito.visuals.module.impl.visuals.ViewModel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Регистрация и диспетчеризация всех модулей.
 */
public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private final Map<Category, List<Module>> byCategory = new EnumMap<>(Category.class);

    public ModuleManager() {
        // Combat
        register(new ShiftTap());
        register(new CustomHitBox());
        register(new HitEffect());
        register(new TotemTracker());

        // FunTime
        register(new ItemRadius());
        register(new AhHelper());
        register(new CoordInvite());

        // Visuals
        register(new ArmorHud());
        register(new ViewModel());
        register(new FixCustomFog());
        register(new AspectRatio());

        // Player
        register(new NameProtect());
    }

    private void register(Module m) {
        modules.add(m);
        byCategory.computeIfAbsent(m.getCategory(), c -> new ArrayList<>()).add(m);
    }

    public List<Module> getModules() { return modules; }

    public List<Module> getByCategory(Category c) {
        return byCategory.getOrDefault(c, List.of());
    }

    public Module getByName(String name) {
        for (Module m : modules) if (m.getName().equalsIgnoreCase(name)) return m;
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> type) {
        for (Module m : modules) if (type.isInstance(m)) return (T) m;
        return null;
    }

    public void onTick() {
        for (Module m : modules) if (m.isEnabled()) m.onTick();
    }

    public void onHudRender(DrawContext ctx, float tickDelta) {
        for (Module m : modules) if (m.isEnabled()) m.onHudRender(ctx, tickDelta);
    }
}
