package net.migueel26.faunaandorchestra.entity.custom.koala_workers;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.MelomancyCauldronBlock;
import net.migueel26.faunaandorchestra.block.custom.SewingMachineBlock;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.entity.goals.MelomancerGoToCauldronGoal;
import net.migueel26.faunaandorchestra.entity.goals.MelomancerGoToChestGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.recipe.MelomancyRecipe;
import net.migueel26.faunaandorchestra.recipe.ModRecipes;
import net.migueel26.faunaandorchestra.recipe.SizedIngredient;
import net.migueel26.faunaandorchestra.screen.custom.MelomancerMenu;
import net.migueel26.faunaandorchestra.util.BlocksUtil;
import net.migueel26.faunaandorchestra.util.RecipesUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MelomancerKoalaEntity extends AbstractKoalaWorker {
    private static final RawAnimation SLEEP = RawAnimation.begin().thenPlay("sleep");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation MIX = RawAnimation.begin().thenPlay("mix");
    private static final RawAnimation EAT = RawAnimation.begin().thenPlay("eat");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    AnimationController<MelomancerKoalaEntity> controller = new AnimationController<>(this, "melomancer_koala_controller", 5, this::koalaState)
            .triggerableAnim("eat", EAT);
    // TALKABLE ENTITY
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/melomancer_koala_icon.png");
    public static final String RESOURCE = "dialogue.faunaandorchestra.melomancer_koala";
    // MIXING
    public static final int LIQUID_MUSIC_SLOT = 6;
    public static final int CATALYST_SLOT = 7;
    public static final int OUTPUT_SLOT = 8;
    public static final int MAX_WORK_TIME = MelomancyCauldronBlockEntity.DEFAULT_COOK_TIME / 20;
    protected static final EntityDataAccessor<Integer> CURRENT_STATE = SynchedEntityData.defineId(MelomancerKoalaEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<BlockPos> CAULDRON_POS = SynchedEntityData.defineId(MelomancerKoalaEntity.class, EntityDataSerializers.BLOCK_POS);
    protected BlockPos cauldronPos;
    // This is the "real" inventory
    public ItemStackHandler inventory = getNewInventory();
    // This is the inventory after mixing
    protected ItemStackHandler nextInventory = getNewInventory();
    // Lock for the inventory
    private boolean isUpdatingRecipe = false;
    // Every 3 items take a well-deserved break
    public static final int ITEMS_UNTIL_BREAK = 3;
    public static final int LUNCH_BREAK_DURATION = 60;//300;
    public static final int EAT_TIME = LUNCH_BREAK_DURATION / 3;
    protected int consecutiveItems = 0;

    public MelomancerKoalaEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CURRENT_STATE, MelomancerState.NOTHING.getId());
        builder.define(CAULDRON_POS, BlockPos.ZERO);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Melomancer Goals
        this.goalSelector.addGoal(1, new MelomancerGoToCauldronGoal(this));
        this.goalSelector.addGoal(1, new MelomancerGoToChestGoal(this));

        // Regular Goals
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5f) {
            MelomancerKoalaEntity koala = (MelomancerKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation());
            }
        });
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f) {
            final MelomancerKoalaEntity koala = (MelomancerKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation()) && (koala.isDoingNothing() || koala.isInLunchBreak());
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0f) {
            final MelomancerKoalaEntity koala = (MelomancerKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation()) && (!koala.hasWorkingStation() || koala.isInLunchBreak() || koala.isDoingNothing());
            }
        });
        this.goalSelector.addGoal(5, new FaunaRandomLookAroundGoal(this));
    }

    private <E extends GeoAnimatable> PlayState koalaState(AnimationState<E> state) {
        if (isKoalaSleeping() && hasWorkingStation()) {
            state.getController().setAnimation(SLEEP);
        } else if (isMixing() && !isInLunchBreak()) {
            state.getController().setAnimation(MIX);
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
        if (key.equals(CAULDRON_POS)) {
            BlockPos pos = this.entityData.get(CAULDRON_POS);
            this.cauldronPos = pos.equals(BlockPos.ZERO) || !level().getBlockState(pos).is(ModBlocks.MELOMANCY_CAULDRON) ? null : pos;
        }
    }

    public void tryToMix() {
        if (isUpdatingRecipe) return;

        isUpdatingRecipe = true;

        try {
            MelomancyRecipe recipe = null;
            RecipeManager recipeManager = level().getRecipeManager();

            // Three liquid music needed
            if (inventory.getStackInSlot(LIQUID_MUSIC_SLOT).getCount() < 3) {
                if (getState() == MelomancerState.GOING_TO_MIX) {
                    // If now he can't do it, he stops doing stuff
                    setState(MelomancerState.NOTHING);
                    RecipesUtil.clearContents(nextInventory);
                }
                return;
            }

            // Try to find the catalyst
            for (RecipeHolder<MelomancyRecipe> holder : recipeManager.getAllRecipesFor(ModRecipes.MELOMANCY_TYPE.get())) {
                if (ItemStack.isSameItem(holder.value().catalyst(), inventory.getStackInSlot(CATALYST_SLOT))) {
                    // The catalyst is the same
                    recipe = holder.value();
                    break;
                }
            }

            if (recipe != null) {
                List<ItemStack> inventoryList = RecipesUtil.toList(inventory);

                List<ItemStack> remainingInventory = calculateRemaining(inventoryList, recipe.ingredients());

                if (remainingInventory != null) {
                    // We've got the new inventory
                    setState(MelomancerState.GOING_TO_MIX);
                    RecipesUtil.listToInventory(remainingInventory, nextInventory);

                    // We place in the hidden slot the output
                    nextInventory.setStackInSlot(OUTPUT_SLOT, recipe.output());
                }
            } else if (getState() == MelomancerState.GOING_TO_MIX) {
                // If now he can't do it, he stops doing stuff
                setState(MelomancerState.NOTHING);
                RecipesUtil.clearContents(nextInventory);
            }
        } finally {
            isUpdatingRecipe = false;
        }
    }

    private List<ItemStack> calculateRemaining(List<ItemStack> inventoryList, List<SizedIngredient> recipeIngredients) {
        List<ItemStack> simulatedInventory = new ArrayList<>();
        for (ItemStack stack : inventoryList) {
            simulatedInventory.add(stack.copy());
        }

        for (SizedIngredient required : recipeIngredients) {
            int amountNeeded = required.amount();

            for (ItemStack currentStack : simulatedInventory) {
                if (currentStack.isEmpty()) continue;

                if (required.ingredient().test(currentStack)) {
                    int deduct = Math.min(amountNeeded, currentStack.getCount());
                    currentStack.shrink(deduct);
                    amountNeeded -= deduct;

                    if (amountNeeded <= 0) {
                        break;
                    }
                }
            }

            if (amountNeeded > 0) {
                return null;
            }
        }

        return simulatedInventory;
    }

    public void startToMix() {
        // The koala starts mixing, the inventory is locked up
        this.setState(MelomancerState.MIXING);

        if (!this.level().isClientSide()) {
            for (Player player : this.level().players()) {
                if (player.containerMenu instanceof MelomancerMenu melomancerMenu && melomancerMenu.melomancer == this) {

                    player.closeContainer();
                }
            }
        }

        // We update the cauldron
        if (level().getBlockEntity(cauldronPos) instanceof MelomancyCauldronBlockEntity cauldron) {
            level().setBlock(cauldronPos, cauldron.getBlockState()
                    .setValue(MelomancyCauldronBlock.COOKING, true)
                    .setValue(MelomancyCauldronBlock.LIQUID, 3), 3);


        }
    }

    @Override
    public void tick() {
        // MIXING
        if (isMixing() && getCauldronBE() != null && !level().isClientSide() && !isKoalaSleeping()) {
            if (tickCount <= 20 || workTime <= 1) {
                this.lookAt(EntityAnchorArgument.Anchor.FEET, cauldronPos.getCenter());
            }

            if (tickCount % 20 == 0) {
                increaseWorkTime();
            }

            MelomancyCauldronBlockEntity.particleTick(level(), cauldronPos, getCauldronBlockState(), getCauldronBE());

            if (workTime >= MAX_WORK_TIME) {
                finishMixing();
            }
        }

        // LUNCH BREAK
        if (isInLunchBreak() && !level().isClientSide()) {
            if (workTime == 0) {
                this.setGoodMorning(true);
            }

            if (tickCount % 20 == 0) {
                increaseWorkTime();
            }

            if (workTime == EAT_TIME || workTime == EAT_TIME*2) {
                triggerAnim("melomancer_koala_controller", "eat");
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
                setState(MelomancerState.GOING_TO_CHEST);
                resetWorkTime();
            }
        }

        super.tick();
    }

    private void finishMixing() {
        if (getCauldronBE() != null && getCauldronBlockState() != null) {
            // Clear the contents of the cauldron
            level().setBlock(cauldronPos, getCauldronBlockState().setValue(MelomancyCauldronBlock.COOKING, false).setValue(MelomancyCauldronBlock.LIQUID, 0), 3);
            this.setCauldronPos(BlockPos.ZERO);

            // Replace the inventory
            List<ItemStack> resultList = RecipesUtil.toList(nextInventory);
            RecipesUtil.listToInventory(resultList, this.inventory);

            this.inventory.extractItem(LIQUID_MUSIC_SLOT, 3, false);
            this.inventory.extractItem(CATALYST_SLOT, 1, false);

            // Clear the "next inventory" because its contents are now in the main one
            RecipesUtil.clearContents(nextInventory);

            // Reset the koala and send to chest
            this.resetWorkTime();
            this.consecutiveItems++;

            // Lunch break or go to chest
            if (consecutiveItems == ITEMS_UNTIL_BREAK) {
                this.setState(MelomancerState.LUNCH_BREAK);
                this.consecutiveItems = 0;
            } else {
                this.setState(MelomancerState.GOING_TO_CHEST);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide() && this.hasWorkingStation()) {
            if (getState() == MelomancerState.MIXING) {
                onLeaveCauldronWhileMixing(this, false);

            } else if (getState() == MelomancerState.GOING_TO_CHEST || getState() == MelomancerState.GOING_TO_MIX || getState() == MelomancerState.LUNCH_BREAK) {
                // We drop all the items
                BlocksUtil.dropContents(level(), blockPosition(), inventory);
            }

            this.setCauldronPos(BlockPos.ZERO);

            RecipesUtil.clearContents(inventory);
            RecipesUtil.clearContents(nextInventory);
        }
        setState(MelomancerState.NOTHING);
        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        super.knockback(strength, x, z);
    }

    public static void onLeaveCauldronWhileMixing(MelomancerKoalaEntity melomancerKoala, boolean destroyed) {
        // Clear the contents of the cauldron
        if (!destroyed && melomancerKoala.getCauldronBE() != null && melomancerKoala.getCauldronBlockState() != null) {
            melomancerKoala.level().setBlock(melomancerKoala.getCauldronPos(), melomancerKoala.getCauldronBlockState()
                    .setValue(MelomancyCauldronBlock.COOKING, false)
                    .setValue(MelomancyCauldronBlock.LIQUID, 0), 3);
        }

        // We drop all the new items except the output
        melomancerKoala.nextInventory.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
        BlocksUtil.dropContents(melomancerKoala.level(), melomancerKoala.blockPosition(), melomancerKoala.nextInventory);

        melomancerKoala.setCauldronPos(BlockPos.ZERO);
    }

    private ItemStackHandler getNewInventory() {
        return new ItemStackHandler(9) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return (slot == LIQUID_MUSIC_SLOT) == stack.is(ModItems.MUSIC_BOTTLE);
            }

            @Override
            protected void onContentsChanged(int slot) {
                if (slot < 8) {
                    tryToMix();
                }
            }
        };
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(ModItems.BATON) && stack.get(ModDataComponents.MUSICIAN_UUID) == null) {
            // We link the tailor to the baton
            stack.set(ModDataComponents.MUSICIAN_UUID, this.uuid);
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.WAX_OFF, getX(), getY() + 0.5f, getZ(), 20, 0.2, 0.2, 0.2, 0.05);
            }
            return InteractionResult.SUCCESS;

        } else if (isKoalaSleeping() && hasWorkingStation()) {
            player.displayClientMessage(Component.translatable("text.faunaandorchestra.sleeping_worker_koala"), true);
            return InteractionResult.SUCCESS;

        } else if (hasWorkingStation() && !isMixing() && !isInLunchBreak()) {
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
        if (!this.level().isClientSide()) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) ->
                    new MelomancerMenu(id, playerInventory, this), this.getDisplayName()), buf -> {
                buf.writeUUID(getUUID());
            });
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        NbtUtils.readBlockPos(compound, "Cauldron").ifPresent(pos -> setCauldronPos(pos));
        if (compound.contains("CurrentState")) {
            this.entityData.set(CURRENT_STATE, compound.getInt("CurrentState"));
        }
        if (compound.contains("Inventory")) {
            this.inventory.deserializeNBT(this.registryAccess(), compound.getCompound("Inventory"));
        }
        if (compound.contains("NextInventory")) {
            this.nextInventory.deserializeNBT(this.registryAccess(), compound.getCompound("NextInventory"));
        }
        if (compound.contains("ConsecutiveItems")) {
            this.consecutiveItems = compound.getInt("ConsecutiveItems");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Inventory", this.inventory.serializeNBT(this.registryAccess()));
        compound.put("NextInventory", this.nextInventory.serializeNBT(this.registryAccess()));
        compound.putInt("CurrentState", getState().getId());
        compound.putInt("ConsecutiveItems", this.consecutiveItems);
        if (cauldronPos != null && level().getBlockState(cauldronPos).is(ModBlocks.MELOMANCY_CAULDRON)) {
            compound.put("Cauldron", NbtUtils.writeBlockPos(this.cauldronPos));
        }
    }

    public BlockPos getCauldronPos() {
        return cauldronPos;
    }

    public BlockState getCauldronBlockState() {
        if (cauldronPos != BlockPos.ZERO && cauldronPos != null) {
            BlockState state = level().getBlockState(cauldronPos);
            if (state.is(ModBlocks.MELOMANCY_CAULDRON.get())) {
                return state;
            }
        }
        return null;
    }

    public MelomancyCauldronBlockEntity getCauldronBE() {
        if (cauldronPos != BlockPos.ZERO && cauldronPos != null) {
            if (level().getBlockEntity(cauldronPos) instanceof MelomancyCauldronBlockEntity cauldron) {
                return cauldron;
            }
        }
        return null;
    }

    public void setCauldronPos(BlockPos cauldronPos) {
        this.entityData.set(CAULDRON_POS, cauldronPos);
        this.cauldronPos = cauldronPos == BlockPos.ZERO ? null : cauldronPos;
    }

    public MelomancerState getState() {
        return MelomancerState.byId(entityData.get(CURRENT_STATE));
    }

    public boolean isMixing() {
        return getState() == MelomancerState.MIXING;
    }

    public void setState(MelomancerState state) {
        entityData.set(CURRENT_STATE, state.getId());
    }

    public boolean isDoingNothing() {
        return getState() == MelomancerState.NOTHING;
    }

    @Override
    public boolean isInLunchBreak() {
        return getState() == MelomancerState.LUNCH_BREAK;
    }

    @Override
    public boolean isWorking() {
        return !isDoingNothing();
    }

    @Override
    public boolean isWorkingStation(BlockState state) {
        return state.is(Tags.Blocks.CHESTS);
    }

    @Override
    public boolean isListening() {
        return !isKoalaSleeping();
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
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    public String getRandomDialogue(Player player) {
        // NOTE: % is the koala's name
        String dialogue = currentDialogue;
        boolean goodMorning = entityData.get(GOOD_MORNING);
        if (getDialogueTimer() <= 5) {
            if (isMixing() || isInLunchBreak()) {
                if (isInLunchBreak()) {
                    if (goodMorning) {
                        // Hey boss
                        dialogue = Component.translatable(RESOURCE + "2").getString();
                    } else {
                        int randomDialogue = random.nextIntBetweenInclusive(3, 20);
                        dialogue = Component.translatable(RESOURCE + randomDialogue).getString();
                    }
                } else {
                    // I'm working, don't bother me
                    dialogue = Component.translatable(RESOURCE + "1").getString();
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public enum MelomancerState {
        MIXING(0),
        GOING_TO_MIX(1),
        GOING_TO_CHEST(2),
        NOTHING(3),
        LUNCH_BREAK(4);

        private static final MelomancerState[] BY_ID = Arrays.stream(values()).sorted(
                Comparator.comparingInt(MelomancerState::getId)).toArray(MelomancerState[]::new);
        private final int id;

        MelomancerState(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static MelomancerState byId(int id) {
            return BY_ID[id % BY_ID.length];
        }
    }
}
