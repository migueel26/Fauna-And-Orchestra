package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.DiscordNucleiBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.FlowerGrowerDiscordBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.util.RecipesUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

public class DiscordNucleiBlock extends Block implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(1.0, 0, 1.0, 15.0, 2.0, 15.0);
    public DiscordNucleiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.getBlockEntity(pos) instanceof DiscordNucleiBlockEntity discordNucleiBE) {
            int essence = discordNucleiBE.getEssence();
            int instability = discordNucleiBE.getInstability();
            ItemStack stackInSlot = discordNucleiBE.inventory.getStackInSlot(0);

            if (!stackInSlot.isEmpty()) {
                if (stack.is(ModItems.DISCORD_ESSENCE.get())) {
                    level.playSound(player, pos, SoundEvents.WARDEN_LISTENING, SoundSource.BLOCKS);
                    if (!level.isClientSide()) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z, 20, 0.2, 0.2, 0.2, 0.01);
                    }
                    stack.shrink(1);

                    applyEffect(stackInSlot, level, pos, discordNucleiBE, essence ,instability);

                    return InteractionResult.SUCCESS;

                } else if (stack.is(ModItems.WANDERING_NOTE.get())) {
                    int reduction = getInstabilityReduction(instability);
                    discordNucleiBE.setInstability(instability - reduction);

                    if (!level.isClientSide()) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.WAX_OFF, pos.getCenter().x, pos.getY()+0.75f, pos.getZ(), 10, 0.2, 0.2, 0.2, 0);
                    }
                    level.playSound(player, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            } else if (RecipesUtil.isDiscordNucleiIngredient(stack)) {
                discordNucleiBE.inventory.setStackInSlot(0, new ItemStack(stack.getItem(), 1));
                level.playSound(player, pos, SoundEvents.WARDEN_TENDRIL_CLICKS, SoundSource.BLOCKS, 1.5f, 0.5f);
                if (!level.isClientSide()) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z, 15, 0.1, 0.1, 0.1, 0.15);
                }
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private int getInstabilityReduction(int instability) {
        return (instability*3)/4;
    }

    private void applyEffect(ItemStack stack, Level level, BlockPos pos, DiscordNucleiBlockEntity blockEntity, int essence, int instability) {
        Pair<Integer, Float> indexes = RecipesUtil.getDiscordNucleiIndexes(stack);
        int nextInstability = (int) (instability + indexes.getA() + indexes.getB()*level.random.nextFloat()* indexes.getB());

        if (nextInstability >= 100) {
            instabilityExplosion(level, pos);
        } else if (essence + 1 >= RecipesUtil.getDiscordNucleiResult(stack).getA()) {
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z, 100, 0.5, 0.5, 0.5, 0.3);
            }
            level.playSound(null, pos, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 1.0f, 1.0f);
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(RecipesUtil.getDiscordNucleiResult(stack).getB()));

            blockEntity.clearContents();
            blockEntity.setEssence(0);
            blockEntity.setInstability(0);
            blockEntity.setActionTimer(-1);
        } else {
            blockEntity.setEssence(essence+1);
            blockEntity.setInstability(nextInstability);
            blockEntity.setActionTimer(getNextUnstableTick(instability, nextInstability));
        }
    }

    public static void instabilityExplosion(Level level, BlockPos pos) {
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 4, Level.ExplosionInteraction.BLOCK);
        level.playSound(null, pos, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (!level.isClientSide()) {
            ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z, 100, 0.5, 0.5, 0.5, 0.3);
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(pos.getX(), pos.below().getY(), pos.getZ());
        while (level.getBlockState(mutableBlockPos).isAir()) mutableBlockPos.move(Direction.DOWN);

        level.setBlock(pos, ModBlocks.FLOWER_DISCORD_BLOCK.get().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof FlowerGrowerDiscordBlockEntity be) {
            be.setMaxGeneration(13);
        }
    }

    public static int getNextUnstableTick(int instability, int nextInstability) {
        if (nextInstability < 20) {
            return 50;
        } else {
            return (int) ((1.0f / (float) nextInstability) * 1000);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState belowBlockState = level.getBlockState(blockpos);
        return belowBlockState.is(ModBlocks.DISCORD_BLOCK.get()) || belowBlockState.is(ModBlocks.FLOWER_DISCORD_BLOCK.get());
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DiscordNucleiBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.DISCORD_NUCLEI_BE.get() ?
                (BlockEntityTicker<T>) (lvl, pos, st, be) -> DiscordNucleiBlockEntity.tick(lvl, pos, st, (DiscordNucleiBlockEntity) be) : null;
    }
}
