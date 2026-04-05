package net.migueel26.faunaandorchestra.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.components.Button;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;

import java.awt.*;

public class MailboxScreen extends AbstractContainerScreen<MailboxMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/block/mailbox_gui.png");
    private ExtendedButton sendButton;

    public MailboxScreen(MailboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = 76;
        this.titleLabelY = 4;
    }

    @Override
    protected void init() {

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.sendButton = new ExtendedButton(x + 66, y + 53, 54, 15, Component.translatable("block.faunaandorchestra.mailbox.send"), onPress -> {

        });

        this.addRenderableWidget(sendButton);
        super.init();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        //guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        //guiGraphics.drawString(this.font, menu.blockEntity.getDisplayName(), this.titleLabelX + 1, this.titleLabelY + 1, 0x6B532F, true);
        //guiGraphics.drawString(this.font, menu.blockEntity.getDisplayName(), this.titleLabelX, this.titleLabelY, 0x312615, false);
        guiGraphics.drawString(this.font, menu.blockEntity.getDisplayName(), this.titleLabelX, this.titleLabelY, 0xffffff, true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
