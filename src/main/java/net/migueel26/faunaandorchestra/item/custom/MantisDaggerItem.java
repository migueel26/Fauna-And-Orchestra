package net.migueel26.faunaandorchestra.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.migueel26.faunaandorchestra.client.item.MantisDaggerItemRenderer;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class MantisDaggerItem extends Item implements GeoItem {
    public static final int CHARGE_TIME = 15;
    public static final float DEFAULT_DAMAGE = 5.0f;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static RawAnimation USE = RawAnimation.begin().thenPlay("use");
    protected static RawAnimation NOTHING = RawAnimation.begin().thenPlay("nothing");
    protected final AnimationController<MantisDaggerItem> controller = new AnimationController<>(this, "mantis_claws_controller", 0, this::predicate)
            .triggerableAnim("use", USE)
            .triggerableAnim("nothing", NOTHING);

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public MantisDaggerItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", DEFAULT_DAMAGE, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.9f, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    private <T extends GeoItem> PlayState predicate(AnimationState<T> state) {
        return PlayState.CONTINUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(usedHand), (ServerLevel) level), "mantis_claws_controller", "use");
        }
        player.startUsingItem(usedHand);
        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return CHARGE_TIME + 15;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration == CHARGE_TIME + 14) {
            level.playSound(null, livingEntity.blockPosition(), ModSounds.DAGGER_CHARGE.get(), SoundSource.NEUTRAL);
        }

        if (remainingUseDuration == 18 && !level.isClientSide()) {
            level.playSound(null, livingEntity.blockPosition(), ModSounds.PAN_FLUTE_WIND_IMPULSE.get(), SoundSource.NEUTRAL);
        }

        if (remainingUseDuration > 15 && remainingUseDuration % 3 == 0 && !level.isClientSide()) {
            ((ServerLevel) level).sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, livingEntity.getBlockStateOn()),
                    livingEntity.getX(), livingEntity.getBlockY(), livingEntity.getZ(), 25, 0.5f, 0.4f, 0.5f, 0.075);
        }

        if (remainingUseDuration == 15) {
            if (livingEntity instanceof Player player) {
                float speed = 2f;

                float f7 = player.getYRot();
                float f1 = player.getXRot();
                float f2 = -Mth.sin(f7 * 0.017453292F) * Mth.cos(f1 * 0.017453292F);
                float f3 = -Mth.sin(f1 * 0.017453292F);
                float f4 = Mth.cos(f7 * 0.017453292F) * Mth.cos(f1 * 0.017453292F);
                float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
                f2 *= speed / f5;
                f3 *= (speed-1) / f5;
                f4 *= speed / f5;
                player.push(f2, f3, f4);

                if (player.onGround()) {
                    player.move(MoverType.SELF, new Vec3(0.0, 1.2, 0.0));
                }

                if (!level.isClientSide()) {
                    player.startAutoSpinAttack(20);

                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    player.getCooldowns().addCooldown(stack.getItem(), 20);
                    ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 20, 0.1, 0.1, 0.1, 0.1);
                }
            }
        }

        if (remainingUseDuration == 1) {
            livingEntity.releaseUsingItem();
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!level.isClientSide()) {
            triggerAnim(livingEntity, GeoItem.getOrAssignId(stack, (ServerLevel) level), "mantis_claws_controller", "nothing");
        }
        super.releaseUsing(stack, level, livingEntity, timeCharged);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.faunaandorchestra.mantis_dagger.desc"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }

        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new MantisDaggerItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
