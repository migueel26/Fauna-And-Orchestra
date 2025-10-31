package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.DiscordNucleiBlockEntity;
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
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

public class DiscordNucleiBlock extends Block implements EntityBlock {
    public static final IntegerProperty INSTABILITY = IntegerProperty.create("instability", 0, 100);
    public static final IntegerProperty ESSENCE = IntegerProperty.create("essence", 0, 100);
    protected static final VoxelShape SHAPE = Block.box(1.0, 0, 1.0, 15.0, 2.0, 15.0);
    public DiscordNucleiBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(getStateDefinition().any().setValue(INSTABILITY, 0).setValue(ESSENCE, 0));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof DiscordNucleiBlockEntity discordNucleiBE) {
            int essence = state.getValue(ESSENCE);
            int instability = state.getValue(INSTABILITY);
            ItemStack stackInSlot = discordNucleiBE.inventory.getStackInSlot(0);
            if (!stackInSlot.isEmpty()) {
                if (stack.is(ModItems.DISCORD_ESSENCE)) {
                    level.playSound(player, pos, SoundEvents.WARDEN_LISTENING, SoundSource.BLOCKS);
                    if (!level.isClientSide()) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z, 20, 0.2, 0.2, 0.2, 0.01);
                    }
                    stack.consume(1, player);

                    applyEffect(stackInSlot, level, state, pos, discordNucleiBE, essence ,instability);

                    return ItemInteractionResult.SUCCESS;

                } else if (stack.is(ModItems.WANDERING_NOTE)) {
                    stack.consume(1, player);

                    int reduction = getInstabilityReduction(instability);
                    level.setBlock(pos, state.setValue(INSTABILITY, instability - reduction), 3);

                    if (!level.isClientSide()) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.WAX_OFF, pos.getCenter().x, pos.getY()+0.75f, pos.getZ(), 10, 0.2, 0.2, 0.2, 0);
                    }
                    level.playSound(player, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);

                    return ItemInteractionResult.SUCCESS;
                }
            } else if (RecipesUtil.isDiscordNucleiIngredient(stack)) {
                stack.consume(1, player);
                discordNucleiBE.inventory.setStackInSlot(0, new ItemStack(stack.getItem(), 1));
                level.playSound(player, pos, SoundEvents.WARDEN_TENDRIL_CLICKS, SoundSource.BLOCKS, 1.5f, 0.5f);
                if (!level.isClientSide()) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z, 15, 0.1, 0.1, 0.1, 0.15);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private int getInstabilityReduction(int instability) {
        return (instability*3)/4;
    }

    private void applyEffect(ItemStack stack, Level level, BlockState state, BlockPos pos, DiscordNucleiBlockEntity blockEntity, int essence, int instability) {
        Pair<Integer, Float> indexes = RecipesUtil.getDiscordNucleiIndexes(stack);
        int nextInstability = (int) (instability + indexes.getA() + indexes.getB()*level.random.nextFloat()* indexes.getB());

        if (nextInstability >= 100) {
            instabilityExplosion(level, pos);
        } else if (essence + 1 == RecipesUtil.getDiscordNucleiResult(stack).getA()) {
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z, 100, 0.5, 0.5, 0.5, 0.3);
            }
            level.playSound(null, pos, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 1.0f, 1.0f);
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(RecipesUtil.getDiscordNucleiResult(stack).getB()));
            blockEntity.clearContents();
            level.setBlock(pos, state.setValue(ESSENCE, 0).setValue(INSTABILITY, 0), 3);
        } else {
            level.setBlock(pos, state.setValue(ESSENCE, essence+1).setValue(INSTABILITY, nextInstability),3);
            level.scheduleTick(pos, this, getNextUnstableTick(instability, nextInstability));
        }
    }

    private static void instabilityExplosion(Level level, BlockPos pos) {
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 4, Level.ExplosionInteraction.BLOCK);
        level.playSound(null, pos, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (!level.isClientSide()) {
            ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z, 100, 0.5, 0.5, 0.5, 0.3);
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(pos.getX(), pos.below().getY(), pos.getZ());
        while (level.getBlockState(mutableBlockPos).isAir()) mutableBlockPos.move(Direction.DOWN);

        level.setBlock(mutableBlockPos, ModBlocks.FLOWER_DISCORD_BLOCK.get().defaultBlockState().setValue(FlowerGrowerDiscordBlock.MAX_GENERATION, 13), 3);
    }

    private int getNextUnstableTick(int instability, int nextInstability) {
        if (nextInstability < 20) {
            return 50;
        } else {
            return (int) ((1.0f / (float) nextInstability) * 1000);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int instability = state.getValue(INSTABILITY);
        if (instability > 0 && state.getValue(ESSENCE) > 0) {
            if (instability+1 == 100) {
                instabilityExplosion(level, pos);
            } else {
                level.setBlock(pos, state.setValue(INSTABILITY, instability+1), 3);
                level.scheduleTick(pos, this, getNextUnstableTick(instability, instability+1));
            }
        }
        super.tick(state, level, pos, random);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState belowBlockState = level.getBlockState(blockpos);
        return belowBlockState.is(ModBlocks.DISCORD_BLOCK) || belowBlockState.is(ModBlocks.FLOWER_DISCORD_BLOCK);
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DiscordNucleiBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INSTABILITY, ESSENCE);
    }
}
