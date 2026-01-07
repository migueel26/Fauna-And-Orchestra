package net.migueel26.faunaandorchestra.screen.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import oshi.util.tuples.Pair;

import java.util.List;

public class TheGreatComposerScreen {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/dialogue.png");
    public static final int DEFAULT_OFFSET = 80;
    public static final int DEFAULT_BACKGROUND_Y = 160;
    public static final int DEFAULT_BACKGROUND_X = 100;
    public static final int DEFAULT_TEXT_Y = 172;
    public static final int TRANSITION_DURATION = 40;
    public static final int DEFAULT_TEXT_WIDTH = 160;
    // SCALING
    public static final double M = 30.0 / 53.0;
    public static final double N = -12810.0 / 53.0;
    public static final double My = 245.0 / 251.0;
    public static final double Ny = -58800.0 / 251.0;

    public static final IGuiOverlay OVERLAY = TheGreatComposerScreen::renderOverlay;
    public static final int POP_UP_TIME = 160;

    public static void renderOverlay(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        LocalPlayer player = minecraft.player;

        if (level != null && player != null) {
            List<TheGreatComposer> candidates = level.getEntitiesOfClass(TheGreatComposer.class, player.getBoundingBox().inflate(30));
            TheGreatComposer composer = candidates.isEmpty() ? null : candidates.get(0);
            if (composer != null && composer.shouldDisplayDialogue() && composer.getSpawnTime() > -1) {
                int spawnTime = composer.getSpawnTime();
                int normalizedSpawnTime = spawnTime > POP_UP_TIME ? spawnTime - POP_UP_TIME : spawnTime;
                TheGreatComposer.ComposerBossState state = composer.getState();

                ResourceLocation icon;
                Pair<Integer, Integer> size = new Pair<>(49, 60);
                Pair<Integer, Integer> location = new Pair<>(107, 136);
                String text;

                if (spawnTime < POP_UP_TIME) {
                    icon = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/composer_spawn_icon.png");
                    text = Component.translatable("dialogue.faunaandorchestra.the_great_composer0").getString();

                } else {
                    icon = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/composer_default_icon.png");
                    text = Component.translatable("dialogue.faunaandorchestra.the_great_composer1").getString();
                }

                int currIconY = location.getB() + DEFAULT_OFFSET;
                int currBackY = DEFAULT_BACKGROUND_Y + DEFAULT_OFFSET;
                int currTextY = DEFAULT_TEXT_Y + DEFAULT_OFFSET;
                int currOffset = DEFAULT_OFFSET;
                String currentText = "";

                guiGraphics.pose().translate(0, 0, 1);

                if (composer.getSpawnTime() <= TRANSITION_DURATION) {
                    // 50 -> final Y
                    // 40 -> ticks
                    currOffset = (int) Math.round(DEFAULT_OFFSET * (1 - Math.pow(1 - (double) (normalizedSpawnTime - 1) / TRANSITION_DURATION, 4)));

                }

                if (normalizedSpawnTime >= 120 && normalizedSpawnTime <= POP_UP_TIME && spawnTime > POP_UP_TIME) {
                    currOffset = (int) Math.round(DEFAULT_OFFSET * (1 - Math.pow((double) (normalizedSpawnTime - 120) / TRANSITION_DURATION, 4)));
                }

                currentText = typewritify(text, normalizedSpawnTime, guiGraphics, currTextY, currOffset);

                guiGraphics.blit(BACKGROUND, DEFAULT_BACKGROUND_X + xOffset(guiGraphics), currBackY - currOffset + yOffset(guiGraphics), 0, 0, 223, TRANSITION_DURATION);

                guiGraphics.blit(icon, location.getA() + xOffset(guiGraphics), currIconY - currOffset + yOffset(guiGraphics), 0, 0, size.getA(), size.getB(), size.getA(), size.getB());

                guiGraphics.drawWordWrap(minecraft.font, FormattedText.of(currentText), 157 + xOffset(guiGraphics), currTextY - currOffset + yOffset(guiGraphics), DEFAULT_TEXT_WIDTH, 0xffffff);

                if (spawnTime > POP_UP_TIME*2-5) {
                    composer.resetDialogueTimer();
                }
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

        if (dialogueTimer <= fullText.length() * 2 && dialogueTimer % 2 == 0) {
            player.playSound(ModSounds.DIALOGUE.get(), 0.5F, RandomSource.create().nextFloat());
        }

        return dialogueTimer < fullText.length() * 2 ? fullText.substring(0, dialogueTimer / 2) : fullText;
    }
}

