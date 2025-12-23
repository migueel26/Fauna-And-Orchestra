package net.migueel26.faunaandorchestra.screen.custom;

import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.StringJoiner;
import java.util.logging.Level;

public class MelomancyCauldronScreen {
    public static int MY_COUNTER = 0;
    public static final int DEFAULT_TEXT_WIDTH = 160;
    public static final IGuiOverlay OVERLAY = MelomancyCauldronScreen::renderOverlay;
    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;
        ClientLevel level = minecraft.level;
        int centerX = guiGraphics.guiWidth()/2 + 10;
        int centerY = guiGraphics.guiHeight()/2 + 20;

        if (level != null && hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (level.getBlockEntity(pos) instanceof MelomancyCauldronBlockEntity melomancyCauldron) {

                String extra = "";

                if (melomancyCauldron.isCooking()) {
                    String dots = getDots(MY_COUNTER);
                    extra = "\n§d§l" + Component.translatable("block.faunaandorchestra.melomancy_cauldron.preparing").getString() + dots;
                } else if (melomancyCauldron.hasFinishedCooking()) {
                    extra = "\n§d" + Component.translatable("block.faunaandorchestra.melomancy_cauldron.finished").getString();
                    MY_COUNTER = 0;
                }
                NonNullList<ItemStack> ingredients = melomancyCauldron.getIngredients();
                StringJoiner output = new StringJoiner(", ");

                for (ItemStack item : ingredients) {
                    if (!item.isEmpty()) {
                        output.add(item.getItem().getDescription().getString() + " x" + item.getCount());
                    }
                }

                guiGraphics.drawWordWrap(minecraft.font, FormattedText.of(output + extra), centerX, centerY, DEFAULT_TEXT_WIDTH, 0xffffff);

                MY_COUNTER++;
            }
        }
    }

    private static String getDots(int cookTime) {
        int time = cookTime % 80;
        if (time < 20) {
            return "";
        } else if (time < 40) {
            return ".";
        } else if (time < 60) {
            return "..";
        } else {
            return "...";
        }
    }
}
