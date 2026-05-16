package net.migueel26.faunaandorchestra.item.custom.armor;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.FlowerPathBlock;
import net.migueel26.faunaandorchestra.client.item.armor.FloralBootsRenderer;
import net.migueel26.faunaandorchestra.client.item.armor.FluffyBootsRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class FloralBootsItem extends ArmorItem implements GeoItem {
    protected final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    protected final AnimationController<FloralBootsItem> controller = new AnimationController<>(this, "floral_boots_controller", 0, this::predicate);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public FloralBootsItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    public PlayState predicate(AnimationState<FloralBootsItem> state) {
        Entity entity = state.getData(DataTickets.ENTITY);
        if (entity instanceof LivingEntity livingEntity && livingEntity.walkAnimation.speed() > 0.01F) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!level.isClientSide() && entity instanceof Player player) {

            if (player.getItemBySlot(EquipmentSlot.FEET) == stack) {

                BlockPos posBelow = player.blockPosition().below();

                // -1 Durability every second on top of the flower path
                if (level.getBlockState(posBelow).is(ModBlocks.FLOWER_PATH.get()) || level.getBlockState(player.blockPosition()).is(ModBlocks.FLOWER_PATH.get())) {
                    if (player.tickCount % 20 == 0) {
                        stack.hurtAndBreak(1, (ServerLevel) level, player instanceof ServerPlayer sp ? sp : null,
                                item -> player.onEquippedItemBroken(item, EquipmentSlot.FEET));
                    }
                }

                // Creating the path
                if (!player.isCrouching()) {
                    Vec3 look = player.getLookAngle();

                    int dx = (int) Math.round(look.x);
                    int dz = (int) Math.round(look.z);

                    BlockPos posAheadBelow = posBelow.offset(dx, 0, dz);

                    tryPlacePath(level, posBelow);

                    if (dx != 0 || dz != 0) {
                        tryPlacePath(level, posAheadBelow);
                    }
                }
            }
        }
    }

    private void tryPlacePath(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.WATER) && level.getBlockState(pos.above()).isAir()) {
            level.setBlockAndUpdate(pos.above(), ModBlocks.FLOWER_PATH.get().defaultBlockState());
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if(this.renderer == null)
                    this.renderer = new FloralBootsRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.faunaandorchestra.floral_boots.desc").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
