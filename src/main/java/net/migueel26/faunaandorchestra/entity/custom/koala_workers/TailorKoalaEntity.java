package net.migueel26.faunaandorchestra.entity.custom.koala_workers;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.SewingMachineBlock;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.ListeningEntity;
import net.migueel26.faunaandorchestra.entity.custom.TalkableEntity;
import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.recipe.ModRecipes;
import net.migueel26.faunaandorchestra.recipe.SewingRecipe;
import net.migueel26.faunaandorchestra.recipe.SizedIngredient;
import net.migueel26.faunaandorchestra.screen.custom.TailorMenu;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.BlocksUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class TailorKoalaEntity extends AbstractKoalaWorker {
    private static final RawAnimation SLEEP = RawAnimation.begin().thenPlay("sleep");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation SEW = RawAnimation.begin().thenPlay("sew");
    private static final RawAnimation EAT = RawAnimation.begin().thenPlay("eat");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    AnimationController<TailorKoalaEntity> controller = new AnimationController<>(this, "tailor_koala_controller", 5, this::koalaState)
            .triggerableAnim("eat", EAT);
    // TALKABLE ENTITY
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/tailor_koala_icon.png");
    public static final String RESOURCE = "dialogue.faunaandorchestra.tailor_koala";
    // TAILORING
    public static final int MAX_WORK_TIME = 1200;
    public static final int START_PAUSE = 600;
    public static final int END_PAUSE = 900;
    public static final int EAT_TIME = (START_PAUSE + END_PAUSE) / 3;
    protected static final EntityDataAccessor<Boolean> SEWING = SynchedEntityData.defineId(TailorKoalaEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<ItemStack> CATALOG_CHOICE = SynchedEntityData.defineId(TailorKoalaEntity.class, EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<Integer> LEARNT_RECIPES = SynchedEntityData.defineId(TailorKoalaEntity.class, EntityDataSerializers.INT);
    public ItemStackHandler inventory = new ItemStackHandler(12);
    public TailorKoalaEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CATALOG_CHOICE, ItemStack.EMPTY);
        builder.define(SEWING, false);
        builder.define(LEARNT_RECIPES, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5f) {
            final TailorKoalaEntity koala = (TailorKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation());
            }
        });
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f) {
            final TailorKoalaEntity koala = (TailorKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation()) && (!koala.isSewing() || koala.isInLunchBreak());
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0f) {
            final TailorKoalaEntity koala = (TailorKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && (!koala.isKoalaSleeping() || !koala.hasWorkingStation()) && (!koala.hasWorkingStation() || koala.isInLunchBreak());
            }
        });
        this.goalSelector.addGoal(5, new FaunaRandomLookAroundGoal(this));
    }

    private <E extends GeoAnimatable> PlayState koalaState(AnimationState<E> state) {
        if (isKoalaSleeping() && hasWorkingStation()) {
            state.getController().setAnimation(SLEEP);
        } else if (isSewing() && !isInLunchBreak()) {
            state.getController().setAnimation(SEW);
        } else if (hasWorkingStation() && !isInLunchBreak()) {
            state.getController().setAnimation(SIT);
        } else if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.entityData.set(SEWING, compound.getBoolean("Sewing"));
        this.entityData.set(LEARNT_RECIPES, compound.getInt("LearntRecipes"));
        if (compound.contains("Inventory")) {
            this.inventory.deserializeNBT(this.registryAccess(), compound.getCompound("Inventory"));
        }
        if (compound.contains("CatalogChoice")) {
            this.entityData.set(CATALOG_CHOICE, ItemStack.parseOptional(this.registryAccess(), compound.getCompound("CatalogChoice")));
        }
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("LearntRecipes", this.entityData.get(LEARNT_RECIPES));
        compound.put("Inventory", this.inventory.serializeNBT(this.registryAccess()));
        compound.putBoolean("Sewing", isSewing());
        if (!getCatalogChoice().isEmpty()) {
            compound.put("CatalogChoice", this.getCatalogChoice().save(this.registryAccess()));
        }
        super.addAdditionalSaveData(compound);
    }

    public boolean tryToSew() {
        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ingredients.add(inventory.getStackInSlot(i).copy());
        }

        SewingRecipe.RecipeInput recipeInput = new SewingRecipe.RecipeInput(ingredients);

        List<RecipeHolder<SewingRecipe>> possibleRecipes = level().getRecipeManager()
                .getRecipesFor(ModRecipes.SEWING_TYPE.get(), recipeInput, level());

        for (RecipeHolder<SewingRecipe> recipeHolder : possibleRecipes) {
            if (ItemStack.isSameItem(recipeHolder.value().output(), this.getCatalogChoice())) {
                // If the output matches the catalog choice the koala starts sewing
                this.setSewing(true);
                this.lookAt(EntityAnchorArgument.Anchor.FEET, workingStation.getCenter());
                this.level().setBlock(workingStation, level().getBlockState(workingStation).setValue(SewingMachineBlock.SEWING, true), 3);

                consumeIngredients(recipeHolder.value(), this);
                return true;
            }
        }

        return false;
    }

    private static void consumeIngredients(SewingRecipe recipe, TailorKoalaEntity koala) {
        for (SizedIngredient required : recipe.ingredients()) {
            int amountNeeded = required.amount();

            for (int i = 0; i < koala.inventory.getSlots(); i++) {
                ItemStack slotStack = koala.inventory.getStackInSlot(i);

                if (!slotStack.isEmpty() && required.ingredient().test(slotStack)) {
                    int toExtract = Math.min(amountNeeded, slotStack.getCount());

                    koala.inventory.extractItem(i, toExtract, false);

                    amountNeeded -= toExtract;

                    if (amountNeeded <= 0) break;
                }
            }
        }
    }

    private static void finishSewing(TailorKoalaEntity koala) {
        ItemStack result = koala.getCatalogChoice().copy();

        // Insert the piece of clothing if the table has room
        boolean inserted = false;
        for (int i = 0; i < koala.inventory.getSlots(); i++) {
            if (koala.inventory.insertItem(i, result, false).isEmpty()) {
                inserted = true;
                break;
            }
        }

        // Drop the piece of clothing if the table is full
        if (!inserted) {
            Containers.dropItemStack(koala.level(), koala.getX() + 0.5, koala.getY() + 1.0, koala.getZ() + 0.5, result);
        }

        // We reset the koala
        koala.setSewing(false);
        koala.setCatalogChoice(ItemStack.EMPTY);
        koala.resetWorkTime();
        koala.level().setBlock(koala.workingStation, koala.level().getBlockState(koala.workingStation).setValue(SewingMachineBlock.SEWING, false), 3);
    }

    @Override
    public void tick() {
        if (isSewing() && !level().isClientSide() && !isKoalaSleeping()) {
            if (tickCount <= 20 || workTime <= 1 || (workTime >= END_PAUSE && workTime <= END_PAUSE + 1)) {
                this.lookAt(EntityAnchorArgument.Anchor.FEET, workingStation.getCenter());
            }

            if (tickCount % 20 == 0) {
                increaseWorkTime();
            }

            if (workTime == START_PAUSE) {
                this.level().setBlock(workingStation, level().getBlockState(workingStation).setValue(SewingMachineBlock.SEWING, false), 3);
                this.setGoodMorning(true);
            }

            if (workTime == END_PAUSE - 4) {
                BlockPos stoolPos = workingStation.relative(level().getBlockState(workingStation).getValue(SewingMachineBlock.FACING));
                this.getNavigation().moveTo(stoolPos.getX(), stoolPos.getY(), stoolPos.getZ(), 0, 1.0f);
            }

            if (workTime == EAT_TIME || workTime == EAT_TIME*2) {
                triggerAnim("tailor_koala_controller", "eat");
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

            if (workTime >= END_PAUSE && workTime <= END_PAUSE + 2) {
                this.level().setBlock(workingStation, level().getBlockState(workingStation).setValue(SewingMachineBlock.SEWING, true), 3);
                this.stopInPlace();
                Direction facing = level().getBlockState(workingStation).getValue(SewingMachineBlock.FACING);
                BlockPos stoolPos = workingStation.relative(facing);
                SewingMachineBlock.moveKoalaToStool(stoolPos, this, facing);
            }

            if (workTime == MAX_WORK_TIME) {
                finishSewing(this);
            }
        }

        super.tick();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide() && this.hasWorkingStation()) {
            BlockState state = level().getBlockState(workingStation);

            if (state.hasProperty(SewingMachineBlock.SEWING)) {
                level().scheduleTick(workingStation, ModBlocks.SEWING_MACHINE.get(), 30);
            }

            onLeaveWorkingStation(this);
        }
        return super.hurt(source, amount);
    }

    public static void onLeaveWorkingStation(TailorKoalaEntity koala) {
        koala.setWorkingStation(BlockPos.ZERO);
        koala.setSewing(false);
        koala.setCatalogChoice(ItemStack.EMPTY);
        koala.resetWorkTime();

        if (!koala.level().isClientSide()) {
            BlocksUtil.dropContents(koala.level(), koala.blockPosition(), koala.inventory);
        }
    }

    private void openCustomMenu(Player player) {
        if (!this.level().isClientSide()) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) ->
                    new TailorMenu(id, playerInventory, this), this.getDisplayName()), buf -> {
                buf.writeUUID(getUUID());
            });
        }
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

        } else if (stack.is(ModItems.SEWING_RECIPE)) {
            String itemString = stack.get(ModDataComponents.FAUNA_NAME);
            ResourceLocation location = ResourceLocation.parse(itemString);
            Item item = BuiltInRegistries.ITEM.get(location);

            if (item.equals(ModItems.FLORAL_BOOTS.get()) && (getLearntRecipes() & 1) == 0) {
                if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, blockPosition(), ModSounds.TWINKLE.get(), SoundSource.NEUTRAL);
                    serverLevel.sendParticles(ModParticleTypes.STAR.get(),
                            position().x, blockPosition().above().getY()+0.25f, position().z,
                            10, 0.1f, 0.1f, 0.1f, 0.025f);

                    setLearntRecipes(getLearntRecipes() | 1);
                    player.displayClientMessage(Component.translatable("text.faunaandorchestra.new_recipe"), true);
                }
                return InteractionResult.SUCCESS;
            }

        } else if (hasWorkingStation() && !isSewing()) {
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
            if (isSewing()) {
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

    public ItemStack getCatalogChoice() {
        return entityData.get(CATALOG_CHOICE);
    }

    public void setCatalogChoice(ItemStack itemStack) {
        this.entityData.set(CATALOG_CHOICE, itemStack);
    }

    public boolean isSewing() {
        return entityData.get(SEWING);
    }

    public void setSewing(boolean sewing) {
        this.entityData.set(SEWING, sewing);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void onStartListening(ConductorEntity conductor) {
        setKoalaSleeping(false);

        if (isSewing() && !isInLunchBreak()) {
            this.level().setBlock(workingStation, level().getBlockState(workingStation).setValue(SewingMachineBlock.SEWING, true), 3);
        }
    }

    @Override
    public void onStopListening() {
        setKoalaSleeping(true);

        if (isSewing()) {
            this.level().setBlock(workingStation, level().getBlockState(workingStation).setValue(SewingMachineBlock.SEWING, false), 3);
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.SEWING.get();
    }

    @Override
    public void playAmbientSound() {
        if (isSewing() && !isInLunchBreak()) {
            super.playAmbientSound();
        }
    }

    public boolean isInLunchBreak() {
        return workTime >= START_PAUSE && workTime <= END_PAUSE;
    }

    @Override
    public boolean isWorking() {
        return isSewing();
    }

    @Override
    public boolean isWorkingStation(BlockState state) {
        return state.is(ModBlocks.SEWING_MACHINE);
    }

    @Override
    public boolean isListening() {
        return !isKoalaSleeping();
    }

    public void setLearntRecipes(int recipes) {
        this.entityData.set(LEARNT_RECIPES, recipes);
    }

    public int getLearntRecipes() {
        return entityData.get(LEARNT_RECIPES);
    }
}
