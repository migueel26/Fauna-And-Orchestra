package net.migueel26.faunaandorchestra.item.custom.armor;

import net.migueel26.faunaandorchestra.client.item.armor.FluffyBootsRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class FluffyBootsItem extends ArmorItem implements GeoItem {
    public static final UUID FLUFFY_SPEED_UUID = UUID.fromString("d838b0fb-59c4-42ea-a417-640fb09e7c10");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FluffyBootsItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {

            AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

            if (speedAttribute != null) {
                BlockState stateBelow = level.getBlockState(player.blockPosition().below());

                boolean hasBoost = speedAttribute.getModifier(FLUFFY_SPEED_UUID) != null;
                boolean isWearingBoots = player.getItemBySlot(EquipmentSlot.FEET) == stack;
                boolean isOnSnow = player.onGround() &&
                        (stateBelow.is(BlockTags.SNOW) || stateBelow.is(BlockTags.SAND));

                if (isWearingBoots && isOnSnow) {
                    if (!hasBoost) {
                        AttributeModifier modifier = new AttributeModifier(FLUFFY_SPEED_UUID, "Fluffy Boots Speed Boost",0.4D, AttributeModifier.Operation.MULTIPLY_BASE);

                        speedAttribute.addTransientModifier(modifier);
                    }
                } else {
                    if (hasBoost) {
                        speedAttribute.removeModifier(FLUFFY_SPEED_UUID);
                    }
                }
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private <T extends GeoItem> PlayState predicate(AnimationState<T> state) {
        return PlayState.STOP;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new FluffyBootsRenderer();
                }

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return stack.is(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.faunaandorchestra.fluffy_boots.desc1").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.translatable("item.faunaandorchestra.fluffy_boots.desc2").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
