package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MelomancyCauldronBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final static RawAnimation ACTIVE = RawAnimation.begin().thenPlay("active");
    private final AnimationController<MelomancyCauldronBlockEntity> controller = new AnimationController<>(this, "melomancy_cauldron_controller", 5, this::animController);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public MelomancyCauldronBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MELOMANCY_CAULDRON_BE.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    protected <E extends MelomancyCauldronBlockEntity> PlayState animController(final AnimationState<E> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
