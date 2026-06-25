package net.migueel26.faunaandorchestra.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.networking.WriteMailC2SPayload;
import net.migueel26.faunaandorchestra.networking.packets.EraseMailC2SPacket;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

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
    public static final MutableComponent MY_DEAR_TEXT = Component.translatable("screen.faunaandorchestra.my_dear");
    public static final MutableComponent WRITE_MAILBOX_TEXT = Component.translatable("screen.faunaandorchestra.mailbox_coordinates");
    public static final MutableComponent MAILBOX_COORDINATE_X = Component.translatable("screen.faunaandorchestra.mailbox_coordinates.x");
    public static final MutableComponent MAILBOX_COORDINATE_Y = Component.translatable("screen.faunaandorchestra.mailbox_coordinates.y");
    public static final MutableComponent MAILBOX_COORDINATE_Z = Component.translatable("screen.faunaandorchestra.mailbox_coordinates.z");
    public static final MutableComponent SINCERELY_TEXT = Component.translatable("screen.faunaandorchestra.sincerely");
    public static final MutableComponent ERASE_TOOLTIP = Component.translatable("screen.faunaandorchestra.erase_tooltip");
    public static final MutableComponent WRITE_TOOLTIP = Component.translatable("screen.faunaandorchestra.write_tooltip");
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
    private SpriteIconButton eraseButton;
    private SpriteIconButton writeButton;


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

        //this.eraseButton = new ExtendedButton(x + 142, y + 7, 18, 18, Component.empty(), button -> {});
        this.eraseButton = new SpriteIconButton.Builder(Component.empty(), this::onErase, true)
                .sprite(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "erase_icon"), 16, 16)
                .size(18, 18)
                .build();
        eraseButton.setX(x + 142);
        eraseButton.setY(y + 7);
        eraseButton.setTooltip(Tooltip.create(ERASE_TOOLTIP));

        this.writeButton = new SpriteIconButton.Builder(Component.empty(), this::onWrite, true)
                .sprite(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "write_icon"), 16, 16)
                .size(18, 18)
                .build();
        writeButton.setX(x + 142);
        writeButton.setY(y + 48);
        writeButton.setTooltip(Tooltip.create(WRITE_TOOLTIP));

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

        this.addRenderableWidget(eraseButton);
        this.addRenderableWidget(writeButton);

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

    private void onWrite(Button button) {
        ItemStack stack = menu.getLetterItem();

        if (!stack.isEmpty()) {
            int x = -1;
            int y = -1;
            int z = -1;
            String sender = "";
            String receiver = "";

            if (!xText.getValue().isEmpty() && !yText.getValue().isEmpty() && !zText.getValue().isEmpty()) {
                x = Integer.parseInt(xText.getValue());
                y = Integer.parseInt(yText.getValue());
                z = Integer.parseInt(zText.getValue());
            } else if (!stack.has(ModDataComponents.POSITION)) {
                return;
            }

            if (!sincerelyText.getValue().isEmpty()) {
                sender = sincerelyText.getValue();
                stack.set(ModDataComponents.SENDER, sender);
            }

            if (!myDearestText.getValue().isEmpty()) {
                receiver = myDearestText.getValue();
                stack.set(ModDataComponents.RECEIVER, receiver);
            }

            if (x != -1) {
                stack.set(ModDataComponents.POSITION, new BlockPos(x, y, z));
            }

            PacketDistributor.sendToServer(new WriteMailC2SPayload(sender, receiver, x, y, z));

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.WRITE.get(), 1.0f));
        }
    }

    private void onErase(Button button) {
        ItemStack stack = menu.getLetterItem();

        if (!stack.isEmpty()) {
            ModNetwork.sendToServer(new EraseMailC2SPacket(stack));

            if (stack.hasTag()) {
                CompoundTag tag = stack.getTag();

                tag.remove(ModDataComponents.SENDER);
                tag.remove(ModDataComponents.RECEIVER);
                tag.remove(ModDataComponents.POSITION);

                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.ERASE.get(), 1.0f));
        }
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.myDearestText);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }

        for (EditBox textBox : this.textBoxes) {
            if (textBox.keyPressed(keyCode, scanCode, modifiers) || textBox.canConsumeInput()) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        guiGraphics.drawString(this.font, MY_DEAR_TEXT, this.initialX, this.myDearestY, 0x0, false);
        guiGraphics.drawWordWrap(this.font, FormattedText.of(WRITE_MAILBOX_TEXT.getString()), initialX, 17,  110, 0x0);
        guiGraphics.drawString(this.font, MAILBOX_COORDINATE_X, this.initialX, coordinateY, 0x0, false);
        guiGraphics.drawString(this.font, MAILBOX_COORDINATE_Y, this.yCoordinateX, coordinateY, 0x0, false);
        guiGraphics.drawString(this.font, MAILBOX_COORDINATE_Z, this.zCoordinateX, coordinateY, 0x0, false);
        guiGraphics.drawString(this.font, SINCERELY_TEXT, this.initialX, this.sincerelyY, 0x0, false);
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
