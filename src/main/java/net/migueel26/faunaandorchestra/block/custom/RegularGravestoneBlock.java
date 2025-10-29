package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegularGravestoneBlock extends ComposerGravestoneBlock {
    // Server-only
    public static BooleanProperty CAN_DROP = BooleanProperty.create("can_drop");

    public RegularGravestoneBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(CAN_DROP, true).setValue(PART, BedPart.FOOT).setValue(OPENED, false));
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            if (neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART)) {
                return state.setValue(OPENED, neighborState.getValue(OPENED)).setValue(CAN_DROP, neighborState.getValue(CAN_DROP));
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockPos blockPos = pos.relative(state.getValue(FACING).getOpposite());
            boolean canDrop = !(placer instanceof Player);
            level.setBlock(blockPos, state.setValue(PART, BedPart.HEAD).setValue(CAN_DROP, canDrop), 3);
            level.setBlock(pos, state.setValue(CAN_DROP, canDrop), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ComposerGravestoneBlockEntity composerGravestoneBlockEntity) {
            Vec3 vecPos;
            BlockPos neighbourPos = pos.relative(state.getValue(FACING));

            if (state.getValue(PART) == BedPart.HEAD) {
                // We get the foot, which renders and plays the animation
                vecPos = neighbourPos.getBottomCenter();
                composerGravestoneBlockEntity = (ComposerGravestoneBlockEntity) level.getBlockEntity(neighbourPos);
            } else {
                vecPos = pos.getBottomCenter();
            }

            Vec3 center;
            int diffX = neighbourPos.getX() - pos.getX();
            int diffZ = neighbourPos.getZ() - pos.getZ();
            double xOffset = 0.25;
            double zOffset = 0.25;

            if (diffX > 0) {
                center = vecPos.subtract(0.5, 0, 0);
                xOffset = 0.4;
            } else if (diffX < 0) {
                center = vecPos.add(0.5, 0, 0);
                xOffset = 0.4;
            } else if (diffZ > 0) {
                center = vecPos.subtract(0, 0, 0.5);
                zOffset = 0.4;
            } else {
                center = vecPos.add(0, 0, 0.5);
                zOffset = 0.4;
            }

            boolean isOpened = state.getValue(OPENED);
            boolean canDrop = state.getValue(CAN_DROP);

            // Transition animation
            if (isOpened) composerGravestoneBlockEntity.close();
            else composerGravestoneBlockEntity.open();

            // Sound and particles
            level.playLocalSound(pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 0.5F, false);
            if (!level.isClientSide() && !isOpened) {

                ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.STONE.defaultBlockState()),
                        center.x, center.y + 0.5F, center.z, 20,
                        xOffset, 0, zOffset, 1.0F);

                if (canDrop) {
                    spawnRandomLoot(level, pos);
                }
            }

            // Update block OPENED value
            if (!isOpened && canDrop) level.setBlock(pos, state.setValue(OPENED, true).setValue(CAN_DROP, false), 3);
            else level.setBlock(pos, state.setValue(OPENED, !isOpened), 3);
        }
        return ItemInteractionResult.SUCCESS;
    }

    /***
     *
     * 20% NOTHING
     * 20% SKELETON
     * 60% ITEM
     */

    private void spawnRandomLoot(Level level, BlockPos pos) {
        float probability = level.getRandom().nextFloat();
        if (probability >= 0 && probability <= 0.2) {
            // Skeleton
            List<Item> list = new ArrayList<>(List.of(
                    ModItems.FLUTE.get(),
                    ModItems.SAXOPHONE.get(),
                    ModItems.KEYTAR.get(),
                    ModItems.OBOE.get(),
                    ModItems.BATON.get()));

            Item instrument = list.get(new Random().nextInt(list.size()));

            Skeleton skeleton = EntityType.SKELETON.spawn((ServerLevel) level, pos, MobSpawnType.MOB_SUMMONED);

            ItemStack item = new ItemStack(instrument);
            item.setDamageValue(10);
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, item);
        } else if (probability > 0.2 && probability <= 0.8) {
            // Item
            float itemProb = level.getRandom().nextFloat();
            ItemStack reward;
            if (itemProb >= 0 && itemProb <= 0.05) {
                reward = new ItemStack(ModBlocks.GINGKO_BILOBA_SAPLING);
            } else if (itemProb > 0.05 && itemProb <= 0.1) {
                reward = new ItemStack(Items.DIAMOND, 2);
            } else if (itemProb > 0.1 && itemProb <= 0.15) {
                reward = new ItemStack(Items.EMERALD, 3);
            } else if (itemProb > 0.15 && itemProb <= 0.20) {
                reward = new ItemStack(Items.IRON_HORSE_ARMOR);
            } else if (itemProb > 0.20 && itemProb <= 0.25) {
                reward = new ItemStack(ModItems.BATON.get());
            } else if (itemProb > 0.25 && itemProb < 0.30) {
                reward = new ItemStack(Items.IRON_HELMET);
            } else if (itemProb >= 0.30 && itemProb < 0.90){
                reward = new ItemStack(Items.BONE, level.getRandom().nextInt(1, 5));
            } else {
                reward = ItemStack.EMPTY;
            }

            popResourceFromFace(level, pos, Direction.UP, reward);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OPENED, CAN_DROP);
    }
}
