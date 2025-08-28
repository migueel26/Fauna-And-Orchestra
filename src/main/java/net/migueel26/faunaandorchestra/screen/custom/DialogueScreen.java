package net.migueel26.faunaandorchestra.screen.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.TipCaseBlock;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.migueel26.faunaandorchestra.entity.custom.TalkableEntity;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import oshi.util.tuples.Pair;

public class DialogueScreen {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/dialogue.png");
    public static final int DEFAULT_OFFSET = 80;
    public static final int DEFAULT_BACKGROUND_Y = 160;
    public static final int DEFAULT_BACKGROUND_X = 100;
    public static final int DEFAULT_TEXT_Y = 172;
    public static final int TRANSITION_DURATION = 40;
    public static final int DEFAULT_TEXT_WIDTH = 160;
    // SCALING
    public static final double M =  30.0/53.0;
    public static final double N = -12810.0/53.0;
    public static final double My = 245.0/251.0;
    public static final double Ny = -58800.0/251.0;
    // TIP CASE PRIZES
    protected static int prizeTimer = 0;
    protected static int prize = -1;

    public static final LayeredDraw.Layer OVERLAY = DialogueScreen::renderOverlay;


    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker tracker) {
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;

        // Generic dialogue for any TalkableEntity
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity hitResultEntity = entityHitResult.getEntity();
            if (hitResultEntity instanceof TalkableEntity entity && entity.getDialogueTimer() > 0) {

                ResourceLocation icon = entity.getIcon();
                Pair<Integer, Integer> size = entity.getIconSize();
                Pair<Integer, Integer> location = entity.getIconLocation();
                String text = entity.getRandomDialogue(minecraft.player);

                int currIconY = location.getB() + DEFAULT_OFFSET;
                int currBackY = DEFAULT_BACKGROUND_Y + DEFAULT_OFFSET;
                int currTextY = DEFAULT_TEXT_Y + DEFAULT_OFFSET;
                int currOffset = DEFAULT_OFFSET;
                String currentText = "";

                guiGraphics.pose().translate(0, 0, 1);

                if (entity.getDialogueTimer() <= TRANSITION_DURATION) {
                    // 50 -> final Y
                    // 40 -> ticks
                    currOffset = (int) Math.round(DEFAULT_OFFSET * (1 - Math.pow(1 - (double) (entity.getDialogueTimer() - 1) / TRANSITION_DURATION, 4)));

                }

                if (entity.getDialogueTimer() >= 220 && entity.getDialogueTimer() <= 260) {
                    currOffset = (int) Math.round(DEFAULT_OFFSET * (1 - Math.pow((double) (entity.getDialogueTimer() - 220) / TRANSITION_DURATION, 4)));
                }

                currentText = typewritify(text, entity.getDialogueTimer(), guiGraphics, currTextY, currOffset);

                entity.increaseDialogueTimer();

                if (entity.getDialogueTimer() > 260) {
                    entity.resetDialogueTimer();
                    entity.setGoodMorning(false);
                }

                guiGraphics.blit(BACKGROUND, DEFAULT_BACKGROUND_X + xOffset(guiGraphics), currBackY - currOffset + yOffset(guiGraphics),0,0,223, TRANSITION_DURATION);

                guiGraphics.blit(icon, location.getA() + xOffset(guiGraphics), currIconY - currOffset + yOffset(guiGraphics), 0, 0, size.getA(), size.getB(), size.getA(), size.getB());

                guiGraphics.drawWordWrap(minecraft.font, FormattedText.of(currentText), 157 + xOffset(guiGraphics), currTextY - currOffset + yOffset(guiGraphics), DEFAULT_TEXT_WIDTH, 0xffffff);
            }
        }

        // Prize dialogue for Faust & Orion
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState state = minecraft.level.getBlockState(blockPos);
            BlockEntity blockEntity = minecraft.level.getBlockEntity(blockPos);
            if (blockEntity instanceof TipCaseBlockEntity tipCaseBE
                && tipCaseBE.getOwner() != null
                && ((ClientLevelAccessor) minecraft.level).callGetEntities().get(tipCaseBE.getOwner()) instanceof Faust faust) {
                // The tip case is property of Faust & Orion
                int tips = state.getValue(TipCaseBlock.TIPS);
                if (tips == 16 && state.getValue(TipCaseBlock.FIRST)) {
                    minecraft.level.setBlock(blockPos, state.setValue(TipCaseBlock.FIRST, false), 3);
                    prize = 1;
                    prizeTimer = 1;
                } else if (tips == 32 && state.getValue(TipCaseBlock.SECOND)) {
                    minecraft.level.setBlock(blockPos, state.setValue(TipCaseBlock.SECOND, false), 3);
                    prize = 2;
                    prizeTimer = 1;
                } if (tips == 64 && state.getValue(TipCaseBlock.THIRD)) {
                    minecraft.level.setBlock(blockPos, state.setValue(TipCaseBlock.THIRD, false), 3);
                    prize = 3;
                    prizeTimer = 1;
                }
            }
        }

        // We show the dialogue associated with the prize
        if (prizeTimer >= 1) {
            ResourceLocation icon;
            ResourceLocation faust = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/faust_icon.png");
            ResourceLocation orion = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/orion_icon.png");
            Pair<Integer, Integer> size = new Pair<>(49, 60);
            Pair<Integer, Integer> location = new Pair<>(107, 136);
            String text = Component.translatable("dialogue.faunaandorchestra.ringtails_prize" + prize).getString();

            int currIconY = location.getB() + DEFAULT_OFFSET;
            int currBackY = DEFAULT_BACKGROUND_Y + DEFAULT_OFFSET;
            int currTextY = DEFAULT_TEXT_Y + DEFAULT_OFFSET - 5;
            int currOffset = DEFAULT_OFFSET;
            int wordWrapX = 157;
            String currentText = "";

            if (prizeTimer <= TRANSITION_DURATION) {
                // 50 -> final Y
                // 40 -> ticks
                currOffset = (int) Math.round(DEFAULT_OFFSET * (1 - Math.pow(1 - (double) (prizeTimer - 1) / TRANSITION_DURATION, 4)));

            }

            if (prizeTimer >= 220 && prizeTimer <= 260) {
                currOffset = (int) Math.round(DEFAULT_OFFSET * (1 - Math.pow((double) (prizeTimer - 220) / TRANSITION_DURATION, 4)));
            }

            currentText = typewritify(text, prizeTimer, guiGraphics, currTextY, currOffset);

            prizeTimer++;

            if (prizeTimer > 260) {
                prizeTimer = 0;
                prize = -1;
            }

            icon = switch (prize) {
                case 1 -> orion;
                case 2 -> faust;
                default -> orion;
            };

            guiGraphics.blit(BACKGROUND, DEFAULT_BACKGROUND_X + xOffset(guiGraphics), currBackY - currOffset + yOffset(guiGraphics),0,0,223, TRANSITION_DURATION);

            guiGraphics.blit(icon, location.getA() + xOffset(guiGraphics) - 5, currIconY - currOffset + yOffset(guiGraphics), 0, 0, size.getA(), size.getB(), size.getA(), size.getB());

            if (prize == 3) {
                guiGraphics.blit(faust, location.getA() + xOffset(guiGraphics) + 10, currIconY - currOffset + yOffset(guiGraphics), 0, 0, size.getA(), size.getB(), size.getA(), size.getB());
                wordWrapX += 10;
            }

            guiGraphics.drawWordWrap(minecraft.font, FormattedText.of(currentText), wordWrapX + xOffset(guiGraphics), currTextY - currOffset + yOffset(guiGraphics), DEFAULT_TEXT_WIDTH, 0xffffff);
        }
    }

    private static int xOffset(GuiGraphics guiGraphics) {
        return (int) Math.round(M * (double) guiGraphics.guiWidth() + N);
    }

    private static int yOffset(GuiGraphics guiGraphics) {
        return (int) Math.round(My * (double) guiGraphics.guiHeight() + Ny);
    }

    private static String typewritify(String fullText, int dialogueTimer, GuiGraphics guiGraphics, int currentTextY, int currentOffset) {
        Player player = Minecraft.getInstance().player;
        boolean hasLaugh = false;

        if (fullText.charAt(fullText.length()-1) == '#') {
            fullText = fullText.substring(0, fullText.length()-2);
            hasLaugh = true;
        }

        if (hasLaugh && dialogueTimer >= fullText.length()*2) {
            // If laugh
            String laugh = Component.translatable(Orion.RESOURCE + "_laugh").getString();
            int newTime = dialogueTimer - fullText.length()*2;

            String drawLaugh = newTime < laugh.length()*20 ? laugh.substring(0, newTime/20) : laugh;

            int laughY = (int) Math.round(currentTextY - currentOffset + 9 + Math.sin(newTime/5)*3);

            if (newTime <= laugh.length()*20 && newTime % 20 == 0) {
                player.playSound(ModSounds.DIALOGUE.get(), 0.5F, RandomSource.create().nextFloat());
            }

            guiGraphics.drawString(Minecraft.getInstance().font, drawLaugh, 260 + xOffset(guiGraphics), laughY + yOffset(guiGraphics), 0xffffff);
        }

        if (dialogueTimer <= fullText.length()*2 && dialogueTimer % 2 == 0) {
            player.playSound(ModSounds.DIALOGUE.get(), 0.5F, RandomSource.create().nextFloat());
        }

        return dialogueTimer < fullText.length()*2 ? fullText.substring(0, dialogueTimer/2) : fullText;
    }
}
