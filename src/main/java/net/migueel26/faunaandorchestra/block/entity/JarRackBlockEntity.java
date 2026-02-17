package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.recipe.ModRecipes;
import net.migueel26.faunaandorchestra.recipe.NaturalRecipe;
import net.migueel26.faunaandorchestra.recipe.SizedIngredient;
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
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
            super.onContentsChanged(slot);
        }
    };
    public JarRackBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.JAR_RACK_BE.get(), pos, blockState);

        this.progress = 0;
    }

    public void cookTick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            BlockState fuel = level.getBlockState(pos.below());

            List<ItemStack> ingredients = new ArrayList<>();
            for (int i = 0; i < inventory.getSlots(); i++) {
                ingredients.add(inventory.getStackInSlot(i));
            }

            NaturalRecipe.RecipeInput recipeInput = new NaturalRecipe.RecipeInput(ingredients, fuel.getBlock());

            Optional<RecipeHolder<NaturalRecipe>> recipeOptional = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.NATURAL_TYPE.get(), recipeInput, level);

            if (recipeOptional.isPresent()) {
                NaturalRecipe recipe = recipeOptional.get().value();

                if (progress % 10 == 0) {
                    spawnCookingParticles(pos, serverLevel, fuel);
                }

                this.progress++;

                if (this.progress >= recipe.time()) {
                    craftItem(recipe);
                    this.progress = 0;
                }
            } else {
                if (this.progress > 0) {
                    this.progress = 0;
                    setChanged();
                }
            }
        }
    }

    private static void spawnCookingParticles(BlockPos pos, ServerLevel serverLevel, BlockState fuel) {
        SimpleParticleType particle = switch (fuel) {
            case BlockState block when block.is(Blocks.LAVA) -> ParticleTypes.LAVA;
            case BlockState block when block.is(Blocks.WATER) -> ParticleTypes.DRIPPING_WATER;
            case BlockState block when block.is(Blocks.ICE) -> ParticleTypes.DUST_PLUME;
            default -> ParticleTypes.CLOUD;
        };

        serverLevel.sendParticles(particle, pos.getCenter().x, pos.getY(), pos.getCenter().z,
                10, 0.25f, 0.5f, 0.25f, 0.05);
    }

    private void craftItem(NaturalRecipe recipe) {
        for (SizedIngredient required : recipe.ingredients()) {
            int amountNeeded = required.amount();

            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack slotStack = inventory.getStackInSlot(i);

                if (!slotStack.isEmpty() && required.ingredient().test(slotStack)) {
                    int toExtract = Math.min(amountNeeded, slotStack.getCount());

                    inventory.extractItem(i, toExtract, false);

                    amountNeeded -= toExtract;

                    if (amountNeeded <= 0) break;
                }
            }
        }

        ItemStack result = recipe.output().copy();

        boolean inserted = false;
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.insertItem(i, result, false).isEmpty()) {
                inserted = true;
                break;
            }
        }

        if (!inserted) {
            Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, result);
        }

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public void setInventory(ItemStackHandler inventory) {
        this.inventory = inventory;
    }
}
