package net.migueel26.faunaandorchestra.item.custom;

import com.mojang.datafixers.util.Pair;
import net.migueel26.faunaandorchestra.client.item.SoundSensorItemRenderer;
import net.migueel26.faunaandorchestra.client.item.VoiceVesselItemRenderer;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.entity.custom.projectile.SensorNote;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.util.SensorManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class SoundSensorItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static RawAnimation USE = RawAnimation.begin().thenPlay("use");
    protected final AnimationController<SoundSensorItem> soundSensorController = new AnimationController<>(this, "sound_sensor_controller", 0, this::predicate)
            .triggerableAnim("use", USE);
    public SoundSensorItem(Properties properties) {
        super(properties);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private <T extends GeoItem> PlayState predicate(AnimationState<T> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            ItemStack stack = player.getItemInHand(usedHand);

            List<EntityType<?>> entities = SensorManager.getScannableEntities(level);

            if (entities.isEmpty()) return InteractionResultHolder.fail(stack);

            if (player.isShiftKeyDown()) {
                // We change the current sound
                int current = getCurrentSound(stack);

                // Save the next instrument
                int next = (current + 1) % entities.size();
                setSound(stack, next);

                EntityType<?> nextEntity = entities.get(next);

                // Display the change
                player.displayClientMessage(
                        getInstrumentComponent(level, nextEntity), true);

                // Add cooldown
                player.getCooldowns().addCooldown(ModItems.SOUND_SENSOR.get(), 20);

                level.playSound(null, player.blockPosition(), SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.NEUTRAL, 1.0f, 1.0f + (level.random.nextFloat()/2)-0.25f);
                return InteractionResultHolder.consume(stack);

            } else {
                // Sensor Mode
                int index = getCurrentSound(stack);

                // Trigger Use animation
                triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(usedHand), (ServerLevel) level),"sound_sensor_controller", "use");

                if (index >= entities.size()) index = 0;

                EntityType<?> targetEntity = entities.get(index);

                // We add the cooldown
                player.getCooldowns().addCooldown(ModItems.SOUND_SENSOR.get(), 50);

                // We get the Biome for the Entity
                TagKey<Biome> biomeTag = SensorManager.getBiomeTagForEntity(targetEntity);

                Pair<BlockPos, Holder<Biome>> result = ((ServerLevel) level).findClosestBiome3d(
                        holder -> holder.is(biomeTag), player.blockPosition(), 6400, 32, 64);

                // We check and display the result
                if (result != null) {
                    BlockPos pos = result.getFirst();
                    SensorNote sensorNote = new SensorNote(level, player.getX(), player.getY(0.5), player.getZ());
                    sensorNote.signalTo(pos);
                    level.gameEvent(GameEvent.PROJECTILE_SHOOT, sensorNote.position(), GameEvent.Context.of(player));
                    float f = Mth.lerp(level.random.nextFloat(), 0.33F, 0.5F);
                    level.playSound(null, player.blockPosition(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 1.0F, f);
                    level.playSound(null, player.blockPosition(), ((InstrumentItem)((MusicalEntity) targetEntity.create(level)).getInstrument().get()).getSound(), SoundSource.NEUTRAL);
                    stack.hurtAndBreak(1, player, getEquipmentSlot(stack));
                    level.addFreshEntity(sensorNote);
                } else {
                    player.displayClientMessage(Component.translatable("item.faunaandorchestra.sound_sensor.not_found").withStyle(ChatFormatting.RED), true);
                }

                return InteractionResultHolder.fail(stack);
            }


        }
        return super.use(level, player, usedHand);
    }

    @NotNull
    private static MutableComponent getInstrumentComponent(Level level, EntityType<?> nextEntity) {
        return Component.translatable("item.faunaandorchestra.sound_sensor.desc")
                .append(Component.literal(((MusicalEntity) nextEntity.create(level)).getInstrument().get().getDescription().getString()).withStyle(ChatFormatting.GOLD));
    }

    private int getCurrentSound(ItemStack stack) {
        Integer currentSound = stack.get(ModDataComponents.LIST_INDEX);

        return currentSound != null ? currentSound : 0;
    }

    private void setSound(ItemStack stack, int sound) {
        stack.set(ModDataComponents.LIST_INDEX, sound);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.faunaandorchestra.sound_sensor.tooltip1"));
            tooltipComponents.add(Component.translatable("item.faunaandorchestra.sound_sensor.tooltip2"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }

        if (context.level() != null) {
            List<EntityType<?>> entities = SensorManager.getScannableEntities(context.level());
            int current = getCurrentSound(stack);
            if (current >= entities.size()) current = 0;

            EntityType<?> targetEntity = entities.get(current);
            Component original = getInstrumentComponent(context.level(), targetEntity);

            tooltipComponents.add(Component.literal(original.getString())
                    .withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private SoundSensorItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new SoundSensorItemRenderer();
                }
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(soundSensorController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
