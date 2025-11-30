package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.client.item.VoiceVesselItemRenderer;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.particles.custom.VoiceParticle;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.PlayerUtil;
import net.migueel26.faunaandorchestra.util.VesselUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class VoiceVesselItem extends Item implements GeoItem {
    protected static final int ABSORB_TIME = 80;
    protected Mob mob = null;
    protected Vec3 particlePos = null;
    protected int timestamp = -1;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static RawAnimation OPEN = RawAnimation.begin().thenPlay("open");
    protected static RawAnimation CLOSE = RawAnimation.begin().thenPlayAndHold("close");
    protected static RawAnimation ABSORB = RawAnimation.begin().thenPlay("absorb");
    public VoiceVesselItem(Properties properties) {
        super(properties);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (stack.is(this) && stack.has(ModDataComponents.OPENED)) {
            if (!stack.get(ModDataComponents.OPENED)) {
                if (!level.isClientSide()) triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(usedHand), (ServerLevel) level),"vessel_controller", "open");
                level.playSound(player, player.blockPosition(), ModSounds.VESSEL_CLICK.get(), SoundSource.NEUTRAL, 1.0f, 1.0f + (level.random.nextFloat()/2 - 0.25f));
                stack.set(ModDataComponents.OPENED, true);

                player.startUsingItem(usedHand);
            } else {
                releaseUsing(stack, level, player, 1);
            }
        }


        return super.use(level, player, usedHand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (mob == null) {
                Optional<Mob> pEntity = level.getNearbyEntities(Mob.class, TargetingConditions.DEFAULT, livingEntity, livingEntity.getBoundingBox().inflate(7.0))
                        .stream()
                        .filter(VesselUtil::isEntityAptForVessel)
                        .filter(mob -> !mob.isSilent())
                        .findFirst();

                if (pEntity.isPresent()) {
                    this.mob = pEntity.get();
                    mob.playAmbientSound();
                    timestamp = remainingUseDuration;
            }

        } else if (stack.has(ModDataComponents.OPENED) && stack.get(ModDataComponents.OPENED)) {
            if (remainingUseDuration % 5 == 0) {
                level.playSound(livingEntity, livingEntity.blockPosition(), ModSounds.VESSEL_AIR.get(), SoundSource.PLAYERS, 0.1f, 1.0f + (level.random.nextFloat() - 0.5f));
            }

            if (remainingUseDuration == timestamp - ABSORB_TIME) {
                this.particlePos = mob.position().add(0, mob.getBbHeight() + 1.5, 0);
                if (level.isClientSide()) {
                    level.addParticle(ModParticleTypes.VOICE_PARTICLE.get(), mob.getX(), particlePos.y - 1.5, mob.getZ(), 0, 0.35f, 0);
                }
                level.playSound(livingEntity, livingEntity.blockPosition(), ModSounds.VESSEL_COLLECT.get(), SoundSource.NEUTRAL, 1.0f, 0.5f);
            }

            if (remainingUseDuration == timestamp - ABSORB_TIME - 40 || remainingUseDuration == timestamp - ABSORB_TIME - VesselUtil.LIFETIME) {
                if (!level.isClientSide()) {
                    PlayerUtil.spawnParticlesFromTo(ParticleTypes.CLOUD, 1, (ServerLevel) level, particlePos, livingEntity.position().add(0, 1, 0));
                }
                level.playSound(livingEntity, livingEntity.blockPosition(), ModSounds.VESSEL_COLLECT.get(), SoundSource.NEUTRAL, 1.0f, 1.5f);
            }


            if (remainingUseDuration == timestamp - ABSORB_TIME - VesselUtil.LIFETIME - 60) {
                if (!level.isClientSide()) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, particlePos.x(), particlePos.y(), particlePos.z(), 40, 0.3, 0.3, 0.3, 0.1);
                    PlayerUtil.spawnParticlesFromTo(ParticleTypes.CLOUD, 3, (ServerLevel) level, particlePos, livingEntity.position().add(0, 1, 0));
                }

                ((Player) livingEntity).addItem(VesselUtil.voiceOfEntity(mob.getType()));
                level.playSound(livingEntity, livingEntity.blockPosition(), ModSounds.VESSEL_COLLECT.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);

            }

            if (remainingUseDuration == timestamp - ABSORB_TIME - VesselUtil.LIFETIME - 65) {
               livingEntity.releaseUsingItem();
            }
        }



        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!level.isClientSide()) triggerAnim(livingEntity, GeoItem.getOrAssignId(livingEntity.getItemInHand(livingEntity.getUsedItemHand()), (ServerLevel) level),"vessel_controller", "close");
        if (stack.get(ModDataComponents.OPENED)) level.playSound(livingEntity, livingEntity.blockPosition(), ModSounds.VESSEL_CLICK.get(), SoundSource.NEUTRAL, 1.0f, 1.0f + (level.random.nextFloat()/2 - 0.25f));
        stack.set(ModDataComponents.OPENED, false);
        if (this.mob != null) {
            mob.setSilent(true);
        }
        this.mob = null;
        this.timestamp = -1;
        super.releaseUsing(stack, level, livingEntity, timeCharged);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "vessel_controller", 1, this::predicate)
                .triggerableAnim("open", OPEN)
                .triggerableAnim("close", CLOSE));
    }

    private <T extends GeoItem> PlayState predicate(AnimationState<T> state) {
        ItemStack stack = state.getData(DataTickets.ITEMSTACK);
        if (stack.has(ModDataComponents.OPENED) && stack.get(ModDataComponents.OPENED)) {
            state.getController().setAnimation(ABSORB);
        } else {
            state.getController().stop();
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private VoiceVesselItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new VoiceVesselItemRenderer();
                }
                return renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.faunaandorchestra.voice_vessel.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
