package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.recipe.ModRecipes;
import net.migueel26.faunaandorchestra.recipe.NaturalRecipe;
import net.migueel26.faunaandorchestra.recipe.SizedIngredient;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JarRackBlockEntity extends BlockEntity {
    protected int progress;
    public ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return !stack.is(ModBlocks.HANGING_JAR.asItem());
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!level.isClientSide) {
                markUpdated();
            }
            super.onContentsChanged(slot);
        }
    };
    public JarRackBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.JAR_RACK_BE.get(), pos, blockState);

        this.progress = 0;
    }

    public static void cookTick(Level level, BlockPos pos, BlockState state, JarRackBlockEntity jarRack) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            BlockState fuel = level.getBlockState(pos.below());

            List<ItemStack> ingredients = new ArrayList<>();
            for (int i = 0; i < jarRack.inventory.getSlots(); i++) {
                ingredients.add(jarRack.inventory.getStackInSlot(i).copy());
            }

            NaturalRecipe.RecipeInput recipeInput = new NaturalRecipe.RecipeInput(ingredients, fuel);

            Optional<RecipeHolder<NaturalRecipe>> recipeOptional = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.NATURAL_TYPE.get(), recipeInput, level);

            if (recipeOptional.isPresent()) {
                NaturalRecipe recipe = recipeOptional.get().value();

                if (jarRack.progress % 10 == 0) {
                    spawnCookingParticles(pos, serverLevel, fuel);
                }

                jarRack.progress++;

                if (jarRack.progress >= recipe.time()) {
                    craftItem(recipe, jarRack, level, state);
                    jarRack.progress = 0;
                }

                setChanged(level, pos, state);

            } else {
                if (jarRack.progress > 0) {
                    jarRack.progress = 0;
                    jarRack.setChanged();
                }
            }
        }
    }

    private static void spawnCookingParticles(BlockPos pos, ServerLevel serverLevel, BlockState fuel) {
        SimpleParticleType particle = switch (fuel) {
            case BlockState block when block.is(ModTags.Blocks.JAR_FIRE_FUEL) -> ParticleTypes.CAMPFIRE_COSY_SMOKE;
            case BlockState block when block.is(ModTags.Blocks.JAR_WATER_FUEL) -> ParticleTypes.BUBBLE_POP;
            case BlockState block when block.is(BlockTags.ICE) -> ParticleTypes.DUST_PLUME;
            default -> ParticleTypes.CLOUD;
        };

        serverLevel.sendParticles(particle, pos.getCenter().x, pos.getY(), pos.getCenter().z,
                2, 0.25f, 0.15f, 0.25f, 0.01);
    }

    private static void craftItem(NaturalRecipe recipe, JarRackBlockEntity jarRack, Level level, BlockState state) {
        for (SizedIngredient required : recipe.ingredients()) {
            int amountNeeded = required.amount();

            for (int i = 0; i < jarRack.inventory.getSlots(); i++) {
                ItemStack slotStack = jarRack.inventory.getStackInSlot(i);

                if (!slotStack.isEmpty() && required.ingredient().test(slotStack)) {
                    int toExtract = Math.min(amountNeeded, slotStack.getCount());

                    jarRack.inventory.extractItem(i, toExtract, false);

                    amountNeeded -= toExtract;

                    if (amountNeeded <= 0) break;
                }
            }
        }

        ItemStack result = recipe.output().copy();

        boolean inserted = false;
        for (int i = 0; i < jarRack.inventory.getSlots(); i++) {
            if (jarRack.inventory.insertItem(i, result, false).isEmpty()) {
                inserted = true;
                break;
            }
        }

        if (!inserted) {
            Containers.dropItemStack(level, jarRack.worldPosition.getX() + 0.5, jarRack.worldPosition.getY() + 1.0, jarRack.worldPosition.getZ() + 0.5, result);
        }

        jarRack.setChanged();
        level.sendBlockUpdated(jarRack.worldPosition, state, state, 3);
    }

    public void clearContents() {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Progress")) {
            this.progress = tag.getInt("Progress");
        }
        super.loadAdditional(tag, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Inventory", inventory.serializeNBT(registries));
        compoundTag.putInt("Progress", this.progress);
        return compoundTag;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void setInventory(ItemStackHandler inventory) {
        this.inventory = inventory;
    }
}
