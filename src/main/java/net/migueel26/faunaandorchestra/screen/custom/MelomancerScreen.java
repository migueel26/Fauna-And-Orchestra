package net.migueel26.faunaandorchestra.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class MelomancerScreen extends AbstractContainerScreen<MelomancerMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/melomancer_gui.png");
    private final MelomancerKoalaEntity melomancer;
    private float xMouse;
    private float yMouse;
    public MelomancerScreen(MelomancerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 188;

        this.melomancer = menu.melomancer;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x , y, 0,0, imageWidth, imageHeight);

        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x + 26, y + 22, x + 78, y + 70, 50, 0.25F,
                this.xMouse, this.yMouse, this.melomancer);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.xMouse = (float)mouseX;
        this.yMouse = (float)mouseY;

        if (this.isHovering(6, 63, 15, 17, mouseX, mouseY)) {
            MutableComponent tooltip = Component.translatable("screen.faunaandorchestra.melomancer1")
                    .append(Component.literal("\n"))
                    .append(Component.translatable("screen.faunaandorchestra.melomancer2"))
                    .append(Component.literal("\n"))
                    .append(Component.translatable("screen.faunaandorchestra.melomancer3"))
                    .append(Component.literal("\n"))
                    .append(Component.translatable("screen.faunaandorchestra.melomancer4"));
            List<FormattedCharSequence> wrappedTooltip = font.split(tooltip, 200);

            guiGraphics.renderTooltip(font, wrappedTooltip, mouseX, mouseY);
        }

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
