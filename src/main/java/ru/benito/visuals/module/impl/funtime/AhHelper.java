package ru.benito.visuals.module.impl.funtime;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import ru.benito.visuals.module.Category;
import ru.benito.visuals.module.Module;
import ru.benito.visuals.render.RenderUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AhHelper — помощник для аукциона FunTime.
 *
 * Алгоритм:
 *  - При открытом AH-контейнере (GenericContainerScreen) обходим все слоты.
 *  - Читаем имя и LORE-компонент предмета.
 *  - Ищем число после слов "Цена"/"Price" — это стоимость лота.
 *  - Считаем цену за штуку = price / stack.count.
 *  - Если "per-unit" цена лота ниже среднего по окну на 30%+ — помечаем "выгодой"
 *    и показываем в HUD-плашке 10 секунд.
 */
public final class AhHelper extends Module {

    private static final Pattern PRICE = Pattern.compile("(\\d[\\d\\s.,']*)");

    private String lastDeal = "";
    private long lastDealTime = 0;

    public AhHelper() {
        super("AhHelper", "AhHelper",
                "Помощник аукциона", Category.FUNTIME);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) return;
        analyze(screen);
    }

    private void analyze(HandledScreen<?> screen) {
        long best = Long.MAX_VALUE;
        String bestName = null;
        long total = 0;
        int count = 0;

        for (Slot slot : screen.getScreenHandler().slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            Long price = extractPrice(stack.getName());
            if (price == null) price = extractFromLore(stack);
            if (price == null || price <= 0) continue;

            long perUnit = price / Math.max(1, stack.getCount());
            total += perUnit;
            count++;
            if (perUnit < best) {
                best = perUnit;
                bestName = stack.getName().getString();
            }
        }

        if (count == 0 || bestName == null) return;
        long avg = total / count;
        if (best < avg * 0.7) {
            lastDeal = bestName + " — " + best + " (средн: " + avg + ")";
            lastDealTime = System.currentTimeMillis();
        }
    }

    private Long extractFromLore(ItemStack stack) {
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore == null) return null;
        for (Text line : lore.lines()) {
            Long p = extractPrice(line);
            if (p != null) return p;
        }
        return null;
    }

    private Long extractPrice(Text text) {
        if (text == null) return null;
        String s = text.getString().toLowerCase();
        if (s.contains("цена") || s.contains("price") || s.contains("стоимость")) {
            Matcher m = PRICE.matcher(s);
            if (m.find()) {
                String digits = m.group(1).replaceAll("[\\s.,']", "");
                try { return Long.parseLong(digits); } catch (Exception ignored) {}
            }
        }
        return null;
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        if (lastDeal.isEmpty() || System.currentTimeMillis() - lastDealTime > 10_000) return;
        int x = 6, y = 6;
        int w = RenderUtils.textWidth("AH: " + lastDeal) + 8;
        RenderUtils.rect(ctx, x, y, w, 14, 0xC0000000, 0xFF6B4CFF);
        RenderUtils.text(ctx, "§eAH: §f" + lastDeal, x + 4, y + 3, 0xFFFFFFFF, true);
    }
}
