package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.SewingMachineBlock;
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

public class SewingMachineBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected final RawAnimation OOPS = RawAnimation.begin().thenPlay("oops");
    protected final RawAnimation USE = RawAnimation.begin().thenPlay("use");
    protected final AnimationController<SewingMachineBlockEntity> controller = new AnimationController<>(this, "sewing_machine_controller", 0, this::animController)
            .triggerableAnim("oops", OOPS);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public SewingMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SEWING_MACHINE_BE.get(), pos, blockState);
    }

    protected <E extends SewingMachineBlockEntity> PlayState animController(final AnimationState<E> state) {
        BlockState blockState = getBlockState();
        if (blockState.getValue(SewingMachineBlock.SEWING)) {
            state.getController().setAnimation(USE);
        } else {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    public void oops() {
        triggerAnim("sewing_machine_controller", "oops");
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
