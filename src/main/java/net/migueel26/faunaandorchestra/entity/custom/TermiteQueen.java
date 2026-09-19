package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TermiteQueen extends AgeableMob implements TalkableEntity, Npc, GeoEntity {
    private final int SCHEDULED_DEATH = 20;
    private final int MAX_TERMITE_MOUNDS_DESTROYED = 3;

    protected static final EntityDataAccessor<Integer> DIALOGUE_TIMER = SynchedEntityData.defineId(TermiteQueen.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(TermiteQueen.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> TERMITE_MOUNDS_DESTROYED = SynchedEntityData.defineId(TermiteQueen.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> HAS_FOUND_CHILDREN = SynchedEntityData.defineId(TermiteQueen.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation EMERGE = RawAnimation.begin().thenPlay("emerge");
    private static final RawAnimation SUBMERGE = RawAnimation.begin().thenPlay("submerge");
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/termite_queen_icon.png");
    public static final String RESOURCE = "dialogue.faunaandorchestra.termite_queen";
    public String currentDialogue;
    private int deathTimer;

    // BABY QUEST
    private final List<UUID> babyUUIDs = new ArrayList<>();
    private boolean hasGivenReward = false; // server only
    //

    private final AnimationController<TermiteQueen> termiteQueenController = new AnimationController<>(this, "termite_queen_controller", 2, this::termiteQueenState)
            .triggerableAnim("submerge", SUBMERGE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public TermiteQueen(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.deathTimer = -1;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DIALOGUE_TIMER, 0);
        builder.define(GOOD_MORNING, true);
        builder.define(TERMITE_MOUNDS_DESTROYED, 0);
        builder.define(HAS_FOUND_CHILDREN, false);
        super.defineSynchedData(builder);
    }

    private <E extends GeoAnimatable> PlayState termiteQueenState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(EMERGE);
        }
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createTermiteQueenAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TermiteMoundsDestroyed", this.entityData.get(TERMITE_MOUNDS_DESTROYED));
        compound.putBoolean("HasGivenReward", this.hasGivenReward);
        compound.putBoolean("HasFoundChildren", this.entityData.get(HAS_FOUND_CHILDREN));
        ListTag uuidList = new ListTag();
        for (UUID uuid : this.babyUUIDs) {
            uuidList.add(NbtUtils.createUUID(uuid));
        }
        compound.put("BabyUUIDs", uuidList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("TermiteMoundsDestroyed")) {
            this.entityData.set(TERMITE_MOUNDS_DESTROYED, compound.getInt("TermiteMoundsDestroyed"));
        }
        this.hasGivenReward = compound.getBoolean("HasGivenReward");
        this.entityData.set(HAS_FOUND_CHILDREN, compound.getBoolean("HasFoundChildren"));
        this.babyUUIDs.clear();
        if (compound.contains("BabyUUIDs", Tag.TAG_LIST)) {
            ListTag uuidList = compound.getList("BabyUUIDs", Tag.TAG_INT_ARRAY);
            for (Tag tag : uuidList) {
                this.babyUUIDs.add(NbtUtils.loadUUID(tag));
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        this.lookControl.setLookAt(player.position());
        if (getDialogueTimer() == 0) {
            if (level().isClientSide()) {
                increaseDialogueTimer();
            } else {
                if (!hasGivenReward() && hasFoundChildren()) {
                    setHasGivenReward(true);

                    Block.popResourceFromFace(level(), blockPosition(), Direction.UP, new ItemStack(ModItems.TERMITE.get(), 1));

                    // whoooshhh
                    ((ServerLevel) level()).sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.TERMITE_MOUND.get().defaultBlockState()),
                            position().x, position().y, position().z,
                            10, 0.2f, 0.2f, 0.2f, 0.2f
                    );
                    this.playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0F, 1.0F + (random.nextFloat() - 0.5f));
                }
            }
            return InteractionResult.SUCCESS;
        }



        return InteractionResult.FAIL;
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            if (deathTimer == SCHEDULED_DEATH) {
                for (TermiteBaby baby : getTermiteBabyList((ServerLevel) this.level())) {
                    baby.submerge();
                }

                this.discard();
            } else if (deathTimer >= 0) {
                if (deathTimer % 2 == 0) {
                    ((ServerLevel) level()).sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.TERMITE_MOUND.get().defaultBlockState()),
                             position().x, position().y, position().z,
                            10, 0.2f, 0.2f, 0.2f, 0.2f
                    );
                }
                if (deathTimer % 5 == 0) {
                    this.playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0F, 1.0F + (random.nextFloat() - 0.5f));
                }
                deathTimer++;
            }

            // BABY QUEST
            if (this.deathTimer < 0 && !this.hasFoundChildren() && this.tickCount % 20 == 0) {
                manageBabies((ServerLevel) this.level());
            }
        }
        super.tick();
    }

    private void manageBabies(ServerLevel serverLevel) {
        List<TermiteBaby> activeBabies = getTermiteBabyList(serverLevel);

        // Spawn the missing babies (if there are any)
        int missingBabies = 3 - babyUUIDs.size();
        for (int i = 0; i < missingBabies; i++) {
            // The babies will spawn in a triangle
            double angle = (Math.PI * 2 / 3.0) * (babyUUIDs.size());
            double radius = 30.0;
            double targetX = this.getX() + Math.cos(angle) * radius;
            double targetZ = this.getZ() + Math.sin(angle) * radius;

            int targetY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) targetX, (int) targetZ);

            TermiteBaby newBaby = ModEntities.TERMITE_BABY.get().create(serverLevel);
            if (newBaby != null) {
                newBaby.setPos(targetX, targetY, targetZ);
                serverLevel.addFreshEntity(newBaby);

                this.babyUUIDs.add(newBaby.getUUID());
                activeBabies.add(newBaby);
            }
        }

        // Check the distance and mark the babies as found if nearby
        int foundCount = 0;
        for (TermiteBaby baby : activeBabies) {
            if (this.distanceToSqr(baby) <= 64.0D) {
                baby.setFound(true);
            }

            if (baby.isFound()) {
                foundCount++;
            }
        }

        // Give the termite reward
        if (foundCount >= 3) {
            this.setHasFoundChildren(true);

            this.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F); // Sonido de victoria

            for (TermiteBaby baby : activeBabies) {
                baby.submerge();
            }

            // Tell the player
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (serverPlayer.distanceToSqr(this) <= 64.0D) {
                    serverPlayer.displayClientMessage(Component.translatable("text.faunaandorchestra.talk_to_queen"), true);
                }
            }

            // Reset dialogue timer
            this.level().broadcastEntityEvent(this, (byte) 64);
        }
    }

    private @NotNull List<TermiteBaby> getTermiteBabyList(ServerLevel serverLevel) {
        List<TermiteBaby> activeBabies = new ArrayList<>();

        // Check if babies have despawned or disappeared and update the list
        Iterator<UUID> iterator = babyUUIDs.iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Entity entity = serverLevel.getEntity(uuid);

            if (entity instanceof TermiteBaby baby && baby.isAlive()) {
                activeBabies.add(baby);
            } else {
                iterator.remove();
            }
        }
        return activeBabies;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 64) {
            this.resetDialogueTimer();

        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        submerge();
        return super.hurt(source, amount);
    }

    public void submerge() {
        if (deathTimer == -1) {
            deathTimer = 0;
        }
        triggerAnim("termite_queen_controller", "submerge");
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    public void knockback(double strength, double x, double z) {

    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SILVERFISH_HURT;
    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    public String getRandomDialogue(Player player) {
        String dialogue = currentDialogue;
        boolean goodMorning = entityData.get(GOOD_MORNING);

        if (getDialogueTimer() <= 5) {
            if (hasFoundChildren()) {
                dialogue = Component.translatable(RESOURCE + "2").getString();
            } else if (goodMorning) {
                dialogue = Component.translatable(RESOURCE + "0").getString();
            } else {
                dialogue = Component.translatable(RESOURCE + "1").getString();
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
        return this.entityData.get(DIALOGUE_TIMER);
    }

    @Override
    public void increaseDialogueTimer() {
        entityData.set(DIALOGUE_TIMER, getDialogueTimer() + 1);
    }

    @Override
    public void resetDialogueTimer() {
        entityData.set(DIALOGUE_TIMER, 0);
    }

    @Override
    public void setGoodMorning(boolean goodMorning) {
        this.entityData.set(GOOD_MORNING, goodMorning);
    }

    @Override
    public boolean getGoodMorning() {
        return this.entityData.get(GOOD_MORNING);
    }

    @Override
    public boolean hasThreeLines() {
        return true;
    }

    public int getTermiteMoundsDestroyed() {
        return this.entityData.get(TERMITE_MOUNDS_DESTROYED);
    }

    public void setTermiteMoundsDestroyed(int termiteMoundsDestroyed) {
        this.entityData.set(TERMITE_MOUNDS_DESTROYED, termiteMoundsDestroyed);
        if (!level().isClientSide()) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.ANGRY_VILLAGER,
                    getX(), getY() + 0.5f, getZ(),
                    3, 0.2f, 0.2f, 0.2f, 0);
            this.makeSound(SoundEvents.VILLAGER_NO);
            if (termiteMoundsDestroyed == MAX_TERMITE_MOUNDS_DESTROYED) {
                submerge();
            }
        }
    }

    public void increaseTermiteMoundsDestroyed() {
        setTermiteMoundsDestroyed(getTermiteMoundsDestroyed() + 1);
    }

    public boolean hasGivenReward() {
        return this.hasGivenReward;
    }

    public void setHasGivenReward(boolean hasGivenReward) {
        this.hasGivenReward = hasGivenReward;
    }

    public boolean hasFoundChildren() {
        return this.entityData.get(HAS_FOUND_CHILDREN);
    }

    public void setHasFoundChildren(boolean hasFoundChildren) {
        this.entityData.set(HAS_FOUND_CHILDREN, hasFoundChildren);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(termiteQueenController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
