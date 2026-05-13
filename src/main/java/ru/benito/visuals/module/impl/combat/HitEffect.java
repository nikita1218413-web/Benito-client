package ru.benito.visuals.module.impl.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import ru.benito.visuals.event.Events;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;

/**
 * HitEffect — эффект "волны" частиц при критическом ударе.
 * Листенер регистрируется ОДИН раз в конструкторе — иначе каждый toggle
 * добавлял бы новый листенер → дублирование частиц.
 */
public final class HitEffect extends Module {

    public enum Style { PARTICLES, BLOCKS }

    private Style style = Style.PARTICLES;

    public HitEffect() {
        super("HitEffect", "HitEffect",
                "Эффект волны при критическом ударе", Category.COMBAT);

        Events.ATTACK_ENTITY.register((target, crit) -> {
            if (!isEnabled() || !crit) return;
            spawnWave(target);
        });
    }

    private void spawnWave(Entity target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        Vec3d origin = target.getPos();

        int rings = 16;
        double radius = 1.6;
        for (int i = 0; i < rings; i++) {
            double theta = (Math.PI * 2.0) * i / rings;
            double x = origin.x + Math.cos(theta) * radius;
            double z = origin.z + Math.sin(theta) * radius;
            double y = origin.y + 0.05;
            if (style == Style.PARTICLES) {
                mc.world.addParticle(ParticleTypes.CRIT, x, y, z, 0, 0.25, 0);
            } else {
                mc.world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 0, 0, 0);
            }
        }
    }

    public Style getStyle() { return style; }
    public void  setStyle(Style s) { this.style = s; }
}
