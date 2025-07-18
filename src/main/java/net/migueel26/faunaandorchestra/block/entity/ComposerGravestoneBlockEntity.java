package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ComposerGravestoneBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final static RawAnimation SHAKE = RawAnimation.begin().thenPlay("shake");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public ComposerGravestoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COMPOSER_GRAVESTONE_BE.get(), pos, blockState);
    }

    protected <E extends ComposerGravestoneBlockEntity> PlayState animController(final AnimationState<E> state) {
        return PlayState.CONTINUE;
    }

    public void shake() {
        triggerAnim("gravestone_controller", "gravestone_shake");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "gravestone_controller", 0, this::animController)
                .triggerableAnim("gravestone_shake", SHAKE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
