package net.migueel26.faunaandorchestra.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LetterAndQuillScreen extends AbstractContainerScreen<LetterAndQuillMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/block/letter_and_quill_gui.png");
    public static final int DEAREST_TEXTBOX_X = 62;
    public static final int DEAREST_TEXTBOX_Y = 5;
    public static final int DEAREST_TEXTBOX_WIDTH = 66;
    public static final int DEFAULT_TEXTBOX_HEIGHT = 10;
    public static final int SINCERELY_TEXTBOX_X = 69;
    public static final int SINCERELY_TEXTBOX_Y = 60;
    private static final int SINCERELY_TEXTBOX_WIDTH = 60;
    private static final int X_TEXTBOX_X = 28;
    private static final int Y_TEXTBOX_X = 64;
    private static final int Z_TEXTBOX_X = 102;
    private static final int COORDINATE_TEXTBOX_Y = 47;
    private static final int COORDINATE_TEXTBOX_WIDTH = 26;
    public final int initialX = 18;
    public final int myDearestY = 5;
    public final int sincerelyY = 60;
    public final int coordinateY = 47;
    public final int yCoordinateX = 54;
    public final int zCoordinateX = 92;
    protected EditBox myDearestText;
    private EditBox sincerelyText;
    private EditBox xText;
    private EditBox yText;
    private EditBox zText;
    protected EditBox[] textBoxes = new EditBox[5];


    public LetterAndQuillScreen(LetterAndQuillMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = 76;
        this.titleLabelY = 4;
        this.inventoryLabelY += 14;
        this.imageHeight = 182;
    }

    @Override
    protected void init() {

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.myDearestText = new EditBox(this.font, x + DEAREST_TEXTBOX_X, y + DEAREST_TEXTBOX_Y, DEAREST_TEXTBOX_WIDTH, DEFAULT_TEXTBOX_HEIGHT, Component.literal("Hello"));
        this.sincerelyText = new EditBox(this.font, x + SINCERELY_TEXTBOX_X, y + SINCERELY_TEXTBOX_Y, SINCERELY_TEXTBOX_WIDTH, DEFAULT_TEXTBOX_HEIGHT, Component.translatable("Hello,"));
        this.xText = new EditBox(this.font, x + X_TEXTBOX_X, y + COORDINATE_TEXTBOX_Y, COORDINATE_TEXTBOX_WIDTH, DEFAULT_TEXTBOX_HEIGHT, Component.literal("Hello"));
        this.yText = new EditBox(this.font, x + Y_TEXTBOX_X, y + COORDINATE_TEXTBOX_Y, COORDINATE_TEXTBOX_WIDTH, DEFAULT_TEXTBOX_HEIGHT, Component.literal("Hello"));
        this.zText = new EditBox(this.font, x + Z_TEXTBOX_X, y + COORDINATE_TEXTBOX_Y, COORDINATE_TEXTBOX_WIDTH, DEFAULT_TEXTBOX_HEIGHT, Component.literal("Hello"));

        this.xText.setFilter(this::isInteger);
        this.yText.setFilter(this::isInteger);
        this.zText.setFilter(this::isInteger);

        textBoxes[0] = myDearestText;
        textBoxes[1] = sincerelyText;
        textBoxes[2] = xText;
        textBoxes[3] = yText;
        textBoxes[4] = zText;

        for (int i = 0; i < textBoxes.length; i++) {
            textBoxes[i].setBordered(false);
            textBoxes[i].setTextColor(0x0);
            textBoxes[i].setTextShadow(false);
            textBoxes[i].setValue("");
            this.addRenderableWidget(textBoxes[i]);
        }

        super.init();
    }

    private boolean isInteger(String text) {
        if (text.isEmpty() || text.equals("-")) {
            return true;
        }

        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.myDearestText);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }

        return !this.myDearestText.keyPressed(keyCode, scanCode, modifiers) && !this.myDearestText.canConsumeInput() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("My dear"), this.initialX, this.myDearestY, 0x0, false);
        guiGraphics.drawWordWrap(this.font, FormattedText.of(Component.translatable("I hereby send this item to the following Mailbox via Mailbird:").getString()), initialX, 17,  110, 0x0);
        guiGraphics.drawString(this.font, Component.translatable("X:"), this.initialX, coordinateY, 0x0, false);
        guiGraphics.drawString(this.font, Component.translatable("Y:"), this.yCoordinateX, coordinateY, 0x0, false);
        guiGraphics.drawString(this.font, Component.translatable("Z:"), this.zCoordinateX, coordinateY, 0x0, false);
        guiGraphics.drawString(this.font, Component.translatable("Sincerely,"), this.initialX, this.sincerelyY, 0x0, false);
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

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.fill(
                x + DEAREST_TEXTBOX_X,
                y + DEAREST_TEXTBOX_Y + 7,
                x + DEAREST_TEXTBOX_X + 64,
                y + DEAREST_TEXTBOX_Y + 7 + 1,
                0x8054371C
        );

        pGuiGraphics.fill(
                x + SINCERELY_TEXTBOX_X,
                y + SINCERELY_TEXTBOX_Y + 7,
                x + SINCERELY_TEXTBOX_X + 58,
                y + SINCERELY_TEXTBOX_Y + 7 + 1,
                0x8054371C
        );

        pGuiGraphics.fill(
                x + X_TEXTBOX_X,
                y + COORDINATE_TEXTBOX_Y + 7,
                x + X_TEXTBOX_X + 20,
                y + COORDINATE_TEXTBOX_Y + 7 + 1,
                0x8054371C
        );

        pGuiGraphics.fill(
                x + Y_TEXTBOX_X,
                y + COORDINATE_TEXTBOX_Y + 7,
                x + Y_TEXTBOX_X + 20,
                y + COORDINATE_TEXTBOX_Y + 7 + 1,
                0x8054371C
        );

        pGuiGraphics.fill(
                x + Z_TEXTBOX_X,
                y + COORDINATE_TEXTBOX_Y + 7,
                x + Z_TEXTBOX_X + 20,
                y + COORDINATE_TEXTBOX_Y + 7 + 1,
                0x8054371C
        );

        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
