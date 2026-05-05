package net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.TravellingMusician;
import net.migueel26.faunaandorchestra.entity.goals.JazzyDammysRunAwayGoal;
import net.migueel26.faunaandorchestra.networking.StartAmbientMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.StopMusicS2CPayload;
import net.migueel26.faunaandorchestra.util.ModSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class Delroy extends TravellingMusician implements Npc, GeoEntity {
    // ANIMATION
    private static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final AnimationController<Delroy> jazzyDammyController = new AnimationController<>(this, "delroy_controller", 5, this::jazzyDammyState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // TALKABLE (NOT REALLY USEFUL FOR NOW)
    protected static final EntityDataAccessor<Integer> CONFIDENCE = SynchedEntityData.defineId(Delroy.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(Delroy.class, EntityDataSerializers.BOOLEAN);
    public static final int COOL_CONFIDENCE = 35;
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/delroy_icon.png");
    protected int confidence;
    public static final String RESOURCE = "dialogue.faunaandorchestra.delroy";
    public String currentDialogue;

    // MUSIC
    protected DanB danB;

    public Delroy(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.setCustomName(Component.translatable("entity.faunaandorchestra.delroy"));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CONFIDENCE, 0);
        builder.define(GOOD_MORNING, true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new JazzyDammysRunAwayGoal(this, 2.0));
    }

    private <E extends GeoAnimatable> PlayState jazzyDammyState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else if (isPlaying()) {
            state.getController().setAnimation(PLAYING);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (getDialogueTimer() == 0) {

            if (level().isClientSide()) {
                increaseDialogueTimer();
            } else {
                /*
                this.confidence = ModSavedData.getConfidence((ServerLevel) level(), this, player.getUUID());
                setConfidence(confidence);
                if (this.confidence >= COOL_CONFIDENCE) {
                    ModAdvancements.BEFRIEND_FAUST.get().trigger((ServerPlayer) player);
                }
                ModSavedData.saveConfidence((ServerLevel) level(), this, player.getUUID(), this.confidence + 1);
                */
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    public String getRandomDialogue(Player player) {
        // NOTE: % is the player's name
        // # is laugh and it's animated
        String dialogue = currentDialogue;
        if (getDialogueTimer() <= 5) {
            dialogue = Component.translatable(RESOURCE + "0").getString();
            currentDialogue = dialogue;
        }
        return dialogue;
    }

    @Override
    public void setConfidence(int confidence) {
        entityData.set(CONFIDENCE, confidence);
    }

    @Override
    public int getConfidence() {
        return entityData.get(CONFIDENCE);
    }

    @Override
    public void setGoodMorning(boolean goodMorning) {
        entityData.set(GOOD_MORNING, goodMorning);
    }

    @Override
    public boolean getGoodMorning() {
        return entityData.get(GOOD_MORNING);
    }

    public DanB getDanB() {
        return danB;
    }

    public void setDanB(DanB danB) {
        this.danB = danB;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(jazzyDammyController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
