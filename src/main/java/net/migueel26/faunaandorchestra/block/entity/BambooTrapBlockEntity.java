package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.BambooTrapBlock;
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

public class BambooTrapBlockEntity extends BlockEntity implements GeoBlockEntity {
    public final static RawAnimation TRAP = RawAnimation.begin().thenPlay("trap");
    public final static RawAnimation OPEN = RawAnimation.begin().thenPlay("open");
    public final static RawAnimation OPENED = RawAnimation.begin().thenPlay("opened");
    public final static RawAnimation TRAPPED = RawAnimation.begin().thenPlay("trapped");
    private final AnimationController<BambooTrapBlockEntity> controller = new AnimationController<>(this, "trap_controller", 5, this::animController)
            .triggerableAnim("trap", TRAP)
            .triggerableAnim("open", OPEN);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public BambooTrapBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BAMBOO_TRAP_BE.get(), pos, blockState);
    }

    protected <E extends BambooTrapBlockEntity> PlayState animController(final AnimationState<E> state) {
        if (getBlockState().getValue(BambooTrapBlock.OPEN)) {
            state.getController().setAnimation(OPENED);
        } else {
            state.getController().setAnimation(TRAPPED);
        }
        return PlayState.CONTINUE;
    }

    public void trap() {
        triggerAnim("trap_controller", "trap");
    }

    public void open() {
        triggerAnim("trap_controller", "open");
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
