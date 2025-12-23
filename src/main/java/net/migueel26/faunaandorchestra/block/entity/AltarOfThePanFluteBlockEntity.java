package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AltarOfThePanFluteBlockEntity extends BlockEntity {
    private List<Integer> powers = List.of();
    public AltarOfThePanFluteBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ALTAR_OF_THE_PAN_FLUTE_BE.get(), pos, blockState);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Powers")) {
            this.powers = intArrayToList(tag.getIntArray("Powers"));
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putIntArray("Powers", this.powers);
        super.saveAdditional(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putIntArray("Powers", powers);
        return compoundTag;
    }

    public List<Integer> getPowers() {
        return this.powers;
    }

    public void setPowers(List<Integer> list) {
        this.powers = list;
    }

    private List<Integer> intArrayToList(int[] powers) {
        List<Integer> list = new ArrayList<>();
        for (int power : powers) {
            list.add(power);
        }
        return list;
    }
}
