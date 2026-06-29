package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.MotherStatueBlock;
import net.migueel26.faunaandorchestra.entity.custom.RedPandaEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.PlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class MotherStatueBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final static RawAnimation SING = RawAnimation.begin().thenPlay("sing");
    private final AnimationController<MotherStatueBlockEntity> controller = new AnimationController<>(this, "mother_statue_controller", 0, this::animController);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // IMAGINAL DISK ANIMATION
    private UUID redPandaUUID;
    private RedPandaEntity redPanda = null;
    private boolean isPlayingDiskAnimation = false;
    private int animationTick = 0;

    public MotherStatueBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MOTHER_STATUE_BE.get(), pos, blockState);
    }

    protected <E extends MotherStatueBlockEntity> PlayState animController(final AnimationState<E> state) {
        state.setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MotherStatueBlockEntity motherStatue) {
        if (motherStatue.isPlayingDiskAnimation() && !level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            RedPandaEntity redPanda = motherStatue.getRedPanda();
            if (redPanda == null) {
                motherStatue.setRedPanda((RedPandaEntity) serverLevel.getEntity(motherStatue.getRedPandaUUID()));
            } else {
                int animationTick = motherStatue.getAnimationTick();

                if (!redPanda.isNoGravity()) {
                    redPanda.setNoGravity(true);
                }

                if (animationTick % 20 == 0) {
                    level.playSound(null, pos, ModSounds.VESSEL_COLLECT.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
                    PlayerUtil.spawnParticlesFromTo(ParticleTypes.CLOUD, 1, serverLevel, Vec3.atBottomCenterOf(pos).add(0,1,0), redPanda.getEyePosition());
                    fixRedPandaLookAtStatue(state, redPanda);
                }

                if (animationTick == 5) {
                    redPanda.setDeltaMovement(redPanda.getDeltaMovement().add(0, 0.15, 0));
                } else if (animationTick >= 100) {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);

                    if (lightning != null) {
                        lightning.moveTo(Vec3.atBottomCenterOf(pos));
                        lightning.setVisualOnly(true);
                        serverLevel.addFreshEntity(lightning);
                    }

                    redPanda.setNoGravity(false);
                    redPanda.inventory.setStackInSlot(RedPandaEntity.HAT_SLOT, ModItems.IMAGINAL_DISK.get().getDefaultInstance());

                    motherStatue.setPlayingDiskAnimation(false);
                    motherStatue.setRedPanda(null);
                    motherStatue.setRedPandaUUID(null);
                    motherStatue.setAnimationTick(0);

                    if (redPanda.getOwner() != null) {
                        ModAdvancements.FIRST_RESOLVED_MYTH.trigger((ServerPlayer) redPanda.getOwner());
                    }

                    return;
                }

                motherStatue.setAnimationTick(animationTick + 1);
            }
        }
    }

    private static void fixRedPandaLookAtStatue(BlockState state, RedPandaEntity redPanda) {
        if (state.hasProperty(MotherStatueBlock.FACING)) {
            Direction facing = state.getValue(MotherStatueBlock.FACING);

            float degrees = facing.toYRot();
            redPanda.setYRot(degrees);
            redPanda.setYHeadRot(degrees);
            redPanda.yRotO = degrees;
            redPanda.yHeadRotO = degrees;
            redPanda.yBodyRot = degrees;
            redPanda.yBodyRotO = degrees;
        }
    }

    public void startDiskAnimation(RedPandaEntity redPanda) {
        this.isPlayingDiskAnimation = true;
        this.redPandaUUID = redPanda.getUUID();
        markUpdated();
    }

    public void setPlayingDiskAnimation(boolean isPlayingDiskAnimation) {
        this.isPlayingDiskAnimation = isPlayingDiskAnimation;
        markUpdated();
    }

    public boolean isPlayingDiskAnimation() {
        return this.isPlayingDiskAnimation;
    }

    public void setRedPanda(RedPandaEntity redPanda) {
        this.redPanda = redPanda;
        markUpdated();
    }

    public RedPandaEntity getRedPanda() {
        return this.redPanda;
    }

    public UUID getRedPandaUUID() {
        return this.redPandaUUID;
    }

    public void setRedPandaUUID(UUID redPandaUUID) {
        this.redPandaUUID = redPandaUUID;
        markUpdated();
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public void setAnimationTick(int animationTick) {
        this.animationTick = animationTick;
        markUpdated();
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("IsPlayingDiskAnimation")) {
            this.isPlayingDiskAnimation = tag.getBoolean("IsPlayingDiskAnimation");
        }
        if (tag.contains("RedPandaUUID")) {
            this.redPandaUUID = tag.getUUID("RedPandaUUID");
        }
        if (tag.contains("AnimationTick")) {
            this.animationTick = tag.getInt("AnimationTick");
        }
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putBoolean("IsPlayingDiskAnimation", this.isPlayingDiskAnimation);
        if (redPandaUUID != null) {
            tag.putUUID("RedPandaUUID", this.redPandaUUID);
        }
        tag.putInt("AnimationTick", this.animationTick);
        super.saveAdditional(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("IsPlayingDiskAnimation", isPlayingDiskAnimation);
        if (this.redPandaUUID != null) {
            compoundTag.putUUID("RedPandaUUID", this.redPandaUUID);
        }
        compoundTag.putInt("AnimationTick", this.animationTick);
        return compoundTag;
    }

    protected void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                this.getBlockPos().offset(-16, -16, -16).getCenter(),
                this.getBlockPos().offset(16, 16, 16).getCenter()
        );
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
