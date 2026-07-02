package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.screen.PaperEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EditBox.class)
public abstract class MixinEditBox {

    @Redirect(
            method = "renderWidget",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I")
    )
    private int removeShadowFromText(GuiGraphics instance, Font font, FormattedCharSequence text, int x, int y, int color) {
        if ((Object) this instanceof PaperEditBox) {
            // Llamamos a la versión del método que acepta 'dropShadow' y le pasamos false
            return instance.drawString(font, text, x, y, color, false);
        }
        return instance.drawString(font, text, x, y, color, true);
    }

    @Redirect(
            method = "renderWidget",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I")
    )
    private int removeShadowFromCursor(GuiGraphics instance, Font font, String text, int x, int y, int color) {
        if ((Object) this instanceof PaperEditBox) {
            return instance.drawString(font, text, x, y, color, false);
        }
        return instance.drawString(font, text, x, y, color, true);
    }
}