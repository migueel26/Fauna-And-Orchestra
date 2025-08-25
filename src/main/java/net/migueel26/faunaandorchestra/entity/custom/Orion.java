package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.goals.RingtailsRunAwayGoal;
import net.migueel26.faunaandorchestra.util.ModSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Orion extends TravellingMusician implements Npc, GeoEntity, TalkableEntity {
    private static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final EntityDataAccessor<Integer> DIALOGUE_TIMER = SynchedEntityData.defineId(Orion.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> CONFIDENCE = SynchedEntityData.defineId(Orion.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(Orion.class, EntityDataSerializers.BOOLEAN);
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/orion_icon.png");
    public static final int COOL_CONFIDENCE = 35;
    public static final String RESOURCE = "dialogue.faunaandorchestra.orion";
    public String currentDialogue;
    protected Faust faust;
    private final AnimationController<Orion> orionController = new AnimationController<>(this, "orion_controller", 5, this::orionState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    int confidence;
    public Orion(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.setCustomName(Component.translatable("entity.faunaandorchestra.orion"));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(DIALOGUE_TIMER, 0);
        builder.define(CONFIDENCE, 0);
        builder.define(GOOD_MORNING, true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RingtailsRunAwayGoal(this, 2.0));
    }

    private <E extends GeoAnimatable> PlayState orionState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else if (isPlaying()){
            state.getController().setAnimation(PLAYING);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (getDialogueTimer() == 0) {

            if (level().isClientSide()) {
                increaseDialogueTimer();
            } else {
                this.confidence = ModSavedData.getConfidence((ServerLevel) level(), this, player.getUUID());
                setConfidence(confidence);
                ModSavedData.saveConfidence((ServerLevel) level(), this, player.getUUID(), this.confidence + 1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(orionController);
    }

    public void setFaust(Faust faust) {
        this.faust = faust;
    }

    public Faust getFaust() {
        return faust;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
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
        boolean goodMorning = entityData.get(GOOD_MORNING);
        this.confidence = getConfidence();
        if (getDialogueTimer() <= 5) {
            if (confidence == -1) {
                dialogue = Component.translatable(RESOURCE + "2").getString();
                String[] arr = dialogue.split("%");
                dialogue = arr[0] + player.getDisplayName().getString() + arr[1];
            } else if (confidence == 0 && goodMorning) {
                dialogue = Component.translatable(RESOURCE + "0").getString();
            } else if (confidence > 0 && confidence <= COOL_CONFIDENCE && goodMorning) {
                dialogue = Component.translatable(RESOURCE + "1").getString();
            } else if (confidence > COOL_CONFIDENCE && goodMorning) {
                dialogue = Component.translatable(RESOURCE + "1s").getString();
                String[] arr = dialogue.split("%");
                dialogue = arr[0] + player.getDisplayName().getString() + arr[1];
            } else {
                int randomDialogue = random.nextInt(3, 20);
                dialogue = Component.translatable(RESOURCE + randomDialogue).getString();
                if (randomDialogue >= 16 && confidence > COOL_CONFIDENCE) dialogue = Component.translatable(RESOURCE + randomDialogue + "s").getString();
            }
            currentDialogue = dialogue;
        }
        return dialogue;
    }

    @Override
    public Pair<Integer, Integer> getIconSize() {
        return new Pair<>(49, 60);
    }

    @Override
    public Pair<Integer, Integer> getIconLocation() {
        return new Pair<>(107, 136);
    }

    @Override
    public int getDialogueTimer() {
        return entityData.get(DIALOGUE_TIMER);
    }

    @Override
    public void increaseDialogueTimer() {
        entityData.set(DIALOGUE_TIMER, getDialogueTimer() + 1);
    }

    @Override
    public void resetDialogueTimer() {
        entityData.set(DIALOGUE_TIMER, 0);
    }

    public void setGoodMorning(boolean goodMorning) {
        entityData.set(GOOD_MORNING, goodMorning);
    }

    public boolean getGoodMorning() {
        return entityData.get(GOOD_MORNING);
    }

    public void setConfidence(int confidence) {
        entityData.set(CONFIDENCE, confidence);
    }

    public int getConfidence() {
        return entityData.get(CONFIDENCE);
    }
}
