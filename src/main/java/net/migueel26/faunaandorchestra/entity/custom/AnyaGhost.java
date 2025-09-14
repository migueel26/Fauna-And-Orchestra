package net.migueel26.faunaandorchestra.entity.custom;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AnyaGhost extends Mob implements TalkableEntity, GeoEntity {
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final AnimationController<AnyaGhost> anyaController = new AnimationController<>(this, "anya_ghost_controller", 5, this::anyaState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    // Client Only
    protected ResourceLocation skin;

    public AnyaGhost(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        this.skin = null;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    private <E extends GeoAnimatable> PlayState anyaState(AnimationState<E> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) {
            this.setSkin(((AbstractClientPlayer) player).getSkin());
        }
        return InteractionResult.SUCCESS;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public ResourceLocation getIcon() {
        return null;
    }

    @Override
    public String getRandomDialogue(Player player) {
        return null;
    }

    @Override
    public Pair<Integer, Integer> getIconSize() {
        return null;
    }

    @Override
    public Pair<Integer, Integer> getIconLocation() {
        return null;
    }

    @Override
    public int getDialogueTimer() {
        return 0;
    }

    @Override
    public void increaseDialogueTimer() {

    }

    @Override
    public void resetDialogueTimer() {

    }

    @Override
    public void setGoodMorning(boolean goodMorning) {

    }

    @Override
    public boolean getGoodMorning() {
        return false;
    }

    public ResourceLocation getSkin() {
        return skin;
    }

    public void setSkin(PlayerSkin skin) {
        ResourceLocation convertedSkin = null;
        if (skin != null) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            ResourceLocation baseSkin = skin.texture();

            NativeImage gray = null;
            BufferedImage master;

            try {
                master = ImageIO.read(resourceManager.getResource(baseSkin).get().open());

                gray = new NativeImage(master.getWidth(), master.getHeight(), true);

                int rgb = 0, r = 0, g = 0, b = 0, a = 0;
                for (int y = 0; y < master.getHeight(); y++) {
                    for (int x = 0; x < master.getWidth(); x++) {
                        rgb = (int) (master.getRGB(x, y));
                        a = ((rgb >> 24) & 0xFF);
                        r = ((rgb >> 16) & 0xFF);
                        g = ((rgb >> 8) & 0xFF);
                        b = (rgb & 0xFF);

                        if (a == 0) continue;

                        rgb = (int) ((r + g + b) / 3);
                        rgb = (255 << 24) | (rgb << 16) | (rgb << 8) | rgb;

                        gray.setPixelRGBA(x, y, rgb);
                    }
                }
            } catch (IOException ignored) {
            }

            if (gray != null) {
                DynamicTexture dynTex = new DynamicTexture(gray);

                convertedSkin = Minecraft.getInstance()
                        .getTextureManager()
                        .register("anya_ghost", dynTex);

            }
        }

        this.skin = convertedSkin;
    }

    @NotNull
    private static String getFolder(String path) {
        String[] pathL = path.split("/");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pathL.length - 1; i++) {
            builder.append(pathL[i]);
            builder.append("/");
        }
        path = builder.toString();
        return path;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(anyaController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
