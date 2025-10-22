package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.entity.VoiceChamberBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.util.VesselUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VoiceChamberBlock extends Block implements EntityBlock {
    public static BooleanProperty VOICE = BooleanProperty.create("voice");
    public static final VoxelShape SHAPE = Block.box(6,0,6,10,0.75,10);
    public VoiceChamberBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(getStateDefinition().any().setValue(VOICE, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof VoiceChamberBlockEntity blockEntity) {
            if (stack.is(ModItems.VOICE) && stack.has(ModDataComponents.FAUNA_NAME) && !state.getValue(VOICE)) {
                String voice = stack.get(ModDataComponents.FAUNA_NAME);
                EntityType<? extends Entity> entityType = EntityType.byString(voice).orElseThrow();

                // We play entity sound
                if (entityType.create(level) instanceof Mob mob) {
                    level.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 1.0f, 1.5f);
                    mob.playAmbientSound();
                    mob.discard();
                }

                if (!level.isClientSide()) {
                    ((ServerLevel) level).sendParticles(ModParticleTypes.MAGICAL_NOTE.get(), pos.getCenter().x, pos.getY()+0.5f , pos.getCenter().z, 20, 0.1, 0.1, 0.1, 0.05);
                }

                blockEntity.setVoice(voice);
                stack.consume(1, player);
                level.setBlock(pos, state.setValue(VOICE, true), 3);
                return ItemInteractionResult.SUCCESS;

            } else if (stack.isEmpty() && state.getValue(VOICE)) {
                EntityType<? extends Entity> entityType = EntityType.byString(blockEntity.getVoice()).orElseThrow();
                if (entityType.create(level) instanceof Mob mob) {
                    level.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 1.0f, 1.5f);
                    mob.playAmbientSound();
                    mob.discard();
                }

                if (!level.isClientSide()) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.POOF, pos.getCenter().x, pos.getY()+0.5f , pos.getCenter().z, 10, 0.1, 0.1, 0.1, 0.03);
                }

                player.setItemInHand(hand, VesselUtil.voiceOfEntity(entityType));
                blockEntity.setVoice("");
                level.setBlock(pos, state.setValue(VOICE, false), 3);
                return ItemInteractionResult.SUCCESS;

            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoiceChamberBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VOICE);
    }
}
