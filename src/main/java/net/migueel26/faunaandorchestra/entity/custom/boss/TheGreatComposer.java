package net.migueel26.faunaandorchestra.entity.custom.boss;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.CrawlingDiscordBlock;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.custom.projectile.MusicNoteProjectileEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
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

public class TheGreatComposer extends Mob implements Enemy, GeoEntity {
    protected static final int MAX_HEALTH = 300;
    protected static final int IDLE_ATTACK_COOLDOWN = 80;
    protected static final int THROW_NORMAL_ATTACK_COOLDOWN = 30;
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation DODGE = RawAnimation.begin().thenPlay("dodge");
    public static final RawAnimation NORMAL_ATTACK = RawAnimation.begin().thenPlay("attack");
    public static final RawAnimation POISON_ATTACK = RawAnimation.begin().thenPlay("attack_poison");
    public static final RawAnimation SUMMON_ATTACK = RawAnimation.begin().thenPlay("attack_summon");
    public static final RawAnimation MELEE_ATTACK = RawAnimation.begin().thenPlay("attack_melee");
    public static final RawAnimation LAUGH = RawAnimation.begin().thenPlay("prepare").thenLoop("laugh");
    public static final RawAnimation SHOCK = RawAnimation.begin().thenPlay("shock");
    public static final RawAnimation WEAK = RawAnimation.begin().thenPlay("weak");
    public static final RawAnimation AWAIT = RawAnimation.begin().thenPlay("attack_await");
    public static final RawAnimation REPEL = RawAnimation.begin().thenPlay("repel");
    public final AnimationController<TheGreatComposer> composerController = new AnimationController<>(this, "composer_controller", 5, this::composerState)
            .triggerableAnim("dodge", DODGE)
            .triggerableAnim("normal_attack", NORMAL_ATTACK)
            .triggerableAnim("poison_attack", POISON_ATTACK)
            .triggerableAnim("summon_attack", SUMMON_ATTACK)
            .triggerableAnim("melee_attack", MELEE_ATTACK)
            .triggerableAnim("repel", REPEL);
    public static EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(TheGreatComposer.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<Integer> REPELS = SynchedEntityData.defineId(TheGreatComposer.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<BlockPos> DEFAULT_POSITION = SynchedEntityData.defineId(TheGreatComposer.class, EntityDataSerializers.BLOCK_POS);
    protected int attackCooldown = IDLE_ATTACK_COOLDOWN;
    ////////// CLIENT AND SERVER
    protected int stateId;
    protected int commonCounter = 0;
    /////////// WORKS DIFFERENTLY IN SERVER AND CLIENT
    protected int stateTime = 0;
    private double velocityY = 0.0; // Velocidad vertical propia
    //////////// CLIENT ONLY
    int scheduleDirty = -1;
    boolean dirty = false;
    //////////// SERVER ONLY
    // the Composer will repel n-1 times
    protected int repels = 2;
    protected float healthBefore;
    protected List<? extends Holder<MobEffect>> effectsList = new ArrayList<>(List.of(
            ModEffects.BOOGIE_EFFECT,
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
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS
    );
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public TheGreatComposer(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        bossEvent.setCreateWorldFog(true);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0f));
    }

    private <E extends GeoAnimatable> PlayState composerState(AnimationState<E> state) {
        ComposerBossState bossState = getState(stateId);

        updateTransitionLength(bossState);
        switch (bossState) {
            case NORMAL_ATTACK -> state.getController().setAnimation(AWAIT);
            case LAUGH_ATTACK -> state.getController().setAnimation(LAUGH);
            case WEAK -> state.getController().setAnimation(WEAK);
            case SHOCK -> state.getController().setAnimation(SHOCK);
            case null, default -> state.getController().setAnimation(IDLE);
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
        this.entityData.set(DEFAULT_POSITION, blockPosition());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
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
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("X", this.entityData.get(DEFAULT_POSITION).getX());
        compound.putInt("Y", this.entityData.get(DEFAULT_POSITION).getY());
        compound.putInt("Z", this.entityData.get(DEFAULT_POSITION).getZ());

        super.addAdditionalSaveData(compound);
    }

    @Override
    public void tick() {

        if (scheduleDirty > 0) {
            scheduleDirty--;
        } else if (scheduleDirty == 0) {
            dirty = true;
            scheduleDirty = -1;
        }

        if (stateTime % 10 == 0) {
            // Display fire particles under the spine
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(), this.getY() - 0.25F, this.getZ(),
                        5, 0.35, 0.25, 0.35, 0);
            }
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        stateTime++;
        commonCounter++;

        super.tick();
    }

    @Override
    public void aiStep() {
        // We need to update its position both in the client and server
        ComposerBossState state = getState(stateId);

        if (state == ComposerBossState.WEAK) {
            /*while (getBlockStateOn().is(Blocks.AIR) && stateTime % 5 == 0) {
                this.moveTo(this.getX(), this.getY() - 0.2f, this.getZ());
            }*/

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

        if (state == ComposerBossState.IDLE) {
            if (attackCooldown == 0) {
                int rnd = level().getRandom().nextInt(1, 6);
                ComposerBossState newState = getState(rnd);

                setNewState(newState);
            }
        } else if (state == ComposerBossState.NORMAL_ATTACK) {
            if (stateTime == THROW_NORMAL_ATTACK_COOLDOWN) {
                for (Player player : bossEvent.getPlayers()) {
                    // We add the music note projectile
                    Vec3 vec3 = this.getViewVector(1.0F);
                    double d2 = player.getX() - (this.getX() + vec3.x * 4.0);
                    double d3 = player.getY(1) - (0.5 + this.getY(0.5));
                    double d4 = player.getZ() - (this.getZ() + vec3.z * 4.0);
                    Vec3 vec31 = new Vec3(d2, d3, d4);
                    MusicNoteProjectileEntity note = new MusicNoteProjectileEntity(this, vec31.normalize(), level());
                    note.setPos(this.getX() + vec3.x * 1.25, this.getY(0.5), note.getZ() + vec3.z * 1.25);
                    level().addFreshEntity(note);

                    // We establish the repels
                    setRepels();
                }
            }

            if (stateTime > IDLE_ATTACK_COOLDOWN) {
                if (repels == 0) setNewState(ComposerBossState.SHOCK);
                else if (level().getEntitiesOfClass(MusicNoteProjectileEntity.class, this.getBoundingBox().inflate(20)).isEmpty()) {
                    setNewState(ComposerBossState.IDLE);
                    this.attackCooldown = IDLE_ATTACK_COOLDOWN;
                }
            }

        } else if (state == ComposerBossState.SHOCK) {
            if (stateTime == 40) {
                this.healthBefore = getHealth() / getMaxHealth();
                setNewState(ComposerBossState.WEAK);
            }

        } else if (state == ComposerBossState.WEAK) {
            if (stateTime == 100 || (healthBefore - getHealth() / getMaxHealth()) >= 0.25) {
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = IDLE_ATTACK_COOLDOWN;
            }
        } else if (state == ComposerBossState.POISON_ATTACK) {
            if (stateTime == 45) {
                List<LivingEntity> entities = level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, this.getBoundingBox().inflate(20));
                for (LivingEntity entity : entities) {
                    Holder<MobEffect> nextEffect = effectsList.get(random.nextInt(0, effectsList.size()));
                    entity.addEffect(new MobEffectInstance(nextEffect, isSecondPhase() ? 280 : 200, isSecondPhase() ? 2 : 1));
                }
            }
            if (stateTime == 80) {
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = IDLE_ATTACK_COOLDOWN;
            }

        } else if (state == ComposerBossState.LAUGH_ATTACK) {
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

            if (stateTime == CrawlingDiscordBlock.NEW_CHILD_TIME * CrawlingDiscordBlock.MAX_GENERATION + CrawlingDiscordBlock.DIE_TIME + 40) {
                setNewState(ComposerBossState.IDLE);
                this.attackCooldown = IDLE_ATTACK_COOLDOWN + 20;
            }

        } else if (state == ComposerBossState.SUMMON_ATTACK) {
            if (stateTime == 65) {
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
                this.attackCooldown = IDLE_ATTACK_COOLDOWN;
            }

        } else if (state == ComposerBossState.MELEE_ATTACK) {
            if (stateTime == 25) {
                Optional<ServerPlayer> oPlayer = bossEvent.getPlayers().stream().findAny();
                if (oPlayer.isPresent() && oPlayer.get() instanceof ServerPlayer player) {
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                            this.getX(), this.getY(), this.getZ(),
                            60, 0.3, 1, 0.3, 0.1);
                    this.moveTo(player.getBlockX(), player.getBlockY(), player.getBlockZ());
                }
            } else if (stateTime == 30) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL,
                        this.getX(), this.getY(), this.getZ(),
                        60, 0.3, 1, 0.3, 0.1);

            } else if (stateTime >= 45 && stateTime < 100) {
                if (stateTime % 5 == 0) {
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            this.getX(), this.getY(), this.getZ(),
                            40, 1, 0.1, 1, 0.2);
                }

                if (stateTime % 10 == 2) {
                    List<LivingEntity> entities = level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, this.getBoundingBox().inflate(2, 1, 2));
                    for (LivingEntity entity : entities) {
                        entity.hurt(damageSources().mobAttack(this), 10.0F);
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
                this.attackCooldown = IDLE_ATTACK_COOLDOWN;
            }
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

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
            case LAUGH_ATTACK -> {
            }
        }
        setStateId(newState);
        this.stateTime = 0;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getState(stateId) != ComposerBossState.WEAK && !source.getMsgId().equalsIgnoreCase("generickill")) {

            composerController.transitionLength(1);
            trigger("dodge", true);

            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.25, getZ(),
                        7, 0.3, 0.15, 0.3, 0.05);
            }

            return false;
        }
        return super.hurt(source, amount);
    }

    public static ComposerBossState getState(int id) {
        return switch (id) {
            case 1 -> ComposerBossState.NORMAL_ATTACK;
            case 2 -> ComposerBossState.POISON_ATTACK;
            case 3 -> ComposerBossState.LAUGH_ATTACK;
            case 4 -> ComposerBossState.SUMMON_ATTACK;
            case 5 -> ComposerBossState.MELEE_ATTACK;
            case 8 -> ComposerBossState.SHOCK;
            case 9 -> ComposerBossState.WEAK;
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
            case SHOCK -> 8;
            case WEAK -> 9;
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
        SHOCK,
        WEAK
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

    public ItemStack getRandomInstrument() {
        Item item;
        item = instrumentList.get(random.nextInt(instrumentList.size()));
        ItemStack stack = new ItemStack(item);
        stack.setDamageValue(20);
        return stack;
    }
}
