package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DiscordedFlowerBlockEntity extends BlockEntity {
    private int hunger = 0;

    public DiscordedFlowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISCORDED_FLOWER_BE.get(), pos, state);
    }

    public int getHunger() {
        return this.hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Hunger", this.hunger);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.hunger = tag.getInt("Hunger");
    }
}