package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.ListenerBlock;
import net.migueel26.faunaandorchestra.block.custom.ListenerContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ListenerContainerBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final static RawAnimation LISTEN = RawAnimation.begin().thenPlay("listen");
    private final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final AnimationController<ListenerContainerBlockEntity> controller = new AnimationController<>(this, "listener_controller", 5, this::animController);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public ListenerContainerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LISTENER_CONTAINER_BE.get(), pos, blockState);
    }

    protected <E extends ListenerContainerBlockEntity> PlayState animController(final AnimationState<E> state) {
        if (getBlockState().getValue(ListenerContainerBlock.LISTENING)) {
            state.getController().setAnimation(LISTEN);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
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
