package ru.benito.visuals.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benito.visuals.gui.UpdateMainMenu;

/**
 * Патч главного меню:
 *   - добавляет кнопки "Зайти на FunTime" и "Настройки Benito" на init()
 *   - рисует брендинг Benito поверх стандартного меню
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen implements UpdateMainMenu.ScreenAccess {

    protected TitleScreenMixin() {
        super(null);
    }

    @Shadow
    protected abstract <T extends Element & net.minecraft.client.gui.Drawable & net.minecraft.client.gui.Selectable>
            T addDrawableChild(T widget);

    @Inject(method = "init", at = @At("TAIL"))
    private void benito$init(CallbackInfo ci) {
        UpdateMainMenu.inject((TitleScreen) (Object) this);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void benito$renderBrand(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        UpdateMainMenu.renderBrand(ctx, this.width, this.height);
    }

    @Override
    public void benito$addDrawable(ClickableWidget widget) {
        this.addDrawableChild(widget);
    }
}
