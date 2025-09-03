package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.ListenerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ListenerBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final static RawAnimation LISTEN = RawAnimation.begin().thenPlay("listen");
    private final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final AnimationController<ListenerBlockEntity> controller = new AnimationController<>(this, "listener_controller", 5, this::animController);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public ListenerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LISTENER_BE.get(), pos, blockState);
    }

    protected <E extends ListenerBlockEntity> PlayState animController(final AnimationState<E> state) {
        if (getBlockState().getValue(ListenerBlock.LISTENING)) {
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
