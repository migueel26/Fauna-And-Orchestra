package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.BeaverStatueBlock;
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

public class BeaverStatueBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected final RawAnimation ON = RawAnimation.begin().thenPlayAndHold("on");
    protected final RawAnimation OFF = RawAnimation.begin().thenPlayAndHold("off");
    protected final RawAnimation ON_TO_OFF = RawAnimation.begin().thenPlay("on_to_off");
    protected final RawAnimation OFF_TO_ON = RawAnimation.begin().thenPlay("off_to_on");
    protected final AnimationController<BeaverStatueBlockEntity> controller = new AnimationController<>(this, "beaver_statue_controller", 0, this::animController)
            .triggerableAnim("activate", OFF_TO_ON)
            .triggerableAnim("deactivate", ON_TO_OFF);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public BeaverStatueBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BEAVER_STATUE_BE.get(), pos, blockState);
    }

    protected <E extends BeaverStatueBlockEntity> PlayState animController(final AnimationState<E> state) {
        BlockState blockState = getBlockState();
        if (blockState.getValue(BeaverStatueBlock.ENABLED)) {
            state.getController().setAnimation(ON);
        } else {
            state.getController().setAnimation(OFF);
        }
        return PlayState.CONTINUE;
    }

    public void activate() {
        triggerAnim("beaver_statue_controller", "activate");
    }

    public void deactivate() {
        triggerAnim("beaver_statue_controller", "deactivate");
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
