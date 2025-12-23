package net.migueel26.faunaandorchestra.entity.custom.boss;

import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.CrawlingDiscordBlock;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.projectile.MusicNoteProjectileEntity;
import net.migueel26.faunaandorchestra.entity.custom.projectile.PhantomNoteProjectileEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.networking.ShowTitlePlayerS2CPayload;
import net.migueel26.faunaandorchestra.networking.StartAmbientMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.StopMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.packets.ShowTitlePlayerS2CPacket;
import net.migueel26.faunaandorchestra.networking.packets.StartAmbientMusicS2CPacket;
import net.migueel26.faunaandorchestra.networking.packets.StopMusicS2CPacket;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import java.util.Optional;

/**<p>
 * PHASE 0: SPAWNING
 * </p>
 * <p>
 * PHASE 1: ABOVE 50% HEALTH ->
 * Normal attacks, from 1-6 (7 is the Canon spawn)
 * <p>
 * PHASE 2: BELOW 50% HEALTH ->
 * Faster attacks and harder to dodge, from 1-7 (starts with 7)
 * <p>
 * PHASE 3: FAKE DEAD ->
 * Does nothing, waiting for the player to try to pick up the baton
 * <p>
 * PHASE 4: RESURRECTS AS A SKULL. STARTS FROM 50% HEALTH ->
 * Exactly like Phase 2, but faster and teleports around constantly. It can't protect itself anymore.
 */

public class TheGreatComposer extends Mob implements Enemy, GeoEntity {
    protected static final int MAX_HEALTH = 300;
    protected static final int IDLE_ATTACK_COOLDOWN = 60;
    protected static final int THROW_NORMAL_ATTACK_COOLDOWN = 30;
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation DODGE = RawAnimation.begin().thenPlay("dodge");
    public static final RawAnimation NORMAL_ATTACK = RawAnimation.begin().thenPlay("attack");
    public static final RawAnimation POISON_ATTACK = RawAnimation.begin().thenPlay("attack_poison");
    public static final RawAnimation SUMMON_ATTACK = RawAnimation.begin().thenPlay("attack_summon");
    public static final RawAnimation MELEE_ATTACK = RawAnimation.begin().thenPlay("attack_melee");
    public static final RawAnimation CANON_ATTACK = RawAnimation.begin().thenPlay("attack_canon");
    public static final RawAnimation HEADLESS = RawAnimation.begin().thenPlay("headless");
    public static final RawAnimation LAUGH_ATTACK = RawAnimation.begin().thenPlay("prepare").thenLoop("laugh");
    public static final RawAnimation NOTES_ATTACK = RawAnimation.begin().thenPlay("prepare").thenLoop("attack_notes");
    public static final RawAnimation SHOCK = RawAnimation.begin().thenPlay("shock");
    public static final RawAnimation FAKE_DYING = RawAnimation.begin().thenPlay("dying");
    public static final RawAnimation WEAK = RawAnimation.begin().thenPlay("weak");
    public static final RawAnimation AWAIT = RawAnimation.begin().thenPlay("attack_await");
    public static final RawAnimation REPEL = RawAnimation.begin().thenPlay("repel");
    public static final RawAnimation FAKE_DEAD = RawAnimation.begin().thenPlay("dead");
    public static final RawAnimation IDLE_HEAD = RawAnimation.begin().thenPlay("head_idle");
    public static final RawAnimation HEAD_ATTACK = RawAnimation.begin().thenPlay("head_attack");
    public static final RawAnimation HEAD_ATTACK_LOOP = RawAnimation.begin().thenLoop("head_attack");
    public static final RawAnimation HEAD_ATTACK_MELEE = RawAnimation.begin().thenPlay("head_melee");
    public static final RawAnimation HEAD_ATTACK_CANON = RawAnimation.begin().thenPlay("head_canon");
    public static final RawAnimation HEAD_DYING = RawAnimation.begin().thenPlay("head_dying");
    public static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    public static final RawAnimation RESURRECT = RawAnimation.begin().thenPlay("resurrect");
    public final AnimationController<TheGreatComposer> composerController = new AnimationController<>(this, "composer_controller", 5, this::composerState)
            .triggerableAnim("dodge", DODGE)
            .triggerableAnim("normal_attack", NORMAL_ATTACK)
            .triggerableAnim("poison_attack", POISON_ATTACK)
            .triggerableAnim("summon_attack", SUMMON_ATTACK)
            .triggerableAnim("melee_attack", MELEE_ATTACK)
            .triggerableAnim("canon_attack", CANON_ATTACK)
            .triggerableAnim("repel", REPEL)
            .triggerableAnim("fake_die", FAKE_DYING)
            .triggerableAnim("attack_head", HEAD_ATTACK)
            .triggerableAnim("melee_head", HEAD_ATTACK_MELEE)
            .triggerableAnim("canon_head", HEAD_ATTACK_CANON)
            .triggerableAnim("die", HEAD_DYING)
            .triggerableAnim("spawn", SPAWN)
            .triggerableAnim("resurrect", RESURRECT);
    public static EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(TheGreatComposer.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<Integer> REPELS = SynchedEntityData.defineId(TheGreatComposer.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<BlockPos> DEFAULT_POSITION = SynchedEntityData.defineId(TheGreatComposer.class, EntityDataSerializers.BLOCK_POS);
    public static EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(TheGreatComposer.class, EntityDataSerializers.INT);
    protected int attackCooldown = IDLE_ATTACK_COOLDOWN;
    ////////// CLIENT AND SERVER
    protected int stateId;
    /////////// WORKS DIFFERENTLY IN SERVER AND CLIENT
    protected int stateTime = 0;
    protected int spawnDialogueTime = -1;
    private double velocityY = 0.0; // Velocidad vertical propia
    //////////// CLIENT ONLY
    int scheduleDirty = -1;
    boolean dirty = false;
    //////////// SERVER ONLY
    // the Composer will repel n-1 times
    protected int consecutiveAttacks = 0;
    protected int repels = 2;
    protected float healthBefore;
    ComposerCanonEntity canonEntity;
    protected BlockPos diePos;
    private List<Player> playersListening = new ArrayList<>();
    protected List<? extends Holder<MobEffect>> effectsList = new ArrayList<>(List.of(
            ModEffects.BOOGIE,
            MobEffects.DARKNESS,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.WEAKNESS));

    protected List<Item> instrumentList = new ArrayList<>(List.of(
            ModItems.FLUTE.get(),
            ModItems.SAXOPHONE.get(),
            ModItems.KEYTAR.get(),
            ModItems.OBOE.get(),
            ModItems.BATON.get()));
    ////////////
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS
    );
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);


    public TheGreatComposer(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        bossEvent.setCreateWorldFog(true);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0f) {
            @Override
            public boolean canUse() {
                boolean canUse = !((TheGreatComposer) mob).isFakeDead() && !((TheGreatComposer) mob).isSpawning();
                return super.canUse() && canUse;
            }
        });
    }

    private <E extends GeoAnimatable> PlayState composerState(AnimationState<E> state) {
        ComposerBossState bossState = getState(stateId);

        updateTransitionLength(bossState);
        switch (bossState) {
            case NORMAL_ATTACK -> state.getController().setAnimation(AWAIT);
            case LAUGH_ATTACK -> state.getController().setAnimation(isFinalPhase() ? HEAD_ATTACK_LOOP : LAUGH_ATTACK);
            case NOTE_ATTACK -> state.getController().setAnimation(isFinalPhase() ? HEAD_ATTACK_LOOP : NOTES_ATTACK);
            case CANON_ATTACK -> state.getController().setAnimation(isFinalPhase() ? IDLE_HEAD : HEADLESS);
            case WEAK -> state.getController().setAnimation(WEAK);
            case SHOCK -> state.getController().setAnimation(SHOCK);
            case DEAD -> state.getController().setAnimation(FAKE_DEAD);
            case DYING -> state.getController().setAnimation(HEAD_DYING);
            case null, default -> state.getController().setAnimation(isFinalPhase() ? IDLE_HEAD : IDLE);
        }

        return PlayState.CONTINUE;
    }

    private void updateTransitionLength(ComposerBossState bossState) {
        if (dirty) {
            this.dirty = false;
            composerController.transitionLength(5);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(STATE, 0);
        builder.define(REPELS, 2);
        builder.define(DEFAULT_POSITION, this.blockPosition());
        builder.define(PHASE, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(STATE)) {
            this.stateId = entityData.get(STATE);
        }
        if (key.equals(REPELS)) {
            this.repels = entityData.get(REPELS);
        }
        super.onSyncedDataUpdated(key);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.entityData.set(DEFAULT_POSITION, blockPosition().above(5));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (getState(stateId) == ComposerBossState.DEAD) {
            player.hurt(damageSources().mobAttack(this), 5);
            this.setNewState(ComposerBossState.RESURRECTING);
            this.bossEvent.setVisible(true);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(composerController);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        int x = compound.getInt("X");
        int y = compound.getInt("Y");
        int z = compound.getInt("Z");
        this.entityData.set(DEFAULT_POSITION, new BlockPos(x, y, z));
        this.entityData.set(PHASE, compound.getInt("Phase"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putInt("X", this.entityData.get(DEFAULT_POSITION).getX());
        compound.putInt("Y", this.entityData.get(DEFAULT_POSITION).getY());
        compound.putInt("Z", this.entityData.get(DEFAULT_POSITION).getZ());
        compound.putInt("Phase", this.entityData.get(PHASE));
        if (getState(stateId) == ComposerBossState.RESURRECTING) {
            compound.putFloat("Health", this.getMaxHealth()/2);
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        if (isFinalPhase()) {
            return super.getDefaultDimensions(pose).scale(1.25F, 0.75F);
        } else if (getState(stateId) == ComposerBossState.DEAD) {
            return super.getDefaultDimensions(pose).scale(2.75F, 0.75F);
        } else {
            return super.getDefaultDimensions(pose);
        }
    }

    @Override
    public void tick() {

        if (!level().isClientSide()) {
            // Music Logic
            if (!isFakeDead()) {
                List<Player> nearbyPlayers = this.level().getEntitiesOfClass(
                        Player.class, this.getBoundingBox().inflate(63.0, 32.0, 63.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

                List<Player> newPlayers = new ArrayList<>(nearbyPlayers);
                List<Player> exitPlayers = new ArrayList<>(playersListening);
                exitPlayers.removeAll(nearbyPlayers);
                newPlayers.removeAll(playersListening);

                for (Player player : newPlayers) {
                    if (player instanceof  ServerPlayer serverPlayer) {
                        ModNetwork.sendToPlayer(new StartAmbientMusicS2CPacket(this.uuid), serverPlayer);
                    }
                }

                for (Player player : exitPlayers) {
                    if (player instanceof  ServerPlayer serverPlayer) {
                        ModNetwork.sendToPlayer(new StopMusicS2CPacket(this.uuid), serverPlayer);
                    }
                }

                playersListening = nearbyPlayers;
            } else {
                playersListening = new ArrayList<>();
            }
        }

        if (isSpawning() && spawnDialogueTime == -1) {
            spawnDialogueTime = 0;

        } else if (spawnDialogueTime > -1 && spawnDialogueTime <= 320) {

            spawnDialogueTime++;
        }

        if (scheduleDirty > 0) {
            scheduleDirty--;
        } else if (scheduleDirty == 0) {
            dirty = true;
            scheduleDirty = -1;
        }

        if (stateTime % 10 == 0 && getState(stateId) != ComposerBossState.DEAD) {
            // Display fire particles under the spine
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(), this.getY() - (isFinalPhase() ? 0 : 0.25F), this.getZ(),
                        5, 0.35, 0.25, 0.35, 0);
            }
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        stateTime++;

        super.tick();
    }

    @Override
    public void aiStep() {
        // We need to update its position both in the client and server
        ComposerBossState state = getState(stateId);

        if (isSpawning() && stateTime > 1 && stateTime < 120) {
            this.moveTo(getX(), getY() + 0.05, getZ());
            this.setRemainingFireTicks(0);
        }

        if (state == ComposerBossState.RESURRECTING) {
            refreshDimensions();
        }

        if (state == ComposerBossState.WEAK || state == ComposerBossState.DEAD) {
            /*while (getBlockStateOn().is(Blocks.AIR) && stateTime % 5 == 0) {
                this.moveTo(this.getX(), this.getY() - 0.2f, this.getZ());
            }*/
            refreshDimensions();

            double gravity = -0.08; // similar a la vanilla
            double drag = 0.98;     // resistencia del aire

            if (getBlockStateOn().is(Blocks.AIR)) {
                velocityY += gravity;
                velocityY *= drag; // aplica resistencia

                // Nueva posición
                Vec3 moveVec = new Vec3(0, velocityY, 0);
                this.move(MoverType.SELF, moveVec);

            } else if (velocityY > 0) {
                velocityY = 0;
            }
        }

        super.aiStep();
    }


    @Override
    protected void customServerAiStep() {
        ComposerBossState state = getState(stateId);

        if (this.entityData.get(PHASE) == 3 && state == ComposerBossState.IDLE) {
            setNewState(ComposerBossState.DEAD);
            this.stateTime = 2;
        }

        if (isSpawning()) {
            if (stateTime == 1) {
                triggerAnim("composer_controller", "spawn");
            }

            if (stateTime == 140) {
                for (Player player : bossEvent.getPlayers()) {
                    level().playSound(null, player.blockPosition().above(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.NEUTRAL);
                }
                playStateSound(ModSounds.SPAWN.get());
                ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, position().x, position().y, position().z, 100, 0.1, 0.1, 0.1, 0.3);
                String[] fullName = Component.translatable("entity.faunaandorchestra.the_great_composer").getString().split(",");
                String name = fullName[0];
                String nickname = fullName[1].substring(1);

                for (ServerPlayer player : bossEvent.getPlayers()) {
                    ModNetwork.sendToPlayer(new ShowTitlePlayerS2CPacket(name, nickname), player);
                }

            }

            if (stateTime == 160) {
                BlockPos pos = entityData.get(DEFAULT_POSITION);
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL, position().x, position().y, position().z, 70, 0.1, 0.1, 0.1, 0.15);
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL, pos.getX(), pos.getY(), pos.getZ(), 70, 0.1, 0.1, 0.1, 0.15);

                this.moveTo(pos.getX(), pos.getY(), pos.getZ());
                setNewState(ComposerBossState.IDLE);
                this.entityData.set(PHASE, 1);
                this.attackCooldown = getCooldownTicks();
            }

        } else if (state == ComposerBossState.IDLE) {
            if (attackCooldown == 0) {
                BlockPos pos = entityData.get(DEFAULT_POSITION);
                this.moveTo(pos.getX(), pos.getY(), pos.getZ());
                int nextAttack;

                nextAttack = getNextAttack();

                ComposerBossState newState = getState(nextAttack);

                setNewState(newState);
            }
        } else if (state == ComposerBossState.NORMAL_ATTACK) {
            if (stateTime == 1) {
                playStateSound(ModSounds.ATTACK_NORMAL.get());
            }
            if (stateTime == THROW_NORMAL_ATTACK_COOLDOWN) {
                for (Player player : bossEvent.getPlayers()) {
                    // We add the music note projectile
                    this.lookControl.setLookAt(player);
                    Vec3 vec3 = this.getViewVector(1.0F);
                    double d2 = player.getX() - (this.getX() + vec3.x * 4.0);
                    double d3 = player.getY(1.25) - (0.5 + this.getY(0.5));
                    double d4 = player.getZ() - (this.getZ() + vec3.z * 4.0);
                    Vec3 vec31 = new Vec3(d2, d3, d4);
                    MusicNoteProjectileEntity note = new MusicNoteProjectileEntity(this, vec31.normalize(), level());
                    note.setPos(this.getX() + vec3.x * 1.25, this.getY(0.5), note.getZ() + vec3.z * 1.25);
                    level().playSound(null, blockPosition(), SoundEvents.VEX_HURT, SoundSource.NEUTRAL, 2.0f, 1.0f);
                    level().addFreshEntity(note);

                    // We establish the repels
                    setRepels();
                }
            }

            if (stateTime >= 50) {
                if (repels == 0) {
                    setNewState(ComposerBossState.SHOCK);
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                            this.getX(), this.getY()+0.5, this.getZ(),
                            50, 0.1, 0.75, 0.1, 0.1);

                    ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            this.getX(), this.getY(), this.getZ(),
                            50, 0.1, 0.75, 0.1, 0.1);
                }
                else if (level().getEntitiesOfClass(MusicNoteProjectileEntity.class, this.getBoundingBox().inflate(20)).isEmpty()) {
                    setNewState(ComposerBossState.IDLE);
                    this.attackCooldown = getCooldownTicks();
                    consecutiveAttacks++;
                }
            }

        } else if (state == ComposerBossState.SHOCK) {
            if (stateTime == 1) {
                playStateSound(ModSounds.SHOCK.get());
                playStateSound(ModSounds.ELECTRIC_SHOCK.get());
            }
            if (stateTime == 40) {
                this.healthBefore = getHealth() / getMaxHealth();
                setNewState(ComposerBossState.WEAK);
            }

        } else if (state == ComposerBossState.WEAK) {
            if (stateTime == 1) {
                playStateSound(ModSounds.WEAK.get());
            }
            if (stateTime == 100 || (healthBefore - getHealth() / getMaxHealth()) >= 0.25) {
                setNewState(ComposerBossState.IDLE);
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY(), this.getZ(),
                        60, 0.1, 0.5, 0.1, 0.15);

                BlockPos newPos = this.entityData.get(DEFAULT_POSITION);

                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                        newPos.getX(), newPos.getY(), newPos.getZ(),
                        60, 0.3, 1, 0.3, 0.1);

                this.moveTo(newPos.getX(), newPos.getY(), newPos.getZ());
                this.attackCooldown = getCooldownTicks();
                this.consecutiveAttacks = 0;
            }
        } else if (state == ComposerBossState.POISON_ATTACK) {
            if (stateTime == 1) {
                playStateSound(isFinalPhase() ? ModSounds.ATTACK_HEADLESS.get() : ModSounds.ATTACK_POISON.get());
            }
            if (stateTime == 45) {
                for (Player player : bossEvent.getPlayers()) {
                    level().playSound(null, player.blockPosition().above(), SoundEvents.VEX_CHARGE, SoundSource.NEUTRAL, 2.0f, 1.0f);
                }
                List<LivingEntity> entities = level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, this.getBoundingBox().inflate(20));
                for (LivingEntity entity : entities) {
                    Holder<MobEffect> nextEffect = effectsList.get(random.nextInt(0, effectsList.size()));
                    entity.addEffect(new MobEffectInstance(nextEffect, isSecondPhase() ? 280 : 200, isSecondPhase() ? 2 : 1));
                }
                ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, position().x, position().y, position().z, 100, 0.1, 0.1, 0.1, 0.3);
                ((ServerLevel) level()).sendParticles(ParticleTypes.EFFECT, position().x, position().y, position().z, 100, 0.1, 0.1, 0.1, 0.3);
            }
            if (stateTime == 80) {
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = getCooldownTicks();
                consecutiveAttacks++;
            }

        } else if (state == ComposerBossState.LAUGH_ATTACK) {
            if (stateTime == 1) {
                playStateSound(ModSounds.PREPARE.get());
            }
            if (stateTime == 25) {
                Optional<ServerPlayer> opPlayer = bossEvent.getPlayers().stream().findAny();
                if (opPlayer.isPresent()) {
                    // Create a crawling discord based on the player
                    ServerPlayer player = opPlayer.get();
                    int x = player.getBlockX();
                    int y = player.getBlockY();
                    int z = player.getBlockZ();
                    Direction direction = player.getDirection();

                    int rx = random.nextInt(12, 19);
                    int rz = random.nextInt(12, 19);

                    CrawlingDiscordBlock block = (CrawlingDiscordBlock) ModBlocks.CRAWLING_DISCORD.get();
                    if (this.isSecondPhase()) block.setDifficult(true);

                    switch (direction) {
                        case NORTH -> {
                            rz = (rz - 15) * -1;
                            rx = random.nextBoolean() ? rx *= -1 : rx;
                        }
                        case SOUTH -> {
                            rx = random.nextBoolean() ? rx *= -1 : rx;
                            rz -= 10;
                        }
                        case EAST -> {
                            rz = random.nextBoolean() ? rz *= -1 : rz;
                            rx -= 10;
                        }
                        default -> {
                            rx = (rx - 15) * -1;
                            rz = random.nextBoolean() ? rz *= -1 : rz;
                        }
                    }

                    int offset = 0;
                    boolean sw = false;
                    BlockPos currentPos = new BlockPos(x + rx, y, z + rz);

                    while (!level().getBlockState(currentPos).isAir() || level().getBlockState(currentPos.below()).isAir()) {
                        currentPos = new BlockPos(x + rx, y + offset, z + rz);
                        offset = sw ? -offset : (offset <= 0 ? offset - 1 : offset + 1);
                        sw = !sw;
                    }

                    level().setBlock(currentPos, block.defaultBlockState(), 3);
                    EntityType.LIGHTNING_BOLT.spawn((ServerLevel) level(), currentPos, MobSpawnType.MOB_SUMMONED);

                    // Create two crawling discord based on the composer
                    direction = this.getDirection().getOpposite();

                    for (int i = 1; i <= 2; i++) {

                        x = this.getBlockX();
                        y = this.getBlockY();
                        z = this.getBlockZ();

                        rx = random.nextInt(5, 9);
                        rz = random.nextInt(5, 9);

                        switch (direction) {
                            case NORTH -> {
                                rz *= -1;
                                if (i == 2) rx *= -1;
                            }
                            case SOUTH -> {
                                if (i == 2) rx *= -1;
                            }
                            case EAST -> {
                                if (i == 2) rz *= -1;
                            }
                            default -> {
                                rx *= -1;
                                if (i == 2) rz *= -1;
                            }
                        }

                        offset = 0;
                        sw = false;
                        currentPos = new BlockPos(x + rx, y, z + rz);

                        while (!level().getBlockState(currentPos).isAir() || level().getBlockState(currentPos.below()).isAir()) {
                            currentPos = new BlockPos(x + rx, y + offset, z + rz);
                            offset = sw ? -offset : (offset <= 0 ? offset - 1 : offset + 1);
                            sw = !sw;
                        }

                        level().setBlock(currentPos, block.defaultBlockState(), 3);
                        EntityType.LIGHTNING_BOLT.spawn((ServerLevel) level(), currentPos, MobSpawnType.MOB_SUMMONED);
                    }
                }
            }

            if (stateTime >= 25 && (stateTime-25) % 80 == 0) {
                level().playSound(null, blockPosition(), ModSounds.ATTACK_LAUGH.get(), SoundSource.NEUTRAL, 1.5f, 1.0f + (level().random.nextFloat()/2 - 0.25f));
            }

            if (stateTime == CrawlingDiscordBlock.NEW_CHILD_TIME * CrawlingDiscordBlock.DEFAULT_MAX_GENERATION + CrawlingDiscordBlock.DIE_TIME + (isFinalPhase() ? 0 : 40)) {
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = getCooldownTicks() + 20;
                consecutiveAttacks++;
            }

        } else if (state == ComposerBossState.SUMMON_ATTACK) {
            if (stateTime == 6) playStateSound(isFinalPhase() ? ModSounds.ATTACK_HEADLESS.get() : ModSounds.ATTACK_SUMMON.get());
            if (stateTime == (isFinalPhase() ? 30 : 65)) {
                level().playSound(null, blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL);
                Direction direction = this.getDirection();
                EntityType<? extends AbstractSkeleton> skeletonType;

                if (isSecondPhase()) {
                    skeletonType = EntityType.WITHER_SKELETON;
                } else {
                    skeletonType = EntityType.SKELETON;
                }

                switch (direction) {
                    case NORTH, SOUTH -> {
                        spawnSkeleton(getBlockX() + 3, getBlockY(), getBlockZ(), skeletonType);
                        spawnSkeleton(getBlockX() - 3, getBlockY(), getBlockZ(), skeletonType);
                    }
                    default -> {
                        spawnSkeleton(getBlockX(), getBlockY(), getBlockZ() + 3, skeletonType);
                        spawnSkeleton(getBlockX(), getBlockY(), getBlockZ() - 3, skeletonType);
                    }
                }

                for (Player player : bossEvent.getPlayers()) {
                    direction = player.getDirection();

                    for (int i = 1; i <= 2; i++) {

                        if (isSecondPhase()) {
                            float r = random.nextFloat();
                            if (r >= 0.6) skeletonType = EntityType.SKELETON;
                            else if (r < 0.6 && r >= 0.4) skeletonType = EntityType.WITHER_SKELETON;
                            else if (r < 0.4 && r >= 0.2) skeletonType = EntityType.BOGGED;
                            else skeletonType = EntityType.STRAY;
                        } else {
                            if (random.nextFloat() <= 0.3) skeletonType = EntityType.WITHER_SKELETON;
                            else skeletonType = EntityType.SKELETON;
                        }

                        int offset = (i == 2) ? -4 : 4;

                        switch (direction) {
                            case NORTH, SOUTH -> {
                                spawnSkeleton(player.getBlockX() + offset, player.getBlockY(), player.getBlockZ(), skeletonType);
                            }
                            default -> {
                                spawnSkeleton(player.getBlockX(), player.getBlockY(), player.getBlockZ() + offset, skeletonType);
                            }
                        }

                    }

                }
            } else if (stateTime == 170) {
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = getCooldownTicks() + 20;
                consecutiveAttacks++;
            }

        } else if (state == ComposerBossState.MELEE_ATTACK) {
            if (stateTime == 1) {
                playStateSound(ModSounds.ATTACK_MELEE.get());
            }
            if (stateTime == 25) {
                Optional<ServerPlayer> oPlayer = bossEvent.getPlayers().stream().findAny();
                if (oPlayer.isPresent() && oPlayer.get() instanceof ServerPlayer player) {
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                            this.getX(), this.getY(), this.getZ(),
                            60, 0.3, 1, 0.3, 0.1);
                    this.moveTo(player.getBlockX(), player.getBlockY() + 0.70F, player.getBlockZ());
                }
            } else if (stateTime == 30) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY(), this.getZ(),
                        60, 0.3, 1, 0.3, 0.1);

            } else if (stateTime >= (isFinalPhase() ? 35 : 45) && stateTime < 100) {
                if (stateTime % 5 == 0) {
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            this.getX(), this.getY(), this.getZ(),
                            40, 1, 0.1, 1, 0.2);
                }

                if (stateTime % 10 == 2) {
                    List<LivingEntity> entities = level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, this.getBoundingBox().inflate(2));
                    for (LivingEntity entity : entities) {
                        entity.hurt(damageSources().mobAttack(this), 20.0F);
                    }
                }


            } else if (stateTime == 105) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY(), this.getZ(),
                        60, 0.3, 1, 0.3, 0.1);

                BlockPos blockPos = this.entityData.get(DEFAULT_POSITION);

                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                        blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        60, 0.3, 1, 0.3, 0.1);

                this.moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = getCooldownTicks() - 20;
                consecutiveAttacks++;
            }

        } else if (state == ComposerBossState.NOTE_ATTACK) {
            if (stateTime == 1) {
                playStateSound(ModSounds.PREPARE.get());
            }
            int frequency = isSecondPhase() ? 4 : 7;
            if (stateTime >= 40 && stateTime % frequency == 0) {
                for (Player player : bossEvent.getPlayers()) {
                    this.lookControl.setLookAt(player);
                    Vec3 vec3 = this.getViewVector(1.0F);
                    double d2 = player.getX() - (this.getX() + vec3.x * 4.0);
                    double d3 = player.getY(1.35) - (0.5 + this.getY(0.5));
                    double d4 = player.getZ() - (this.getZ() + vec3.z * 4.0);
                    Vec3 vec31 = new Vec3(d2, d3, d4);
                    PhantomNoteProjectileEntity note = new PhantomNoteProjectileEntity(this, vec31.normalize(), level());
                    note.setPos(this.getX() + vec3.x * 1.25, this.getY(0.5) - (isFinalPhase() ? 0.5 : 0), note.getZ() + vec3.z * 1.25);
                    level().addFreshEntity(note);

                    level().playSound(null, blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL);
                }

            }

            if (stateTime >= 150) {
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = getCooldownTicks();
                consecutiveAttacks++;
            }

        } else if (state == ComposerBossState.CANON_ATTACK) {
            if (stateTime == 1) {
                playStateSound(ModSounds.ATTACK_CANON.get());
            }
            if (stateTime == (isFinalPhase() ? 30 : 90)) {
                Vec3 direction = this.getViewVector(1f);
                canonEntity = new ComposerCanonEntity(ModEntities.THE_GREAT_COMPOSER_CANON.get(), level());
                canonEntity.setYBodyRot(this.getYRot());
                canonEntity.setYHeadRot(this.getYHeadRot());
                canonEntity.setPos(this.getX(), this.getY() - 1.25f, this.getZ());
                level().addFreshEntity(canonEntity);
            }

            if (stateTime > 95 && (stateTime >= 1000 || (canonEntity == null || !canonEntity.isAlive()))) {
                if (canonEntity != null && canonEntity.isAlive()) {
                    canonEntity.discard();
                }

                this.setNewState(ComposerBossState.IDLE);
                this.attackCooldown = getCooldownTicks();
                consecutiveAttacks++;
            }
        } else if (state == ComposerBossState.FAKE_DYING) {
            if (stateTime == 1) {
                BlockPos pos = entityData.get(DEFAULT_POSITION);
                this.moveTo(pos.getX(), pos.getY(), pos.getZ());
                this.entityData.set(PHASE, 3);
                trigger("fake_die", false);
                level().playSound(null, blockPosition(), ModSounds.FAKE_DYING.get(), SoundSource.NEUTRAL);
                level().playSound(null, blockPosition(), ModSounds.ELECTRIC_SHOCK.get(), SoundSource.NEUTRAL);

            } else if (stateTime >= 1 && stateTime < 100) {
                if (stateTime % 5 == 0) {
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                            getX(), getY() + 0.75, getZ(), 50, 0.1, 0.5, 0.1, 0.15);
                }
            } else if (stateTime == 100) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CLOUD,
                        getX(), getY() + 0.75, getZ(), 50, 0.1, 0.5, 0.1, 0.25);
                setNewState(ComposerBossState.DEAD);
                this.attackCooldown = getCooldownTicks();
            }

        } else if (state == ComposerBossState.RESURRECTING) {
            if (stateTime == 1) {
                playStateSound(ModSounds.RESURRECT.get());
            }
            if (this.getHealth() < (this.getMaxHealth() / 2) - 1) {
                if (stateTime == 1) triggerAnim("composer_controller", "resurrect");
                if (stateTime > 15) {
                    this.setHealth(this.getHealth() + 1);
                    this.moveTo(getX(), getY() + 0.05, getZ());
                }
                this.entityData.set(PHASE, 4);

                if (stateTime == 97) {
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL, position().x, position().y, position().z, 70, 0.1, 0.1, 0.1, 0.15);
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, position().x, position().y, position().z, 100, 0.1, 0.1, 0.1, 0.3);
                }
            } else {
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = getCooldownTicks();
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL, position().x, position().y, position().z, 70, 0.1, 0.1, 0.1, 0.15);
                for (Player player : bossEvent.getPlayers()) {
                    player.displayClientMessage(Component.translatable("dialogue.faunaandorchestra.the_great_composer2"), true);
                }
            }

        } else if (state == ComposerBossState.DYING) {
            if (stateTime == 1) {
                for (ServerPlayer player : bossEvent.getPlayers()) {
                    ModAdvancements.KILL_COMPOSER.trigger(player);
                }

                this.diePos = blockPosition();
                level().playSound(null, blockPosition(), ModSounds.DYING.get(), SoundSource.NEUTRAL);
                level().playSound(null, blockPosition(), ModSounds.ELECTRIC_SHOCK.get(), SoundSource.NEUTRAL);
            } else if (stateTime == 157) {
                level().explode(null, getX(), getY(), getZ(), 5, Level.ExplosionInteraction.TNT);
            } else if (stateTime == 160) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CLOUD,
                        getX(), getY()+0.2, getZ(), 70, 0.1, 0.1, 0.1, 0.25);

                ItemEntity baton = new ItemEntity(level(), getX()+0.2, getY(), getZ()+0.2, new ItemStack(ModItems.LEGENDARY_BATON.get(), 1));
                ItemEntity head = new ItemEntity(level(), getX()-0.2, getY(), getZ()-0.2, new ItemStack(ModItems.THE_GREAT_HEAD_ITEM.get(), 1));
                level().addFreshEntity(baton);
                level().addFreshEntity(head);

                ExperienceOrb.award((ServerLevel) this.level(), this.position(), 100);
                this.discard();
            }

            //TPS
            if (stateTime == 26) {
                double d0 = getX() + (this.random.nextDouble() - 0.5) * 4.0;
                double d1 = getY() - 1;
                double d2 = getZ() + (this.random.nextDouble() - 0.5) * 4.0;

                this.moveTo(d0, d1, d2);
            } else if (stateTime == 54) {
                double d0 = getX() + (this.random.nextDouble() - 0.5) * 4.0;
                double d1 = getY() + 2;
                double d2 = getZ() + (this.random.nextDouble() - 0.5) * 4.0;

                this.moveTo(d0, d1, d2);
            } else if (stateTime == 85) {
                double d0 = getX() + (this.random.nextDouble() - 0.5) * 4.0;
                double d1 = getY() - 2;
                double d2 = getZ() + (this.random.nextDouble() - 0.5) * 4.0;

                this.moveTo(d0, d1, d2);

            } else if (stateTime == 112) {
                this.moveTo(diePos.getX(), diePos.getY(), diePos.getZ());
            }

            // PARTICLES
            if (stateTime % 5 == 0) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                        getX(), getY()+0.2, getZ(), 20, 0.1, 0.1, 0.1, 0.15);
            }
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

    }

    private void playStateSound(SoundEvent soundEvent) {
        level().playSound(null, blockPosition(),soundEvent, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    private int getNextAttack() {
        int nextAttack = -1;
        int canAttack = 0;

        if (consecutiveAttacks < getMinAttacks() || isFinalPhase()) canAttack = 1;

        if (isSecondPhase()) {
            if (this.entityData.get(PHASE) <= 1) {
                nextAttack = 7;
                this.entityData.set(PHASE, 2);
            } else {
                int repeats = 0;
                if (consecutiveAttacks > getMaxPity()) {
                    repeats = (int) (1 + (consecutiveAttacks - getMaxPity()) * 1.25f);
                }

                for (int i = 0; i <= repeats && nextAttack != 1; i++) {
                    nextAttack = level().getRandom().nextInt(1 + canAttack, 8);
                }
            }
        } else {
            int repeats = 0;
            if (consecutiveAttacks > getMaxPity()) {
                repeats = (int) (1 + (consecutiveAttacks - getMaxPity()) * 1.25f);
            }
            for (int i = 0; i <= repeats && nextAttack != 1; i++) {
                nextAttack = level().getRandom().nextInt(1 + canAttack, 7);
            }
        }

        return nextAttack;
    }

    private void spawnSkeleton(int x, int y, int z, EntityType<? extends AbstractSkeleton> skeletonType) {
        AbstractSkeleton skeleton;
        ItemStack instrument = getRandomInstrument();
        skeleton = skeletonType.spawn((ServerLevel) level(), new BlockPos(x, y, z), MobSpawnType.MOB_SUMMONED);
        ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL, x, y, z,
                50, 0.1, 0.5, 0.1, 0.1);
        skeleton.setItemSlot(EquipmentSlot.MAINHAND, instrument);
        skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    }

    private void setNewState(ComposerBossState newState) {
        switch (newState) {
            case NORMAL_ATTACK -> {
                trigger("normal_attack", false);
            }
            case POISON_ATTACK -> {
                trigger("poison_attack", false);
            }
            case SUMMON_ATTACK -> {
                trigger("summon_attack", false);
            }
            case MELEE_ATTACK -> {
                trigger("melee_attack", false);
            }
            case CANON_ATTACK -> {
                trigger("canon_attack", false);
            }
            case LAUGH_ATTACK -> {
            }
            case NOTE_ATTACK -> {
            }
        }

        setStateId(newState);

        if (isFinalPhase() && newState != ComposerBossState.DYING) {
            teleport();
        }

        this.stateTime = 0;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypes.FALL)) return true;
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        ComposerBossState state = getState(stateId);

        if (isFinalPhase() &&
                state != ComposerBossState.CANON_ATTACK &&
                !isInvalidState(state) &&
                getHealth() - amount > 0) {
            // If final phase, teleport
            if (this.isInvulnerableTo(source)) {
                return false;
            } else {
                boolean flag = source.getDirectEntity() instanceof ThrownPotion;
                if (!flag) {
                    boolean flag2 = super.hurt(source, amount);
                    if (!this.level().isClientSide() && (source.getEntity() instanceof LivingEntity)) {
                        this.teleport();
                    }

                    return flag2;
                } else {
                    for (int i = 0; i < 64; i++) {
                        if (this.teleport()) {
                            return true;
                        }
                    }

                    return flag;
                }
            }
        }
        if (getHealth() - amount <= 0 && !isInvalidState(state)) {
            // If final hit in phase 2, fake die
            if (this.entityData.get(PHASE) <= 2) {
                setNewState(ComposerBossState.FAKE_DYING);
                this.attackCooldown = IDLE_ATTACK_COOLDOWN;
                this.bossEvent.setVisible(false);
                this.setHealth(0.01f);
                return true;
            } else if (this.entityData.get(PHASE) >= 3) {
                setNewState(ComposerBossState.DYING);
                this.setHealth(0.01f);
                return true;
            }

        } else if (state == ComposerBossState.DEAD && !source.getMsgId().equalsIgnoreCase("generickill")) {
            // If fake dead, send message
            if (source.getEntity() instanceof Player player) {
                player.displayClientMessage(Component.translatable("text.faunaandorchestra.pick_up_composer"), true);
            }
            return false;
        } else if (isInvalidState(state) || isSpawning()) {
            return false;
        } else if (state != ComposerBossState.WEAK && !source.getMsgId().equalsIgnoreCase("generickill")) {
            // If phase one or two, dodge
            composerController.transitionLength(1);
            trigger("dodge", true);
            if (source.getEntity() instanceof Player player) {
                player.displayClientMessage(Component.translatable("dialogue.faunaandorchestra.the_great_composer3"), true);
            }

            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.25, getZ(),
                        7, 0.3, 0.15, 0.3, 0.05);
            }

            return false;
        }

        // Do the damage
        return super.hurt(source, amount);
    }

    protected boolean teleport() {
        Optional<ServerPlayer> player = bossEvent.getPlayers().stream().findAny();
        if (!this.level().isClientSide() && this.isAlive() && player.isPresent()) {
            BlockPos pos = player.get().blockPosition();
            double d0 = pos.getX() + (this.random.nextDouble() - 0.5) * 32.0;
            double d1 = pos.getY() + random.nextInt(2, 5);
            double d2 = pos.getZ() + (this.random.nextDouble() - 0.5) * 32.0;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(x, y, z);

        while (!this.level().getBlockState(mutableBlockPos).isAir()) {
            mutableBlockPos.move(Direction.UP);
        }

        BlockState blockstate = this.level().getBlockState(mutableBlockPos);
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (!flag1) {
            this.moveTo(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ());
            this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }


    public static ComposerBossState getState(int id) {
        if (id == -1) {
            System.err.println("Tried to get an invalid state!");
        }
        return switch (id) {
            case 1 -> ComposerBossState.NORMAL_ATTACK;
            case 2 -> ComposerBossState.POISON_ATTACK;
            case 3 -> ComposerBossState.LAUGH_ATTACK;
            case 4 -> ComposerBossState.SUMMON_ATTACK;
            case 5 -> ComposerBossState.MELEE_ATTACK;
            case 6 -> ComposerBossState.NOTE_ATTACK;
            case 7 -> ComposerBossState.CANON_ATTACK;
            case 8 -> ComposerBossState.SHOCK;
            case 9 -> ComposerBossState.WEAK;
            case 10 -> ComposerBossState.DEAD;
            case 11 -> ComposerBossState.FAKE_DYING;
            case 12 -> ComposerBossState.RESURRECTING;
            case 13 -> ComposerBossState.DYING;
            default -> ComposerBossState.IDLE;
        };
    }

    public void setStateId(ComposerBossState state) {
        int id = switch (state) {
            case NORMAL_ATTACK -> 1;
            case POISON_ATTACK -> 2;
            case LAUGH_ATTACK -> 3;
            case SUMMON_ATTACK -> 4;
            case MELEE_ATTACK -> 5;
            case NOTE_ATTACK -> 6;
            case CANON_ATTACK -> 7;
            case SHOCK -> 8;
            case WEAK -> 9;
            case DEAD -> 10;
            case FAKE_DYING -> 11;
            case RESURRECTING -> 12;
            case DYING -> 13;
            default -> 0;
        };
        this.entityData.set(STATE, id);
    }

    public boolean isBusy() {
        return getState(stateId) != ComposerBossState.IDLE;
    }

    public void trigger(String animation, boolean dirty) {
        if (dirty) this.scheduleDirty = 5;
        else composerController.transitionLength(5);

        if (isFinalPhase() && getState(stateId) != ComposerBossState.DYING) switch (animation) {
            case "melee_attack" -> animation = "melee_head";
            case "canon_attack" -> animation = "canon_head";
            default -> animation = "attack_head";
        }

        triggerAnim("composer_controller", animation);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (!this.isDeadOrDying()) {
            player.displayClientMessage(Component.translatable("text.faunaandorchestra.leave_composer"), true);
            player.playNotifySound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.AMBIENT, 1.0F, 1.0F);
        }
        this.bossEvent.removePlayer(player);
    }

    @Override
    public boolean isPushable() {
        return false;
    }


    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public void knockback(double strength, double x, double z) {

    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    protected boolean isInvalidState(ComposerBossState state) {
        return state == ComposerBossState.DYING ||
                state == ComposerBossState.FAKE_DYING ||
                state == ComposerBossState.RESURRECTING ||
                state == ComposerBossState.DEAD;
    }

    public boolean isSecondPhase() {
        return getHealth() / getMaxHealth() <= 0.5;
    }

    public enum ComposerBossState {
        IDLE,
        NORMAL_ATTACK,
        POISON_ATTACK,
        LAUGH_ATTACK,
        SUMMON_ATTACK,
        MELEE_ATTACK,
        NOTE_ATTACK,
        CANON_ATTACK,
        SHOCK,
        WEAK,
        DEAD,
        FAKE_DYING,
        RESURRECTING,
        DYING,
    }


    public int getRepels() {
        return entityData.get(REPELS);
    }

    public void decreaseRepels() {
        this.repels -= 1;
        entityData.set(REPELS, repels);
    }

    protected void setRepels() {
        float percHealth = getHealth() / getMaxHealth();
        if (percHealth > 0.75) {
            this.repels = 2; // * bossEvent.getPlayers().size();
        } else if (percHealth >= 0.5 && percHealth < 0.75) {
            this.repels = 4;
        } else if (percHealth >= 0.25 && percHealth < 0.5) {
            this.repels = 6;
        } else {
            this.repels = 8;
        }
        entityData.set(REPELS, repels);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WARDEN_HURT;
    }

    public void setSpawnPos(BlockPos pos) {
        this.entityData.set(DEFAULT_POSITION, pos.above(5));
    }

    public boolean isFakeDead() {
        return entityData.get(PHASE) == 3;
    }

    public boolean isFinalPhase() {
        return entityData.get(PHASE) == 4;
    }
    public boolean isSpawning() { return entityData.get(PHASE) == 0; }

    public int getSpawnTime() {
        return this.spawnDialogueTime;
    }

    public ComposerBossState getState() {
        return getState(stateId);
    }

    public boolean shouldDisplayDialogue() {
        return spawnDialogueTime > -1;
    }

    public void resetDialogueTimer() {
        this.spawnDialogueTime = -1;
    }

    public ItemStack getRandomInstrument() {
        Item item;
        item = instrumentList.get(random.nextInt(instrumentList.size()));
        ItemStack stack = new ItemStack(item);
        stack.setDamageValue(20);
        return stack;
    }

    public int getCooldownTicks() {
        if (isFinalPhase()) {
            return IDLE_ATTACK_COOLDOWN - 30;
        } else if (isSecondPhase()) {
            return IDLE_ATTACK_COOLDOWN - 20;
        } else {
            return IDLE_ATTACK_COOLDOWN;
        }
    }

    public int getMinAttacks() {
        return isSecondPhase() ? 4 : 2;
    }

    public int getMaxPity() {
        return isSecondPhase() ? 7 : 5;
    }
}
