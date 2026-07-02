package net.migueel26.faunaandorchestra.entity.custom.koala_workers;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.goals.*;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.screen.custom.ConductorMenu;
import net.migueel26.faunaandorchestra.screen.custom.FarmerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FarmerKoalaEntity extends AbstractKoalaWorker {
    private static final RawAnimation SLEEP = RawAnimation.begin().thenPlay("sleep");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation USE = RawAnimation.begin().thenPlay("use");
    private static final RawAnimation EAT = RawAnimation.begin().thenPlay("eat");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    AnimationController<FarmerKoalaEntity> controller = new AnimationController<>(this, "farmer_koala_controller", 5, this::koalaState)
            .triggerableAnim("eat", EAT)
            .triggerableAnim("use", USE);
    // TALKABLE ENTITY
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/farmer_koala_icon.png");
    public static final String RESOURCE = "dialogue.faunaandorchestra.farmer_koala";
    // FARMING
    public static final int TICKS_UNTIL_BORED = 200; // 30 segundos * 20 ticks
    protected int ticksWithoutCrops = 0;
    protected static final EntityDataAccessor<Integer> CONSECUTIVE_CROPS = SynchedEntityData.defineId(FarmerKoalaEntity.class, EntityDataSerializers.INT);
    // We use workTime as the lunch break timer, if it's 0 the koala is working, else it's on a break
    public static final int CROPS_UNTIL_BREAK = 30;
    public static final int LUNCH_BREAK_DURATION = 300;
    public static final int EAT_TIME = LUNCH_BREAK_DURATION / 3;
    protected int consecutiveCrops = 0;
    // Inventory
    public ItemStackHandler inventory = new ItemStackHandler(12);
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);

    public FarmerKoalaEntity(EntityType<? extends FarmerKoalaEntity> type, Level level) {
        super(type, level);
    }

    @Override
    Item getKit() {
        return ModItems.FARMING_KIT.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CONSECUTIVE_CROPS, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Farmer Goals
        this.goalSelector.addGoal(2, new FarmerHarvestCropGoal(this));
        this.goalSelector.addGoal(1, new FarmerGoToChestGoal(this));

        // Regular Goals
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5f) {
            FarmerKoalaEntity koala = (FarmerKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation());
            }
        });
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f) {
            final FarmerKoalaEntity koala = (FarmerKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation()) && (koala.isInLunchBreak() || koala.isBored());
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0f) {
            final FarmerKoalaEntity koala = (FarmerKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation()) && (koala.isInLunchBreak() || koala.isBored());
            }
        });
        this.goalSelector.addGoal(5, new FaunaRandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                FarmerKoalaEntity koala = (FarmerKoalaEntity) mob;
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation()) && (koala.isInLunchBreak() || koala.isBored());
            }
        });
    }

    private <E extends GeoAnimatable> PlayState koalaState(AnimationState<E> state) {
        if (isKoalaSleeping() && hasWorkingStation()) {
            state.getController().setAnimation(SLEEP);
        } else if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(CONSECUTIVE_CROPS)) {
            this.consecutiveCrops = this.entityData.get(CONSECUTIVE_CROPS);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("ConsecutiveCrops", this.consecutiveCrops);
        compound.put("Inventory", this.inventory.serializeNBT());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Inventory")) {
            this.inventory.deserializeNBT(compound.getCompound("Inventory"));
        }
        this.entityData.set(CONSECUTIVE_CROPS, compound.getInt("ConsecutiveCrops"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult result = super.mobInteract(player, hand);
        if (result == InteractionResult.SUCCESS) return result;

        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(ModItems.BATON.get()) && !stack.getOrCreateTag().hasUUID(ModDataComponents.MUSICIAN_UUID)) {
            // We link the tailor to the baton
            stack.getOrCreateTag().putUUID(ModDataComponents.MUSICIAN_UUID, this.uuid);
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.WAX_OFF, getX(), getY() + 0.5f, getZ(), 20, 0.2, 0.2, 0.2, 0.05);
            }
            return InteractionResult.SUCCESS;

        } else if (isKoalaSleeping() && hasWorkingStation()) {
            player.displayClientMessage(Component.translatable("text.faunaandorchestra.sleeping_worker_koala"), true);
            return InteractionResult.SUCCESS;

        } else if (hasWorkingStation() && !isInLunchBreak()) {
            // We open the inventory
            this.openCustomMenu(player);
            return InteractionResult.SUCCESS;

        } else if (getDialogueTimer() == 0) {
            // The koala talks
            if (level().isClientSide()) {
                increaseDialogueTimer();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private void openCustomMenu(Player player) {
        if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {

            SimpleMenuProvider menuProvider = new SimpleMenuProvider(
                    (id, playerInventory, playerEntity) -> new FarmerMenu(id, playerInventory, this),
                    this.getDisplayName()
            );

            NetworkHooks.openScreen(serverPlayer, menuProvider, buf -> {
                buf.writeUUID(this.getUUID());
            });
        }
    }

    @Override
    public void tick() {
        // LUNCH BREAK
        if (isInLunchBreak() && !level().isClientSide()) {
            if (workTime == 0) {
                this.setGoodMorning(true);
            }

            if (tickCount % 20 == 0) {
                increaseWorkTime();
            }

            if (workTime == EAT_TIME || workTime == EAT_TIME*2) {
                triggerAnim("farmer_koala_controller", "eat");
            }

            if (tickCount % 4 == 0 && (workTime >= EAT_TIME && workTime <= EAT_TIME + 1) ||
                    (workTime >= EAT_TIME*2 && workTime <= EAT_TIME*2 + 1)) {
                this.playSound(SoundEvents.PANDA_EAT, 1.0F, 0.9F + this.getRandom().nextFloat() * 0.2F);

                if (this.level() instanceof ServerLevel serverLevel) {
                    Vec3 look = this.getLookAngle();
                    double x = this.getX() + look.x * 0.25;
                    double y = this.getEyeY() - 0.15;
                    double z = this.getZ() + look.z * 0.25;

                    serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModItems.GINKGO_BILOBA.get())),
                            x, y, z, 3, 0.1, 0.1, 0.1, 0.05
                    );
                }
            }

            if (workTime >= LUNCH_BREAK_DURATION && !isKoalaSleeping()) {
                resetConsecutiveCrops();
                resetWorkTime();
            }
        }

        if (!level().isClientSide() && hasWorkingStation() && !isInLunchBreak() && !isKoalaSleeping()) {
            if (ticksWithoutCrops < TICKS_UNTIL_BORED) {
                ticksWithoutCrops++;
            }
        }

        super.tick();
    }

    public void harvest() {
        triggerAnim("farmer_koala_controller", "use");
    }

    public void startLunchBreak() {
        this.entityData.set(WORK_TIME, 1);
    }

    public void increaseConsecutiveCrops() {
        this.entityData.set(CONSECUTIVE_CROPS, this.consecutiveCrops + 1);
    }

    public int getConsecutiveCrops() {
        return consecutiveCrops;
    }

    public void resetConsecutiveCrops() {
        this.entityData.set(CONSECUTIVE_CROPS, 0);
    }

    @Override
    public boolean isInLunchBreak() {
        return workTime > 0 && hasWorkingStation();
    }

    @Override
    public boolean isWorking() {
        return workTime == 0 && hasWorkingStation();
    }

    public void resetBoredom() {
        this.ticksWithoutCrops = 0;
    }

    public boolean isBored() {
        return !hasWorkingStation() || ticksWithoutCrops >= TICKS_UNTIL_BORED;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // We reset the working station so the koala stops working and goes back to its normal goals
        setWorkingStation(BlockPos.ZERO);
        return super.hurt(source, amount);
    }

    @Override
    public boolean isWorkingStation(BlockState state) {
        return state.is(Tags.Blocks.CHESTS);
    }

    @Override
    public void onStartListening(ConductorEntity conductor) {
        setKoalaSleeping(false);
    }

    @Override
    public void onStopListening() {
        setKoalaSleeping(true);
    }

    @Override
    public boolean isListening() {
        return !isKoalaSleeping();
    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    public String getRandomDialogue(Player player) {
        // NOTE: % is the koala's name
        String dialogue = currentDialogue;
        boolean goodMorning = entityData.get(GOOD_MORNING);
        if (getDialogueTimer() <= 5) {
            if (hasWorkingStation()) {
                if (isInLunchBreak()) {
                    if (goodMorning) {
                        // Hey boss
                        dialogue = Component.translatable(RESOURCE + "2").getString();
                    } else {
                        int randomDialogue = random.nextIntBetweenInclusive(3, 20);
                        dialogue = Component.translatable(RESOURCE + randomDialogue).getString();
                    }
                }
            } else {
                // Guide me to the table
                dialogue = Component.translatable(RESOURCE + "0").getString();
            }
            currentDialogue = dialogue;
        }
        return dialogue;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryOptional.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryOptional.invalidate();
    }

    @Override
    public Pair<Integer, Integer> getIconLocation() {
        return new Pair<>(101, 136);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
