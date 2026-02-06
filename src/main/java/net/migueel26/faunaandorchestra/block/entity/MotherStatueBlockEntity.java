package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.migueel26.faunaandorchestra.block.custom.MotherStatueBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MotherStatueBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final static RawAnimation SING = RawAnimation.begin().thenPlay("sing");
    private final AnimationController<MotherStatueBlockEntity> controller = new AnimationController<>(this, "mother_statue_controller", 0, this::animController);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public MotherStatueBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MOTHER_STATUE_BE.get(), pos, blockState);
    }

    protected <E extends MotherStatueBlockEntity> PlayState animController(final AnimationState<E> state) {
        state.setAnimation(IDLE);
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
