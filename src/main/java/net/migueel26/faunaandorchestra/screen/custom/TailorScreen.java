package net.migueel26.faunaandorchestra.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.networking.packets.TailorKoalaStartSewingC2SPacket;
import net.migueel26.faunaandorchestra.recipe.ModRecipes;
import net.migueel26.faunaandorchestra.recipe.SewingRecipe;
import net.migueel26.faunaandorchestra.screen.ClientRecipeItemsTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TailorScreen extends AbstractContainerScreen<TailorMenu> {
    private static final int CATALOG_SIZE = 30;
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/tailor_gui.png");
    private static final ResourceLocation VILLAGER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/villager2.png");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLL_BAR_HEIGHT = 139;
    private static final int SCROLL_BAR_TOP_POS_Y = 18;
    private static final int SCROLL_BAR_START_X = 94;
    private static final Component CATALOG_LABEL = Component.translatable("entity.faunaandorchestra.tailor_koala.catalog");
    private final TailorKoalaEntity tailor;
    private float xMouse;
    private float yMouse;
    private int scrollOff;
    private int catalogSize;
    private final CatalogOfferButton[] catalogOfferButtons = new CatalogOfferButton[CATALOG_SIZE];
    public TailorScreen(TailorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.tailor = menu.tailor;

        this.imageWidth = 276;
    }

    @Override
    protected void init() {
        super.init();
        ClientLevel level = Minecraft.getInstance().level;

        if (level != null) {
            catalogSize = 0;
            for (var recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.SEWING_TYPE.get())) {
                if (isAptRecipe(recipe)) {
                    this.catalogOfferButtons[catalogSize] = this.addRenderableWidget(
                            new CatalogOfferButton(leftPos + 5, 0, catalogSize, recipe, button -> {
                                ModNetwork.sendToServer(new TailorKoalaStartSewingC2SPacket(tailor.getUUID(), true, recipe.output()));
                            }));
                    catalogSize++;
                }
            }
        }

    }

    public boolean isAptRecipe(SewingRecipe recipe) {
        ItemStack item = recipe.output();
        if (item.is(ModItems.FLORAL_BOOTS.get())) {
            return (tailor.getLearntRecipes() & 1) != 0;
        }
        return true;
    }

    private void renderScroller(GuiGraphics guiGraphics, int posX, int posY) {
        int i = catalogSize + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }

            guiGraphics.blit(VILLAGER_TEXTURE, posX + 94, posY + 18 + i1, 0.0F, 199.0F, 6, 27, 512, 256);
        } else {
            guiGraphics.blit(VILLAGER_TEXTURE, posX + 94, posY + 18, 6.0F, 199.0F, 6, 27, 512, 256);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x , y, 0,0, 0, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        int entityX = x + 233;
        int entityY = y + 80;

        guiGraphics.enableScissor(x + 207, y + 16, x + 259, y + 70);

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                guiGraphics,
                entityX,
                entityY,
                50,
                (float) entityX - this.xMouse,
                (float) (entityY - 50) - this.yMouse,
                this.tailor
        );

        guiGraphics.disableScissor();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = this.leftPos;
        int j = this.topPos;
        this.xMouse = (float)mouseX;
        this.yMouse = (float)mouseY;

        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (getCatalogSize() > 0) {
            this.renderScroller(guiGraphics, i, j);

            // Max visible buttons
            int visibleSlots = 7;
            int l = i + 6;

            for (int index = 0; index < catalogSize; index++) {
                CatalogOfferButton catalogButton = this.catalogOfferButtons[index];

                if (index >= this.scrollOff && index < this.scrollOff + visibleSlots) {
                    // If the button is visible (within first 7)
                    int relativeIndex = index - this.scrollOff;
                    int j1 = j + 16 + 2 + (relativeIndex * 20);

                    catalogButton.setY(j1);
                    catalogButton.visible = true;

                    SewingRecipe recipe = catalogButton.recipe;
                    ItemStack itemstack = recipe.output().copy();

                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
                    guiGraphics.renderFakeItem(itemstack, l + 2, j1 + 1);
                    guiGraphics.drawString(font, font.substrByWidth(itemstack.getHoverName(), 64).getString(), l + 22, j1 + 6, 0xffffff);

                    guiGraphics.pose().popPose();

                    if (catalogButton.isHoveredOrFocused()) {
                        catalogButton.renderToolTip(guiGraphics, mouseX, mouseY);
                    }
                } else {
                    catalogButton.visible = false;
                }
            }

            RenderSystem.enableDepthTest();
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        int i = catalogSize;

        if (this.canScroll(i)) {
            int j = i - 7;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - scrollDelta), 0, j);
        }

        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int i = catalogSize;
        if (this.isDragging()) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(catalogSize) && mouseX > (double)(i + 94) && mouseX < (double)(i + 94 + 6) && mouseY > (double)(j + 18) && mouseY <= (double)(j + 18 + 139 + 1)) {
            this.setDragging(true);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 94 + this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 4210752, false);
        int l = this.font.width(CATALOG_LABEL);
        guiGraphics.drawString(this.font, CATALOG_LABEL, 5 - l / 2 + 48, 6, 4210752, false);
    }

    public int getCatalogSize() {
        return catalogSize;
    }

    private boolean canScroll(int numOffers) {
        return numOffers > 7;
    }

    @OnlyIn(Dist.CLIENT)
    class CatalogOfferButton extends Button {
        private final SewingRecipe recipe;
        private final int index;

        public CatalogOfferButton(int x, int y, int index, SewingRecipe recipe, OnPress onPress) {
            super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
            this.recipe = recipe;
        }

        public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (this.isHovered && this.visible) {
                List<ItemStack> requiredItems = new ArrayList<>();

                for (var ingredient : this.recipe.ingredients()) {
                    ItemStack displayStack = ingredient.ingredient().getItems()[0].copy();
                    displayStack.setCount(ingredient.amount());
                    requiredItems.add(displayStack);
                }

                if (!requiredItems.isEmpty()) {
                    guiGraphics.renderTooltip(
                            Minecraft.getInstance().font,
                            List.of(Component.translatable("entity.faunaandorchestra.tailor_koala.required_items")),
                            Optional.of(new ClientRecipeItemsTooltip.RecipeItemsTooltip(requiredItems)),
                            mouseX, mouseY
                    );
                }
            }
        }

        public int getIndex() {
            return this.index;
        }
    }
}
