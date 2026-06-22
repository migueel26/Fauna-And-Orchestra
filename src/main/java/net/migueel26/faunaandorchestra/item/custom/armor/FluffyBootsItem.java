package net.migueel26.faunaandorchestra.item.custom.armor;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.client.item.armor.FluffyBootsRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class FluffyBootsItem extends ArmorItem implements GeoItem {
    public static final ResourceLocation FLUFFY_SPEED_ID = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "fluffy_speed_boost");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public FluffyBootsItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {

            AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

            if (speedAttribute != null) {
                BlockState stateBelow = level.getBlockState(player.blockPosition().below());

                boolean hasBoost = speedAttribute.hasModifier(FLUFFY_SPEED_ID);
                boolean isWearingBoots = player.getItemBySlot(EquipmentSlot.FEET) == stack;
                boolean isOnSnow = player.onGround() &&
                        (stateBelow.is(BlockTags.SNOW) || stateBelow.is(BlockTags.SAND));

                if (isWearingBoots && isOnSnow) {
                    if (!hasBoost) {
                        AttributeModifier modifier = new AttributeModifier(FLUFFY_SPEED_ID, 0.4D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

                        speedAttribute.addTransientModifier(modifier);
                    }
                } else {
                    if (hasBoost) {
                        speedAttribute.removeModifier(FLUFFY_SPEED_ID);
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
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if(this.renderer == null)
                    this.renderer = new FluffyBootsRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return stack.is(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.faunaandorchestra.fluffy_boots.desc1").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.translatable("item.faunaandorchestra.fluffy_boots.desc2").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
