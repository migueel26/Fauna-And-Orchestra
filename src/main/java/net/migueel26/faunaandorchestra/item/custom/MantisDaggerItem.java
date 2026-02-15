package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.client.item.MantisDaggerItemRenderer;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
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

    public MantisDaggerItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, DEFAULT_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.9000000953674316, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2);
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
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
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
                float damage = DEFAULT_DAMAGE;

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
                    float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    ArmorStand dummy = new ArmorStand(level, player.getX(), player.getY(), player.getZ());

                    damage = EnchantmentHelper.modifyDamage(
                            (ServerLevel) level,
                            stack,
                            dummy,
                            player.damageSources().playerAttack(player),
                            baseDamage
                    );

                    player.startAutoSpinAttack(20, damage, stack);

                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
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

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private MantisDaggerItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new MantisDaggerItemRenderer();
                }
                return renderer;
            }
        });
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
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
