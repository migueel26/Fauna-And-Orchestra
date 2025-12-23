package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.SingingCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SingingCropBlockEntity extends BlockEntity implements GeoBlockEntity {
    public final static RawAnimation EMERGE = RawAnimation.begin().thenPlay("emerge");
    private final AnimationController<SingingCropBlockEntity> controller = new AnimationController<>(this, "singing_crop_controller", 0, this::animController)
            .triggerableAnim("emerge", EMERGE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public SingingCropBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SINGING_CROP_BE.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    protected <E extends SingingCropBlockEntity> PlayState animController(final AnimationState<E> state) {

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
