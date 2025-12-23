package net.migueel26.faunaandorchestra.screen.custom;

import net.migueel26.faunaandorchestra.block.custom.DiscordNucleiBlock;
import net.migueel26.faunaandorchestra.block.entity.DiscordNucleiBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.util.RecipesUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.StringJoiner;

public class DiscordNucleiScreen {
    public static final int DEFAULT_TEXT_WIDTH = 160;
    public static final IGuiOverlay OVERLAY = DiscordNucleiScreen::renderOverlay;
    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;
        ClientLevel level = minecraft.level;
        int centerX = guiGraphics.guiWidth()/2 + 10;
        int centerY = guiGraphics.guiHeight()/2 + 20;

        if (level != null && hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (level.getBlockEntity(pos) instanceof DiscordNucleiBlockEntity discordNuclei) {
                ItemStack stack = discordNuclei.inventory.getStackInSlot(0);
                if (!stack.isEmpty()) {
                    int essence = discordNuclei.getBlockState().getValue(DiscordNucleiBlock.ESSENCE);
                    int normInstability = discordNuclei.getBlockState().getValue(DiscordNucleiBlock.INSTABILITY);
                    int normEssence = (essence*100) / RecipesUtil.getDiscordNucleiResult(stack).getA();

                    String essenceString = Component.translatable("block.faunaandorchestra.discord_nuclei.essence").getString() + normEssence + "%";
                    String instabilityString = "\n" + Component.translatable("block.faunaandorchestra.discord_nuclei.instability").getString() + normInstability + "%";

                    String output = essenceString + instabilityString;

                    guiGraphics.drawWordWrap(minecraft.font, FormattedText.of(output), centerX, centerY, DEFAULT_TEXT_WIDTH, 0xffffff);
                }
            }
        }
    }
}
