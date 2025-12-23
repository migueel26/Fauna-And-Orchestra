package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.MelomancyCauldronBlock;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.util.RecipesUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class MelomancyCauldronBlockEntity extends BlockEntity implements GeoBlockEntity, Clearable {
    public static final int NUM_SLOTS = 4;
    public static final int DEFAULT_COOK_TIME = 1800;
    /// COMPONENTS ---------------
    private final NonNullList<ItemStack> ingredients = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
    protected int cookTime = -1;
    // "null", "musical_ink", "discord"
    protected String mixResult = "null";
    /// ---------------------------
    private final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final static RawAnimation PREPARING = RawAnimation.begin().thenLoop("preparing");
    private final static RawAnimation MIX = RawAnimation.begin().thenPlay("mix");
    public final static RawAnimation EMPTY = RawAnimation.begin().thenPlay("empty");
    private final AnimationController<MelomancyCauldronBlockEntity> controller = new AnimationController<>(this, "melomancy_cauldron_controller", 5, this::animController)
            .triggerableAnim("mix", MIX)
            .triggerableAnim("empty", EMPTY);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public MelomancyCauldronBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MELOMANCY_CAULDRON_BE.get(), pos, blockState);
    }

    protected <E extends MelomancyCauldronBlockEntity> PlayState animController(final AnimationState<E> state) {
        if (isCooking()) {
            state.getController().setAnimation(PREPARING);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    public boolean addIngredient(Player player, ItemStack originalStack, InteractionHand usedHand) {
        int i = 0;
        boolean found = false;
        ItemStack newIngredient =  originalStack.copy();
        newIngredient.setCount(1);

        while (i < ingredients.size() && !found) {
            ItemStack itemStack = ingredients.get(i);
            if (itemStack.is(originalStack.getItem())) {
                // It's already placed, so we increment by 1
                itemStack.setCount(itemStack.getCount() + 1);
                ingredients.set(i, itemStack);

                // We return the item leftover
                handlePlayerCost(player, usedHand, originalStack);

                found = true;
                triggerAnim("melomancy_cauldron_controller", "mix");
                this.markUpdated();
            } else if (itemStack.isEmpty()) {
                this.ingredients.set(i, newIngredient);

                handlePlayerCost(player, usedHand, originalStack);

                found = true;
                triggerAnim("melomancy_cauldron_controller", "mix");
                this.markUpdated();
            }
            i++;
        }

        return found;
    }

    private void handlePlayerCost(Player player, InteractionHand hand, ItemStack originalStack) {
        if (originalStack.hasCraftingRemainingItem()) {
            player.setItemInHand(hand, originalStack.getCraftingRemainingItem());
        } else {
            if (!player.getAbilities().instabuild) {
                originalStack.shrink(1);
            }
        }
    }

    public static void cookTick(Level level, BlockPos pos, BlockState state, MelomancyCauldronBlockEntity blockEntity) {
        boolean flag = false;

        ItemStack itemstack = blockEntity.ingredients.get(0);
        if (!itemstack.isEmpty() && blockEntity.cookTime > 0) {
            flag = true;
            blockEntity.cookTime--;

            if (blockEntity.cookTime == 0) {
                level.setBlock(pos, state.setValue(MelomancyCauldronBlock.COOKING, false), 3);
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 0.5f);
                blockEntity.mixResult = RecipesUtil.isRecipe(blockEntity.ingredients);
            }
        }

        if (flag) {
            setChanged(level, pos, state);
        }

    }


    public static void particleTick(Level level, BlockPos pos, BlockState state, MelomancyCauldronBlockEntity blockEntity) {
        if (level.getRandom().nextFloat() < 0.11F && state.getValue(MelomancyCauldronBlock.COOKING)) {
            CampfireBlock.makeParticles((Level) level, pos, !state.getValue(MelomancyCauldronBlock.COOKING), true);
        }

        if (state.getValue(MelomancyCauldronBlock.LIQUID) >= 2 && level.getRandom().nextFloat() < 0.11F) {
            double randomX = level.random.nextDouble()/2 - 0.25f;
            double randomZ = level.random.nextDouble()/2 - 0.25f;
            double randomY = level.random.nextDouble() / 4;

            SimpleParticleType particle = blockEntity.mixResult.equalsIgnoreCase("discord") ?
                    ParticleTypes.SCULK_CHARGE_POP : ModParticleTypes.CAULDRON_POP.get();
            level.addParticle(particle,
                    pos.getCenter().x+randomX, pos.getY()+0.65f+randomY, pos.getCenter().z+randomZ, 0, 0.1, 0);
        }
    }

    public ItemStack getResult() {
        if (this.cookTime == 0) {
            return RecipesUtil.getMixResult(mixResult);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public boolean cook() {
        if (!ingredients.get(0).isEmpty() && !this.isCooking()) {
            this.cookTime = DEFAULT_COOK_TIME;
            return true;
        } else {
            return false;
        }
    }

    public int getCookTime() {
        return cookTime;
    }

    public boolean isCooking() {
        return getBlockState().getValue(MelomancyCauldronBlock.COOKING);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, this.ingredients);
        if (tag.contains("CookTime")) {
            this.cookTime = tag.getInt("CookTime");
        }

        if (tag.contains("MixResult")) {
            this.mixResult = tag.getString("MixResult");
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.ingredients, true);
        tag.putInt("CookTime", cookTime);
        tag.putString("MixResult", mixResult);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundTag, this.ingredients, true);
        compoundTag.putInt("CookTime", this.cookTime);
        compoundTag.putString("MixResult", this.mixResult);
        return compoundTag;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public String getMixResult() {
        return this.mixResult;
    }


    public NonNullList<ItemStack> getIngredients() {
        return this.ingredients;
    }

    public boolean hasFinishedCooking() {
        return this.cookTime == 0;
    }
    public void clearContent(boolean animate) {
        if (animate) triggerAnim("melomancy_cauldron_controller", "empty");
        this.ingredients.clear();
        this.cookTime = -1;
        this.mixResult = "null";
        this.setChanged();
    }

    @Override
    public void clearContent() {
        clearContent(true);
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
