package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ComposerGravestoneBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final static RawAnimation SHAKE = RawAnimation.begin().thenPlay("shake");
    private final static RawAnimation OPEN = RawAnimation.begin().thenPlay("open");
    private final static RawAnimation CLOSE = RawAnimation.begin().thenPlay("close");
    private final static RawAnimation OPENED = RawAnimation.begin().thenPlay("idle_open");
    private final static RawAnimation CLOSED = RawAnimation.begin().thenPlay("idle_closed");
    private final AnimationController<ComposerGravestoneBlockEntity> controller = new AnimationController<>(this, "gravestone_controller", 0, this::animController)
            .triggerableAnim("gravestone_shake", SHAKE)
            .triggerableAnim("gravestone_open", OPEN)
            .triggerableAnim("gravestone_close", CLOSE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public ComposerGravestoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COMPOSER_GRAVESTONE_BE.get(), pos, blockState);
        if (blockState.getBlock() == ModBlocks.GRAVESTONE.get()) controller.transitionLength(5);
    }

    protected <E extends ComposerGravestoneBlockEntity> PlayState animController(final AnimationState<E> state) {
        if (getBlockState().getValue(ComposerGravestoneBlock.OPENED)) {
            state.setAnimation(OPENED);
        } else {
            state.setAnimation(CLOSED);
        }
        return PlayState.CONTINUE;
    }

    public void shake() {
        triggerAnim("gravestone_controller", "gravestone_shake");
    }

    public void open() {
        triggerAnim("gravestone_controller", "gravestone_open");
    }

    public void close() {
        triggerAnim("gravestone_controller", "gravestone_close");
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(16);
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
