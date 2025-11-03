package net.migueel26.faunaandorchestra.entity.custom;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AnyaGhost extends AbstractCanonEntity implements TalkableEntity, GeoEntity {
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final AnimationController<AnyaGhost> anyaController = new AnimationController<>(this, "anya_ghost_controller", 5, this::anyaState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);


    public AnyaGhost(EntityType<? extends TamableAnimal> entityType, Level level) {
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
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(anyaController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }
}
