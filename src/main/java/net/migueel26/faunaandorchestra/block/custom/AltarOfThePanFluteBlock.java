package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.AltarOfThePanFluteBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.VoiceChamberBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.PanFluteItem;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.PlayerUtil;
import net.migueel26.faunaandorchestra.util.VesselUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class AltarOfThePanFluteBlock extends AltarBlock implements EntityBlock {
    public static final int FIRST_THUNDER_TICKS = 10;
    public static final int CONSEQUENT_THUNDERS_TICKS = 80;
    private static final int FINAL_THUNDERS_TICKS = 10;
    protected int song = -1;
    protected int times = 0;
    public static final BooleanProperty PAN_FLUTE = BooleanProperty.create("pan_flute");

    public AltarOfThePanFluteBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(PAN_FLUTE, false));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getValue(PAN_FLUTE) && !newState.is(ModBlocks.ALTAR_OF_THE_PAN_FLUTE)) {
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.PAN_FLUTE.get()));
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AltarOfThePanFluteBlockEntity altar) {
            if (stack.is(ModItems.PAN_FLUTE) && !state.getValue(PAN_FLUTE)) {
                // Place Pan Flute
                altar.setPowers(stack.get(ModDataComponents.PAN_FLUTE_LIST));
                player.setItemSlot(hand.equals(InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                level.setBlock(pos, state.setValue(PAN_FLUTE, true), 3);
                level.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 1.0f, 1.5f);
                return ItemInteractionResult.SUCCESS;

            } else if (stack.isEmpty() && state.getValue(PAN_FLUTE) && this.song == -1 && !level.isClientSide()) {
                List<BlockPos> chambers = getChambers(pos);
                this.song = getNewSong(level, chambers);

                if (this.song != -1) {
                    // We start adding the song
                    ((ServerLevel) level).sendParticles(ModParticleTypes.MAGICAL_NOTE.get(), pos.getCenter().x, pos.getY() + 1f, pos.getCenter().z, 20, 0.1, 0.1, 0.1, 0.05);
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.VESSEL_COLLECT.get(), SoundSource.BLOCKS, 1.0f, 0.8f);
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.PAN_FLUTE_ALTAR_QUAKE.get(), SoundSource.NEUTRAL, 1.25f, 1.0f);

                    // We lock the chambers
                    for (BlockPos chamberPos : chambers) {
                        if (level.getBlockEntity(chamberPos) instanceof VoiceChamberBlockEntity chamberBE) {
                            chamberBE.setLocked(true);
                        }
                    }

                    this.times = 0;
                    level.scheduleTick(pos, this, FIRST_THUNDER_TICKS);
                    return ItemInteractionResult.SUCCESS;
                } else {
                    // Get Pan Flute
                    player.setItemInHand(hand, new ItemStack(ModItems.PAN_FLUTE, 1,
                            DataComponentPatch.builder().set(ModDataComponents.PAN_FLUTE_LIST.get(), altar.getPowers()).build()));
                    altar.setPowers(List.of());
                    level.setBlock(pos, state.setValue(PAN_FLUTE, false), 3);
                    level.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.BLOCKS, 1.0f, 1.5f);
                    return ItemInteractionResult.SUCCESS;
                }

            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.song == getNewSong(level, getChambers(pos))) {
            if (times < 6) {
                List<BlockPos> actualPosChambers = getChambers(pos).stream()
                        .map(level::getBlockEntity)
                        .filter(be -> be instanceof VoiceChamberBlockEntity)
                        .filter(be -> !((VoiceChamberBlockEntity) be).getVoice().isEmpty())
                        .map(BlockEntity::getBlockPos)
                        .toList();

                for (BlockPos chamberPos : actualPosChambers) {
                    PlayerUtil.spawnParticlesFromTo(ParticleTypes.CLOUD, 1, level, chamberPos.getCenter().add(0, 0.25, 0), pos.getCenter().add(0, 0.85, 0));
                }

                if (!level.isClientSide()) {
                    ((ServerLevel) level).sendParticles(ModParticleTypes.MAGICAL_NOTE.get(), pos.getCenter().x, pos.getY() + 1f, pos.getCenter().z, 20, 0.1, 0.1, 0.1, 0.05);
                }

                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.PAN_FLUTE_ALTAR_THUNDER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

                if (times >= 3) {
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.VESSEL_COLLECT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                }

                if (times >= 3) {
                    level.scheduleTick(pos, ModBlocks.ALTAR_OF_THE_PAN_FLUTE.get(), FINAL_THUNDERS_TICKS);
                } else {
                    level.scheduleTick(pos, ModBlocks.ALTAR_OF_THE_PAN_FLUTE.get(), CONSEQUENT_THUNDERS_TICKS);
                }

            } else if (level.getBlockEntity(pos) instanceof AltarOfThePanFluteBlockEntity altarBE) {
                // Finale
                List<BlockPos> actualPosChambers = getChambers(pos).stream()
                        .map(level::getBlockEntity)
                        .filter(be -> be instanceof VoiceChamberBlockEntity)
                        .filter(be -> !((VoiceChamberBlockEntity) be).getVoice().isEmpty())
                        .map(BlockEntity::getBlockPos)
                        .toList();

                for (BlockPos chamberPos : actualPosChambers) {
                    PlayerUtil.spawnParticlesFromTo(ParticleTypes.CLOUD, 1, level, chamberPos.getCenter().add(0, 0.25, 0), pos.getCenter().add(0, 0.85, 0));
                }

                for (BlockPos chamberPos : getChambers(pos)) {
                    if (level.getBlockEntity(chamberPos) instanceof VoiceChamberBlockEntity be) {
                        be.setLocked(false);
                        be.setVoice("");
                        level.setBlock(chamberPos, level.getBlockState(chamberPos).setValue(VoiceChamberBlock.VOICE, false), 3);
                    }
                }

                EntityType.LIGHTNING_BOLT.spawn(level, pos, MobSpawnType.MOB_SUMMONED);

                SoundEvent soundEvent = switch (this.song) {
                    case 1 -> ModSounds.PAN_FLUTE_NOTES.get();
                    case 2 -> ModSounds.PAN_FLUTE_PUSH.get();
                    case 3 -> ModSounds.PAN_FLUTE_HEALTH.get();
                    case 4 -> ModSounds.PAN_FLUTE_WIND.get();
                    case 5 -> ModSounds.PAN_FLUTE_NATURE.get();
                    default -> ModSounds.PAN_FLUTE_USE.get();
                };

                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), soundEvent, SoundSource.NEUTRAL, 1.0f, 1.0f);

                if (!level.isClientSide()) {
                    level.sendParticles(ParticleTypes.FLAME, pos.getCenter().x, pos.getY()+1.0, pos.getCenter().z, 180, 0.1f, 0.1f, 0.1f, 0.25);
                }

                List<Integer> powers = new ArrayList<>(altarBE.getPowers());
                if (!powers.contains(this.song)) powers.add(this.song);

                altarBE.setPowers(powers);

                this.song = -1;
                this.times = 0;
            }

            times++;
        } else {
            // We reset
            List<BlockPos> chambers = getChambers(pos);
            this.song = getNewSong(level, chambers);
            for (BlockPos chamber : chambers) {
                if (level.getBlockEntity(chamber) instanceof VoiceChamberBlockEntity be) {
                    be.setLocked(false);
                }
            }
        }
        super.tick(state, level, pos, random);
    }

    private int getNewSong(Level level, List<BlockPos> chambersPos) {
        int sound = -1;
        List<? extends EntityType<?>> voicesList = chambersPos.stream()
                .map(level::getBlockEntity)
                .filter(be -> be instanceof VoiceChamberBlockEntity)
                .filter(be -> !((VoiceChamberBlockEntity) be).getVoice().isEmpty())
                .map(be -> EntityType.byString(((VoiceChamberBlockEntity) be).getVoice()).get())
                .toList();

        if (!voicesList.isEmpty()) {
            Map<? extends EntityType<?>, Integer> voicesMap = voicesAsMap(voicesList);

            for (Map.Entry<Map<? extends EntityType<?>, Integer>, Integer> entry : VesselUtil.SOUNDS.entrySet()) {
                if (entry.getKey().equals(voicesMap)) {
                    sound = entry.getValue();
                    break;
                }
            }
        }

        return sound;
    }

    private Map<? extends EntityType<?>, Integer> voicesAsMap(List<? extends EntityType<?>> list) {
        Map<EntityType<?>, Integer> map = new HashMap<>();
        for (EntityType<?> entityType : list) {
            if (!map.containsKey(entityType)) {
                map.put(entityType, 1);
            } else {
                map.put(entityType, map.get(entityType) + 1);
            }
        }
        return map;
    }

    private List<BlockPos> getChambers(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        return List.of(
                new BlockPos(x + 1, y + 1, z + 3),
                new BlockPos(x + 3, y + 1, z + 1),
                new BlockPos(x - 1, y + 1, z + 3),
                new BlockPos(x - 3, y + 1, z + 1),
                new BlockPos(x + 1, y + 1, z - 3),
                new BlockPos(x + 3, y + 1, z - 1),
                new BlockPos(x - 1, y + 1, z - 3),
                new BlockPos(x - 3, y + 1, z - 1)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PAN_FLUTE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarOfThePanFluteBlockEntity(pos, state);
    }
}
