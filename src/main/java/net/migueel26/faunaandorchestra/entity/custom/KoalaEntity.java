package net.migueel26.faunaandorchestra.entity.custom;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.entity.trades.KoalaTrades;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;

public class KoalaEntity extends AgeableMob implements Merchant, Npc, GeoEntity {
    private static final RawAnimation SLEEP = RawAnimation.begin().thenPlay("sleep");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation WAKE_UP = RawAnimation.begin().thenPlay("wake_up");
    private static final RawAnimation STAND_UP = RawAnimation.begin().thenPlay("stand_up");
    private static final RawAnimation SIT_DOWN = RawAnimation.begin().thenPlay("sit_down");
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(KoalaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(KoalaEntity.class, EntityDataSerializers.BOOLEAN);
    @Nullable
    private Player tradingPlayer;
    @Nullable
    protected MerchantOffers offers;
    private static final Logger LOGGER = LogUtils.getLogger();
    protected boolean isSleeping;
    protected boolean isSitting;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public KoalaEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
        this.isSitting = false;
        this.isSleeping = false;

        addOverridenGoals();
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SITTING, false);
        builder.define(SLEEPING, false);
        super.defineSynchedData(builder);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        // LookAtPlayerGoal (2)
        // RandomStrollGoal (4)
        goalSelector.addGoal(5, new FaunaRandomLookAroundGoal(this));
    }

    private void addOverridenGoals() {
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((KoalaEntity) mob).isSitting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !((KoalaEntity) mob).isSitting();
            }
        });
        goalSelector.addGoal(4, new RandomStrollGoal(this, 0.75D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((KoalaEntity) mob).isSitting();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !((KoalaEntity) mob).isSitting();
            }
        });
    }

    private <E extends GeoAnimatable> PlayState koalaState(AnimationState<E> state) {
        if (isKoalaSleeping()) {
            state.getController().setAnimation(SLEEP);
        } else if (isSitting()) {
            state.getController().setAnimation(SIT);
        } else if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(SITTING)) {
            this.isSitting = entityData.get(SITTING);
        } else if (key.equals(SLEEPING)) {
            this.isSleeping = entityData.get(SLEEPING);
        }
        super.onSyncedDataUpdated(key);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.level().isClientSide) {
            throw new IllegalStateException("Cannot load Villager offers on the client");
        } else {
            if (this.offers == null) {
                this.offers = new MerchantOffers();
                this.updateTrades();
            }

            return this.offers;
        }
    }

    private void updateTrades() {
        VillagerTrades.ItemListing[] sheets = KoalaTrades.WANDERING_KOALA_TRADES.get(1);
        VillagerTrades.ItemListing[] instruments = KoalaTrades.WANDERING_KOALA_TRADES.get(2);
        if (sheets != null && instruments != null) {
            MerchantOffers merchantOffers = this.getOffers();
            this.addOffersFromItemListings(merchantOffers, sheets, instruments, 5);

            /*int i = this.random.nextInt(sheets.length);
            VillagerTrades.ItemListing itemListing = sheets[i];
            MerchantOffer merchantOffer = itemListing.getOffer(this, this.random);
            if (merchantOffer != null) {
                merchantOffers.add(merchantOffer);
            }*/
        }
    }

    private void addOffersFromItemListings(MerchantOffers givenMerchantOffers, VillagerTrades.ItemListing[] sheets, VillagerTrades.ItemListing[] instruments, int maxNumbers) {
        ArrayList<VillagerTrades.ItemListing> sheetsList = Lists.newArrayList(sheets);
        ArrayList<VillagerTrades.ItemListing> instrumentsList = Lists.newArrayList(instruments);
        int i = 0;

        while (i < maxNumbers && !sheetsList.isEmpty()) {
            MerchantOffer merchantoffer = null;
            if (i <= 1)
                merchantoffer = sheetsList.remove(this.random.nextInt(sheetsList.size())).getOffer(this, this.random);
            if (i > 1)
                merchantoffer = instrumentsList.remove(this.random.nextInt(instrumentsList.size())).getOffer(this, this.random);
            if (merchantoffer != null) {
                givenMerchantOffers.add(merchantoffer);
                i++;
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && !this.isTrading() && !this.isBaby()) {
            if (!this.level().isClientSide) {
                if (this.getOffers().isEmpty()) {
                    return InteractionResult.CONSUME;
                }

                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Offers")) {
            MerchantOffers.CODEC
                    .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("Offers"))
                    .resultOrPartial(Util.prefix("Failed to load offers: ", LOGGER::warn))
                    .ifPresent(p_323775_ -> this.offers = p_323775_);
        }
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (!this.level().isClientSide) {
            MerchantOffers merchantoffers = this.getOffers();
            if (!merchantoffers.isEmpty()) {
                compound.put(
                        "Offers", MerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), merchantoffers).getOrThrow()
                );
            }
        }
    }
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void setTradingPlayer(@Nullable Player tradingPlayer) {
        this.tradingPlayer = tradingPlayer;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    @Override
    public void overrideOffers(MerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(offer);
        if (this.tradingPlayer instanceof ServerPlayer) {
            //CriteriaTriggers.TRADE.trigger((ServerPlayer)this.tradingPlayer, this, offer.getResult());
        }
    }

    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.makeSound(this.getTradeUpdatedSound(!stack.isEmpty()));
        }
    }

    protected SoundEvent getTradeUpdatedSound(boolean getYesSound) {
        return getYesSound ? SoundEvents.WANDERING_TRADER_YES : SoundEvents.WANDERING_TRADER_NO;
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int xp) {

    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide();
    }

    public boolean isKoalaSleeping() {
        return entityData.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        entityData.set(SLEEPING, sleeping);
    }

    public boolean isSitting() {
        return entityData.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        entityData.set(SITTING, sitting);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "koala_controller", 5, this::koalaState)
                .triggerableAnim("stand_up", STAND_UP)
                .triggerableAnim("sit_down", SIT_DOWN)
                .triggerableAnim("wake_up", WAKE_UP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
