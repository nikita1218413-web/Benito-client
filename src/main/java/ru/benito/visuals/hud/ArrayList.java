package ru.benito.visuals.hud;

import net.minecraft.client.gui.DrawContext;
import ru.benito.visuals.BenitoClient;
import ru.benito.visuals.gui.Theme;
import ru.benito.visuals.module.Module;
import ru.benito.visuals.render.RenderUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ArrayList — правый верхний список включённых модулей.
 * Это главный визуальный индикатор для игрока — видно, что мод работает.
 */
public final class ArrayList {

    private ArrayList() {}

    public static void render(DrawContext ctx, Theme theme) {
        if (BenitoClient.get() == null) return;

        List<Module> active = BenitoClient.get().modules().getModules().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparingInt((Module m) -> -RenderUtils.textWidth(m.getDisplayName())))
                .collect(Collectors.toList());

        int sw = ctx.getScaledWindowWidth();
        int y = 2;
        int padX = 4;
        int rowH = RenderUtils.textHeight() + 2;

        // Заголовок "Benito"
        String head = "§l§dBenito§r §7Visuals";
        int hw = RenderUtils.textWidth(head);
        RenderUtils.fill(ctx, sw - hw - padX * 2, y, hw + padX * 2, rowH + 1, 0x80000000);
        RenderUtils.fill(ctx, sw - 2, y, 2, rowH + 1, theme.getAccent());
        RenderUtils.text(ctx, head, sw - hw - padX, y + 2, 0xFFFFFFFF, true);

        y += rowH + 2;

        for (Module m : active) {
            String txt = m.getDisplayName();
            int w = RenderUtils.textWidth(txt);
            int x = sw - w - padX * 2;
            RenderUtils.fill(ctx, x, y, w + padX * 2, rowH, 0x80000000);
            RenderUtils.fill(ctx, sw - 2, y, 2, rowH, theme.getAccent());
            RenderUtils.text(ctx, txt, sw - w - padX, y + 1, theme.getAccent(), true);
            y += rowH;
        }

        // Если нет активных — подсказка про INSERT
        if (active.isEmpty()) {
            String hint = "§7GUI: §eINSERT";
            int hwH = RenderUtils.textWidth(hint);
            RenderUtils.fill(ctx, sw - hwH - padX * 2, y, hwH + padX * 2, rowH, 0x80000000);
            RenderUtils.text(ctx, hint, sw - hwH - padX, y + 1, 0xFFFFFFFF, true);
        }
    }
}
