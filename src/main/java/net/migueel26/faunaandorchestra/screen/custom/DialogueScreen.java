package net.migueel26.faunaandorchestra.screen.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.migueel26.faunaandorchestra.entity.custom.TalkableEntity;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

    public static final LayeredDraw.Layer OVERLAY = DialogueScreen::renderOverlay;


    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker tracker) {
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;

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

                guiGraphics.blit(BACKGROUND, DEFAULT_BACKGROUND_X, currBackY - currOffset,0,0,223, TRANSITION_DURATION);
                // 56 69
                guiGraphics.blit(icon, location.getA(), currIconY - currOffset, 0, 0, size.getA(), size.getB(), size.getA(), size.getB());

                guiGraphics.drawWordWrap(minecraft.font, FormattedText.of(currentText), 157, currTextY - currOffset, DEFAULT_TEXT_WIDTH, 0xffffff);
            }
        }
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

            guiGraphics.drawString(Minecraft.getInstance().font, drawLaugh, 260, laughY, 0xffffff);
        }

        if (dialogueTimer <= fullText.length()*2 && dialogueTimer % 2 == 0) {
            player.playSound(ModSounds.DIALOGUE.get(), 0.5F, RandomSource.create().nextFloat());
        }

        return dialogueTimer < fullText.length()*2 ? fullText.substring(0, dialogueTimer/2) : fullText;
    }
}
