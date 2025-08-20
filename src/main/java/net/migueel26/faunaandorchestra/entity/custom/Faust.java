package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.FaustFindOrionGoal;
import net.migueel26.faunaandorchestra.entity.goals.RingtailsRunAwayGoal;
import net.migueel26.faunaandorchestra.networking.RestartOrchestraMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.StartAmbientMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.StopMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.StopOrchestraMusicS2CPayload;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class Faust extends TravellingMusician implements Npc, GeoEntity {
    private static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private List<Player> playersListening = new ArrayList<>();

    protected Orion orion;
    private final AnimationController<Faust> faustController = new AnimationController<>(this, "faust_controller", 5, this::faustState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public Faust(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.setCustomName(Component.translatable("entity.faunaandorchestra.faust"));
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
        player.playSound(ModSounds.RINGTAILS_SONG.get());
        return super.mobInteract(player, hand);
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
            }

            for (Player player : exitPlayers) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new StopMusicS2CPayload(this.uuid));
            }

            playersListening = nearbyPlayers;
        } else {
            playersListening = new ArrayList<>();
        }
        super.tick();
    }

    @Override
    public void checkDespawn() {

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
}
