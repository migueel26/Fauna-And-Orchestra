package net.migueel26.faunaandorchestra.item.custom;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BasicGeoItem extends AbstractGeoItem {
    private final RawAnimation idleAnimation;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public BasicGeoItem(Properties properties) {
        super(properties);

        this.idleAnimation = null;
    }
    public BasicGeoItem(String idleAnimationName, Properties properties) {
        super(properties);

        this.idleAnimation = RawAnimation.begin().thenPlay(idleAnimationName);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (idleAnimation != null) {
            controllers.add(new AnimationController<GeoAnimatable>(this, "", 5, state -> {
                state.getController().setAnimation(idleAnimation);
                return PlayState.CONTINUE;
            }));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
