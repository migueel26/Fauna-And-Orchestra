package net.migueel26.faunaandorchestra.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class PaperEditBox extends EditBox {
    // Forge only (see Mixin)
    public PaperEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }
}
