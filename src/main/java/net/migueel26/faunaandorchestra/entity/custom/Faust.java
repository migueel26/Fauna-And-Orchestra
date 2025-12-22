package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.TipCaseBlock;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.entity.goals.FaustFindOrionGoal;
import net.migueel26.faunaandorchestra.entity.goals.RingtailsRunAwayGoal;
import net.migueel26.faunaandorchestra.networking.*;
import net.migueel26.faunaandorchestra.util.ModSavedData;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class Faust extends TravellingMusician implements Npc, GeoEntity, TalkableEntity {
    private static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final EntityDataAccessor<Integer> DIALOGUE_TIMER = SynchedEntityData.defineId(Faust.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> CONFIDENCE = SynchedEntityData.defineId(Faust.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(Faust.class, EntityDataSerializers.BOOLEAN);
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/faust_icon.png");
    public static final int COOL_CONFIDENCE = 35;
    public static final int DEFAULT_LOOK_TIME = 60;
    public static final String RESOURCE = "dialogue.faunaandorchestra.faust";
    public String currentDialogue;
    private List<Player> playersListening = new ArrayList<>();

    protected Orion orion;
    protected BlockPos tipCasePos;
    private int lookTime;
    protected int confidence;

    private final AnimationController<Faust> faustController = new AnimationController<>(this, "faust_controller", 5, this::faustState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public Faust(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.setCustomName(Component.translatable("entity.faunaandorchestra.faust"));
        this.tipCasePos = null;
        this.lookTime = DEFAULT_LOOK_TIME;
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
        this.goalSelector.addGoal(1, new FaustFindOrionGoal(this));
    }

    private <E extends GeoAnimatable> PlayState faustState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else if (isPlaying()){
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
                this.confidence = ModSavedData.getConfidence((ServerLevel) level(), this, player.getUUID());
                setConfidence(confidence);
                if (this.confidence >= COOL_CONFIDENCE) {
                    ModAdvancements.BEFRIEND_FAUST.trigger((ServerPlayer) player);
                }
                ModSavedData.saveConfidence((ServerLevel) level(), this, player.getUUID(), this.confidence + 1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        BlockPos pos = new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
        if (!(pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0)) {
            this.tipCasePos = pos;
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (tipCasePos != null) {
            compound.putInt("X", tipCasePos.getX());
            compound.putInt("Y", tipCasePos.getY());
            compound.putInt("Z", tipCasePos.getZ());
        }

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void tickDeath() {
        if (orion != null) {
            orion.setFaust(null);
        }
        super.tickDeath();
    }

    @Override
    public void tick() {
        if (isPlaying() && !level().isClientSide()) {
            List<Player> nearbyPlayers = this.level().getEntitiesOfClass(
                    Player.class, this.getBoundingBox().inflate(32.0, 32.0, 32.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

            List<Player> newPlayers = new ArrayList<>(nearbyPlayers);
            List<Player> exitPlayers = new ArrayList<>(playersListening);
            exitPlayers.removeAll(nearbyPlayers);
            newPlayers.removeAll(playersListening);

            for (Player player : newPlayers) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new StartAmbientMusicS2CPayload(this.uuid));
                ModAdvancements.MEET_RINGTAILS.trigger((ServerPlayer) player);
            }

            for (Player player : exitPlayers) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new StopMusicS2CPayload(this.uuid));
            }

            playersListening = nearbyPlayers;

        } else {
            playersListening = new ArrayList<>();
        }

        if (tipCasePos != null && orion != null) {
            if (lookTime > 0) {
                lookTime--;
            } else {
                Vec3 vec3 = new Vec3(tipCasePos.getX(), tipCasePos.getY(), tipCasePos.getZ());
                this.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                orion.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                lookTime = DEFAULT_LOOK_TIME;
            }
        }

        super.tick();
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public void setPlaying(boolean playing) {
        super.setPlaying(playing);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(faustController);
    }

    public void setOrion(Orion orion) {
        this.orion = orion;
    }

    public Orion getOrion() {
        return orion;
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
                String[] arr = dialogue.split("%");
                dialogue = arr[0] + player.getDisplayName().getString() + arr[1];
            } else if (confidence > COOL_CONFIDENCE && goodMorning) {
                dialogue = Component.translatable(RESOURCE + "1s").getString();
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

    public BlockPos getTipCasePos() {
        return tipCasePos;
    }

    public void setTipCasePos(BlockPos pos) {
        this.tipCasePos = pos;
    }
}
