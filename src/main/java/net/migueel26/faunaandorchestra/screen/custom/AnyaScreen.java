package net.migueel26.faunaandorchestra.screen.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.AnyaGhost;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import oshi.util.tuples.Pair;

import java.util.List;

public class AnyaScreen {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/dialogue.png");
    public static final int DEFAULT_OFFSET = 80;
    public static final int DEFAULT_BACKGROUND_Y = 160;
    public static final int DEFAULT_BACKGROUND_X = 100;
    public static final int DEFAULT_TEXT_Y = 172;
    public static final int TRANSITION_DURATION = 40;
    public static final int DEFAULT_TEXT_WIDTH = 200;
    // SCALING
    public static final double M = 30.0 / 53.0;
    public static final double N = -12810.0 / 53.0;
    public static final double My = 245.0 / 251.0;
    public static final double Ny = -58800.0 / 251.0;

    public static final LayeredDraw.Layer OVERLAY = AnyaScreen::renderOverlay;
    public static final int POP_UP_TIME = 160;
    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker tracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        LocalPlayer player = minecraft.player;

        if (level != null && player != null) {
            List<AnyaGhost> candidates = level.getEntitiesOfClass(AnyaGhost.class, player.getBoundingBox().inflate(30));
            AnyaGhost anya = candidates.isEmpty() ? null : candidates.getFirst();
            if (anya != null && anya.tickCount >= 60) {
                int spawnTime = anya.tickCount - 60;
                int normalizedSpawnTime = spawnTime > POP_UP_TIME ? spawnTime - POP_UP_TIME : spawnTime;

                ResourceLocation icon;
                Pair<Integer, Integer> size = new Pair<>(49, 60);
                Pair<Integer, Integer> location = new Pair<>(107, 136);
                String text;

                if (spawnTime < POP_UP_TIME) {
                    text = Component.translatable("dialogue.faunaandorchestra.anya0").getString();

                } else {
                    text = Component.translatable("dialogue.faunaandorchestra.anya1").getString();
                }

                int currIconY = location.getB() + DEFAULT_OFFSET;
                int currBackY = DEFAULT_BACKGROUND_Y + DEFAULT_OFFSET;
                int currTextY = DEFAULT_TEXT_Y + DEFAULT_OFFSET;
                int currOffset = DEFAULT_OFFSET;
                String currentText = "";

                guiGraphics.pose().translate(0, 0, 1);

                if (spawnTime <= TRANSITION_DURATION) {
                    // 50 -> final Y
                    // 40 -> ticks
                    currOffset = (int) Math.round(DEFAULT_OFFSET * (1 - Math.pow(1 - (double) (normalizedSpawnTime - 1) / TRANSITION_DURATION, 4)));

                }

                if (normalizedSpawnTime >= 120 && normalizedSpawnTime <= POP_UP_TIME && spawnTime > POP_UP_TIME) {
                    currOffset = (int) Math.round(DEFAULT_OFFSET * (1 - Math.pow((double) (normalizedSpawnTime - 120) / TRANSITION_DURATION, 4)));
                }

                currentText = typewritify(text, normalizedSpawnTime, guiGraphics, currTextY, currOffset);

                guiGraphics.blit(BACKGROUND, DEFAULT_BACKGROUND_X + xOffset(guiGraphics), currBackY - currOffset + yOffset(guiGraphics), 0, 0, 223, TRANSITION_DURATION);

                guiGraphics.drawWordWrap(minecraft.font, FormattedText.of(currentText), 108 + xOffset(guiGraphics), currTextY - currOffset + yOffset(guiGraphics) - 5, DEFAULT_TEXT_WIDTH, 0xffffff);
            }
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

        if (dialogueTimer <= fullText.length()) {
            player.playSound(ModSounds.DIALOGUE.get(), 0.5F, RandomSource.create().nextFloat());
        }

        return dialogueTimer < fullText.length() ? fullText.substring(0, dialogueTimer) : fullText;
    }
}

