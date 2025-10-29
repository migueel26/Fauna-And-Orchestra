package net.migueel26.faunaandorchestra.screen.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.StringJoiner;

public class ComposerGravestoneScreen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/block/composer_gravestone_gui.png");
    public static int MY_COUNTER = 0;
    public static final int DEFAULT_TEXT_WIDTH = 80;
    public static final LayeredDraw.Layer OVERLAY = ComposerGravestoneScreen::renderOverlay;
    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker tracker) {
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;
        ClientLevel level = minecraft.level;
        int centerX = guiGraphics.guiWidth()/2 + 30;
        int centerY = guiGraphics.guiHeight()/2 - 40;

        if (level != null && hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (level.getBlockEntity(pos) instanceof ComposerGravestoneBlockEntity composerGravestoneBE &&
                    level.getBlockState(pos).is(ModBlocks.COMPOSER_GRAVESTONE) &&
                    !composerGravestoneBE.getBlockState().getValue(ComposerGravestoneBlock.OPENED) &&
                    composerGravestoneBE.getBlockState().getValue(ComposerGravestoneBlock.PART).equals(BedPart.HEAD)) {
                String text = Component.translatable("block.faunaandorchestra.composer_gravestone.gui").getString();

                guiGraphics.blit(BACKGROUND, centerX, centerY, 0, 0, 100, 100, 100, 100);
                guiGraphics.drawWordWrap(minecraft.font, FormattedText.of(text), centerX+13, centerY+13, DEFAULT_TEXT_WIDTH, 0x212121);

                MY_COUNTER++;
            }
        }
    }
}
