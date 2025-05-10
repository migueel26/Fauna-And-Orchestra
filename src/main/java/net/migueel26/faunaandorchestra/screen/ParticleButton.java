package net.migueel26.faunaandorchestra.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleButton extends Button {
    private boolean pressed;

    public ParticleButton(int x, int y, Component message, OnPress onPress) {
        super(x, y, 11, 11, message, onPress, DEFAULT_NARRATION);
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public void press(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public void onPress() {
        press(!pressed);
        super.onPress();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ParticleButton.Icon icon;
        if (this.isMouseOver(mouseX, mouseY)) {
            icon = isPressed() ? Icon.PRESSED_HOVER : Icon.UNPRESSED_HOVER;
        } else {
            icon = isPressed() ? Icon.PRESSED : Icon.UNPRESSED;
        }

        RenderSystem.setShaderTexture(0, icon.sprite); // Bind texture
        guiGraphics.blit(icon.sprite, this.getX(), this.getY(), 0, 0, this.width, this.height, 11, 11);
    }

    @OnlyIn(Dist.CLIENT)
    static enum Icon {
        UNPRESSED(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/widget/particle_button.png")),
        UNPRESSED_HOVER(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/widget/particle_button_highlighted.png")),
        PRESSED(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/widget/particle_button_deactivated.png")),
        PRESSED_HOVER(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/widget/particle_button_deactivated_highlighted.png"));

        final ResourceLocation sprite;

        private Icon(ResourceLocation sprite) {
            this.sprite = sprite;
        }
    }
}
