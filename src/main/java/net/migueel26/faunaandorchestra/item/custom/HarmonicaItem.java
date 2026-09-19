package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.entity.custom.TermiteBaby;
import net.migueel26.faunaandorchestra.networking.StartPlayerInstrumentMusicS2CPayload;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class HarmonicaItem extends AbstractGeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public HarmonicaItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        player.startUsingItem(hand);

        if (!level.isClientSide()) {
            PacketDistributor.sendToAllPlayers(new StartPlayerInstrumentMusicS2CPayload(
                    player.getUUID(), ModSounds.HARMONICA_SONG.get().getLocation())
            );
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 20);
        }

        if (!level.isClientSide()) {
            List<TermiteBaby> termiteBabies = level.getEntitiesOfClass(TermiteBaby.class, livingEntity.getBoundingBox().inflate(4.5f));
            for (TermiteBaby termiteBaby : termiteBabies) {
                termiteBaby.stopInPlace();
            }
        }
        super.releaseUsing(stack, level, livingEntity, timeCharged);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration % 20 == 0 && !level.isClientSide()) {
            List<TermiteBaby> termiteBabies = level.getEntitiesOfClass(TermiteBaby.class, livingEntity.getBoundingBox().inflate(4.5f));
            for (TermiteBaby termiteBaby : termiteBabies) {
                termiteBaby.getNavigation().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1, 1.0);
            }
        }

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
