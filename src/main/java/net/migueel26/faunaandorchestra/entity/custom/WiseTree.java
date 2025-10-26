package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModSavedData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WiseTree extends TamableAnimal implements GeoEntity, TalkableEntity, Npc {
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation DROP = RawAnimation.begin().thenPlay("drop");
    protected static final EntityDataAccessor<Integer> DIALOGUE_TIMER = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> CONFIDENCE = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> LIFE_STAGE = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.INT);
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/wise_tree_icon.png");
    public String currentDialogue;
    public static final String RESOURCE = "dialogue.faunaandorchestra.wise_tree";
    private final AnimationController<WiseTree> wiseTreeController = new AnimationController<>(this, "wise_tree_controller", 5, this::wiseTreeState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public WiseTree(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DIALOGUE_TIMER, 0);
        builder.define(CONFIDENCE, 0);
        builder.define(GOOD_MORNING, true);
        builder.define(LIFE_STAGE,1);

        super.defineSynchedData(builder);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        if (getLifeStage() == 1) {
            return EntityDimensions.scalable(0.6f, 1.0f);
        } else if (getLifeStage() == 2) {
            return EntityDimensions.scalable(1.5f, 2.25f);
        } else {
            return EntityDimensions.scalable(3.0f, 5.0f);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.is(ModItems.GLOVE)) {
            setLifeStage(3);
            refreshDimensions();
        } else if (getLifeStage() == 3 && getDialogueTimer() == 0) {
            if (level().isClientSide()) {
                increaseDialogueTimer();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private <E extends GeoAnimatable> PlayState wiseTreeState(AnimationState<E> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("LifeStage", getLifeStage());

        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("LifeStage")) {
            setLifeStage(compound.getInt("LifeStage"));
        }

        super.readAdditionalSaveData(compound);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && player.getMainHandItem().getItem() instanceof AxeItem) {
            double speed = 0.25D;
            double motionX = (level().random.nextDouble() - 0.5D) * 2.0D * speed;
            double motionY = level().random.nextDouble() * speed + 0.1D;
            double motionZ = (level().random.nextDouble() - 0.5D) * 2.0D * speed;

            ItemEntity item = new ItemEntity(level(), getX(), getY()+1, getZ(), new ItemStack(Items.OAK_LOG, random.nextInt(1, 4)),
                    motionX, motionY, motionZ);
            item.setPos(getX(), getY()+1, getZ());
            level().addFreshEntity(item);
            return super.hurt(source, amount);
        } else {
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.25, getZ(),
                        7, 0.3, 0.15, 0.3, 0.05);
            }
            return false;
        }

    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return getLifeStage() == 3 ? ModSounds.WISE_TREE_AMBIENT.get() : super.getAmbientSound();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WOOD_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOOD_BREAK;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 80d)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.FOLLOW_RANGE, 24D);
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

    public int getLifeStage() {
        return entityData.get(LIFE_STAGE);
    }

    public void setLifeStage(int stage) {
        entityData.set(LIFE_STAGE, stage);
    }

    @Override
    public int getTextBoxOffset() {
        return 8;
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
        if (getDialogueTimer() <= 5) {
            if (goodMorning) {
                dialogue = Component.translatable(RESOURCE + "0").getString();
            } else {
                int randomDialogue = random.nextInt(1, 21);
                dialogue = Component.translatable(RESOURCE + randomDialogue).getString();
            }
            //if (getOwner() == null || !getOwner().is(player)) dialogue = "§k" + dialogue;
            currentDialogue = dialogue;
        }
        return dialogue;
    }

    @Override
    public Pair<Integer, Integer> getIconSize() {
        return new Pair<>(111, 102);
    }

    @Override
    public Pair<Integer, Integer> getIconLocation() {
        return new Pair<>(71, 94);
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

    @Override
    public void setGoodMorning(boolean goodMorning) {
        entityData.set(GOOD_MORNING, goodMorning);
    }

    @Override
    public boolean getGoodMorning() {
        return entityData.get(GOOD_MORNING);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(wiseTreeController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
