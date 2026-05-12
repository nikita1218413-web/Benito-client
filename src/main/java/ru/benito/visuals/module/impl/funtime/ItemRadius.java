package ru.benito.visuals.module.impl.funtime;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;
import ru.benito.visuals.render.RenderUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ItemRadius — отображает радиус действия донат-предметов FunTime.
 *
 * Алгоритм детекции (упрощённый, без модификации сетевого стека):
 *   1) Каждый тик обходим всех игроков в мире.
 *   2) Смотрим main-hand и off-hand стэки.
 *   3) Если у предмета есть кастомное имя, сопоставляем его с таблицей DONATE_ITEMS
 *      (сервер FunTime обычно даёт уникальные имена донат-предметам типа "Жезл Грома", "Тотем призыва" и т.д.).
 *   4) Для распознанного предмета ставим кольцо/сферу вокруг игрока соответствующего радиуса.
 *
 * Рендер выполняется из Mixin в WorldRenderer#render (см. afterEntities-хук в InGameHudMixin нельзя,
 * поэтому в onHudRender мы только агрегируем таргеты, а сам 3D-рендер делает WorldRendererMixin —
 * в данной сборке упрощённо рисуем из HUD через матрицу мировой камеры).
 */
public final class ItemRadius extends Module {

    /** Таблица известных донат-предметов FunTime: "нижний регистр имени" -> радиус в блоках. */
    private static final Map<String, Double> DONATE_ITEMS = new HashMap<>();
    static {
        DONATE_ITEMS.put("жезл грома",     8.0);
        DONATE_ITEMS.put("жезл исцеления", 5.0);
        DONATE_ITEMS.put("тотем призыва",  6.0);
        DONATE_ITEMS.put("сфера защиты",   4.5);
        DONATE_ITEMS.put("посох мага",     7.0);
    }

    public enum Shape { CIRCLE, SPHERE }

    private Shape shape = Shape.CIRCLE;

    public ItemRadius() {
        super("ItemRadius", "ItemRadius",
                "Радиус действия донат-предметов", Category.FUNTIME);
    }

    /** Возвращает радиус донат-предмета для игрока или 0.0, если такого нет. */
    public double detectRadius(PlayerEntity player) {
        double r = matchRadius(player.getMainHandStack());
        if (r > 0) return r;
        return matchRadius(player.getOffHandStack());
    }

    private double matchRadius(ItemStack stack) {
        if (stack.isEmpty()) return 0.0;

        // Быстрый отсев — интересуют "особые" предметы (тотем/жезл/посох/сфера).
        // Грубо: палка/blaze rod / end rod / тотем — потенциальный донат-каст.
        if (!(stack.isOf(Items.STICK)
                || stack.isOf(Items.BLAZE_ROD)
                || stack.isOf(Items.END_ROD)
                || stack.isOf(Items.TOTEM_OF_UNDYING)
                || stack.isOf(Items.BREEZE_ROD)
                || stack.isOf(Items.NETHER_STAR)
                || stack.isOf(Items.HEART_OF_THE_SEA))) {
            return 0.0;
        }

        // Имя предмета из CUSTOM_NAME (1.21.4 — DataComponents).
        var name = stack.getName();
        if (name == null) return 0.0;
        String plain = name.getString().toLowerCase();
        for (Map.Entry<String, Double> e : DONATE_ITEMS.entrySet()) {
            if (plain.contains(e.getKey())) return e.getValue();
        }
        return 0.0;
    }

    /**
     * Вызывается из WorldRendererMixin после отрисовки сущностей.
     * В этом классе просто экспортируем рендер-метод, чтобы не тянуть Mixin зависимости.
     */
    public void renderWorld() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        int color = 0x806B4CFF;
        for (PlayerEntity p : mc.world.getPlayers()) {
            double r = detectRadius(p);
            if (r <= 0) continue;
            Vec3d c = p.getPos();
            if (shape == Shape.CIRCLE) RenderUtils.drawWorldCircle(c, r, 64, color);
            else                        RenderUtils.drawWorldSphere(c, r, 8, color);
        }
    }

    public Shape getShape()          { return shape; }
    public void  setShape(Shape s)   { this.shape = s; }
}
