package net.migueel26.faunaandorchestra.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ConductorScreen extends AbstractContainerScreen<ConductorMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/conductor/conductor_gui.png");
    private final ConductorEntity conductor;
    private float xMouse;
    private float yMouse;
    public ConductorScreen(ConductorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.conductor = menu.conductor;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x , y, 0,0, imageWidth, imageHeight);

        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x + 26, y + 22, x + 78, y + 70, 30, 0.25F,
                this.xMouse, this.yMouse, this.conductor);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.xMouse = (float)mouseX;
        this.yMouse = (float)mouseY;

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
