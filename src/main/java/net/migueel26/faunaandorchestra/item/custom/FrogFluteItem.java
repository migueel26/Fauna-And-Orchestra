package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FrogFluteItem extends Item {
    public FrogFluteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        // We play the sounds
        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                ModSounds.FLUTE_USE.get(), SoundSource.NEUTRAL,
                0.2F,
                0.5F + level.getRandom().nextFloat()/2
        );
        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.FROG_AMBIENT, SoundSource.NEUTRAL,
                2F,
                1F + level.getRandom().nextFloat()
        );

        // Call all the frogs nearby
        List<QuirkyFrogEntity> frogs = level.getEntitiesOfClass(QuirkyFrogEntity.class, player.getBoundingBox().inflate(48), frog -> !frog.isTame());
        for (QuirkyFrogEntity frog : frogs) {
            frog.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), 4F);
            if (level.isClientSide()) {
                level.addParticle(ParticleTypes.NOTE, frog.getX(), frog.getY() + 2.5, frog.getZ(), 0F, 0.5F, 0F);
            }
        }

        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.faunaandorchestra.frog_flute.desc"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }

        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
