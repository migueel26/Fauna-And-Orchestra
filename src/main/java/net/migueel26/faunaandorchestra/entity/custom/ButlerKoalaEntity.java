package net.migueel26.faunaandorchestra.entity.custom;

import com.google.common.collect.Lists;
import net.migueel26.faunaandorchestra.entity.goals.*;
import net.migueel26.faunaandorchestra.entity.trades.KoalaTrades;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;

public class ButlerKoalaEntity extends AbstractKoalaEntity {
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation SERVE = RawAnimation.begin().thenPlay("serve");
    private static final RawAnimation IDLE_SERVE = RawAnimation.begin().thenPlay("idle_serve");
    private static final EntityDataAccessor<Boolean> SERVING = SynchedEntityData.defineId(ButlerKoalaEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimationController<ButlerKoalaEntity> controller = new AnimationController<>(this, "butler_koala_controller", 5, this::koalaState)
            .triggerableAnim("serve", SERVE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public ButlerKoalaEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        addOverridenGoals();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SERVING, false);
        super.defineSynchedData(builder);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        // PanicGoal (0)
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new KoalaServePlayerGoal(this, Player.class, 5.5f));
        // RandomStrollGoal (4)
        goalSelector.addGoal(5, new FaunaRandomLookAroundGoal(this));
    }

    private void addOverridenGoals() {
        goalSelector.addGoal(0, new PanicGoal(this, 1.5) {
            @Override
            public void start() {
                if (mob instanceof ButlerKoalaEntity koala) koala.setServing(false);
                super.start();
            }
        });

        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.75D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((ButlerKoalaEntity) mob).isServing();
            }

        });
    }

    private <E extends GeoAnimatable> PlayState koalaState(AnimationState<E> state) {
        if (this.isServing()) {
            state.getController().setAnimation(IDLE_SERVE);
        } else if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && !this.isTrading() && !this.isBaby() && this.isServing()) {
            if (!this.level().isClientSide) {
                if (this.getOffers().isEmpty()) {
                    return InteractionResult.CONSUME;
                }

                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else if (!this.isServing()){
            if (this.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("text.faunaandorchestra.must_serve_butler_koala"), true);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("IsServing", this.isServing());

        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.entityData.set(SERVING, compound.getBoolean("IsServing"));

        super.readAdditionalSaveData(compound);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    protected void updateTrades() {
        VillagerTrades.ItemListing[] food = KoalaTrades.BUTLER_KOALA_TRADES.get(1);
        if (food != null) {
            MerchantOffers offers = this.offers;
            addOffersFromItemListings(offers, food, 1);

        }
    }

    private void addOffersFromItemListings(MerchantOffers givenMerchantOffers, VillagerTrades.ItemListing[] food, int maxNumbers) {
        ArrayList<VillagerTrades.ItemListing> foodList = Lists.newArrayList(food);
        int i = 0;

        while (i < maxNumbers && !foodList.isEmpty()) {
            MerchantOffer merchantoffer = null;
            if (i <= 1)
                merchantoffer = foodList.remove(this.random.nextInt(foodList.size())).getOffer(this, this.random);
            if (merchantoffer != null) {
                givenMerchantOffers.add(merchantoffer);
                i++;
            }
        }
    }

    public boolean isServing() {
        return entityData.get(SERVING);
    }

    public void setServing(boolean isServing) {
        entityData.set(SERVING, isServing);
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
