package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.EmperorPenguinEntity;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class EverfruitItem extends Item {
    public EverfruitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (interactionTarget instanceof AgeableMob mob) {
            Level level = player.level();

            if (mob instanceof EmperorPenguinEntity penguin) {
                mob = penguin.convertTo(ModEntities.PENGUIN.get(), true);
            } else if (mob instanceof Frog) {
                interactionTarget = mob.convertTo(EntityType.TADPOLE, true);
            }

            mob.setAge(Integer.MIN_VALUE);
            mob.refreshDimensions();

            if (!level.isClientSide()) {
                interactionTarget.playSound(SoundEvents.CAMEL_EAT);
                level.playSound(null, player.blockPosition(), ModSounds.WOW.get(), SoundSource.NEUTRAL);

                ServerLevel serverLevel = (ServerLevel) level;

                if (!player.isCreative()) stack.shrink(1);

                for (int i = 0; i < 20; i++) {
                    float hue = serverLevel.random.nextFloat();
                    int rgbColor = Mth.hsvToRgb(hue, 1.0f, 1.0f);

                    float r = ((rgbColor >> 16) & 0xFF) / 255.0f;
                    float g = ((rgbColor >> 8) & 0xFF) / 255.0f;
                    float b = (rgbColor & 0xFF) / 255.0f;

                    DustParticleOptions rainbowDust = new DustParticleOptions(new Vector3f(r, g, b), 1.5f);

                    double offsetX = serverLevel.random.nextGaussian() * 0.3;
                    double offsetY = serverLevel.random.nextGaussian() * 0.3;
                    double offsetZ = serverLevel.random.nextGaussian() * 0.3;

                    serverLevel.sendParticles(rainbowDust,
                            interactionTarget.getX() + offsetX,
                            interactionTarget.getEyeY() - 0.2f + offsetY,
                            interactionTarget.getZ() + offsetZ,
                            1, 0, 0, 0, 0.0);
                }

            }
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }
}
