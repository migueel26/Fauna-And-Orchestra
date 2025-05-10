package net.migueel26.faunaandorchestra.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.networking.RestartOrchestraMusicC2SPayload;
import net.migueel26.faunaandorchestra.screen.ParticleButton;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.neoforged.neoforge.network.PacketDistributor;

public class ConductorScreen extends AbstractContainerScreen<ConductorMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/conductor/conductor_gui.png");
    private final ConductorEntity conductor;
    private float xMouse;
    private float yMouse;
    private ExtendedSlider volumeSlider;
    private ParticleButton button;
    private boolean particlesActivated;
    private float volume;
    public ConductorScreen(ConductorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.conductor = menu.conductor;
        this.volume = 1.0F;
        this.particlesActivated = true;
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.volumeSlider = new ExtendedSlider(x + 86, y + 57, 80, 13, Component.translatable("screen.faunaandorchestra.conductor_screen"), Component.empty(), 0, 100, 100, true);
        this.button = new ParticleButton(x + 159, y + 5, Component.literal("Particle"), button -> {
            conductor.activateParticles(!((ParticleButton) button).isPressed());
        });
        this.button.press(!conductor.areParticlesActivated());

        this.addRenderableWidget(volumeSlider);
        this.addRenderableWidget(button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (mouseX > volumeSlider.getX() && mouseX < volumeSlider.getX()+80
        && mouseY > volumeSlider.getY() && mouseY < volumeSlider.getY()+13) volumeSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        float currentVolume = (float) volumeSlider.getValue() / 100.0F;
        Item newSheetMusic = conductor.getSheetMusic();
        Item currentSheetMusic = MusicUtil.getSheet(conductor.getUUID());

        if (newSheetMusic != currentSheetMusic || currentVolume != volume) {
            volume = currentVolume;
            System.out.println("Sending!");
            PacketDistributor.sendToServer(new RestartOrchestraMusicC2SPayload(conductor.getUUID(), volume));
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void afterMouseAction() {
        // Mouse released? Mouse dragged?
        super.afterMouseAction();
        /*float currentVolume = (float) volumeSlider.getValue() / 100.0F;
        Item newSheetMusic = conductor.getSheetMusic();
        Item currentSheetMusic = MusicUtil.getSheet(conductor.getUUID());

        if (newSheetMusic != currentSheetMusic || currentVolume != volume) {
            volume = currentVolume;
            System.out.println("Sending!");
            PacketDistributor.sendToServer(new RestartOrchestraMusicC2SPayload(conductor.getUUID(), volume));
        }
        if (!conductor.isOrchestraEmpty() && volume != currentVolume) {
            volume = currentVolume;
            int ticksPlaying = conductor.getTicksPlaying();
            System.out.println(ticksPlaying);

            for (MusicalEntity entity : conductor.getOrchestra()) {
                ResourceLocation musician_song = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID,
                        MusicUtil.getLocation(conductor.getSheetMusic(), entity.getInstrument().get()));

                // We stop all current instrument sounds
                ((ISoundManagerMixin) Minecraft.getInstance().getSoundManager()).faunaStopMusic(entity.getUUID());

                // We play them with the new volume / sheet music
                Minecraft.getInstance().getSoundManager().play(
                        new InstrumentSoundInstance(
                                entity,
                                BuiltInRegistries.SOUND_EVENT.get(musician_song),
                                volume, ticksPlaying));
            }
        }*/
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x , y, 0,0, imageWidth, imageHeight);

        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x + 26, y + 22, x + 78, y + 70, 30, 0.25F,
                this.xMouse, this.yMouse, this.conductor);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.xMouse = (float)mouseX;
        this.yMouse = (float)mouseY;

        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
