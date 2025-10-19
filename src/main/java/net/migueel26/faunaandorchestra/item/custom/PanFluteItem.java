package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.decorative.HealthFluteEntity;
import net.migueel26.faunaandorchestra.entity.custom.projectile.PhantomNoteProjectileEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.PlayerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class PanFluteItem extends Item {
    public static final int PUSH_PARTICLES = 10;
    public static final int POWERLESS_COOLDOWN = 20;
    public static final int DEFAULT_COOLDOWN = 80;
    public static final int HEALTH_COOLDOWN = 200;
    private static final int HEALTH_ENTITIES = 5;
    public final List<String> powersString = new ArrayList<>(List.of(
            "notes",
            "push",
            "health",
            "wind",
            "nature"
    ));

    public PanFluteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack flute = player.getItemInHand(usedHand);
        List<Integer> powers = flute.get(ModDataComponents.PAN_FLUTE_LIST);
        Integer currentSound = flute.get(ModDataComponents.PAN_FLUTE_SOUND);

        if (Screen.hasShiftDown() && powers != null && !powers.isEmpty()) {
            if (currentSound != null) {
                if (currentSound == 0) currentSound = 1;
                else currentSound = (currentSound+1) % powers.size();

                player.displayClientMessage(
                        Component.translatable("item.faunaandorchestra.pan_flute." + powersString.get(currentSound)), true);
            }

            flute.set(ModDataComponents.PAN_FLUTE_SOUND, currentSound);
            level.playSound(player, player.blockPosition(), ModSounds.PAN_FLUTE_CHANGE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), POWERLESS_COOLDOWN);

            return InteractionResultHolder.consume(flute);

        } else if (currentSound != null && powers != null) {
            SoundEvent fluteSound = ModSounds.PAN_FLUTE_USE.get();

            // If it doesn't have powers, we just add the default cooldown with default sound
            if (powers.isEmpty()) player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), 60);
            else {
                fluteSound = getFluteSound(player, powers, currentSound, fluteSound);

                // We execute the power
                switch (powers.get(currentSound)) {
                    case 1 -> executeNotes(level, player, usedHand);
                    case 2 -> executePush(level, player, flute);
                    case 3 -> executeHealth(level, player, flute);
                    case 4 -> executeWind(level, player, flute);
                    case 5 -> executeNature(level, player, flute);
                }

                flute.hurtAndBreak(1, player, usedHand.equals(InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }

            // We play the sound
            level.playSound(player, player.blockPosition(), fluteSound, SoundSource.PLAYERS, 1.0f, 1.0f);

            return InteractionResultHolder.consume(flute);
        }

        return InteractionResultHolder.fail(flute);
    }

    private void executeNotes(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
    }

    private void executePush(Level level, Player player, ItemStack flute) {
        List<Entity> targets = PlayerUtil.entitiesInFrontOf(player, 7.0f, level, Entity.class, null);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.PAN_FLUTE_PUSH_WIND.get(), SoundSource.NEUTRAL, 0.5f, 1.0f);

        for (Entity entity : targets) {
            Vec3 lookAngle = player.getLookAngle();
            Vec3 particleDir = player.getLookAngle().normalize().reverse().scale(0.45f);
            entity.setDeltaMovement(lookAngle.normalize().scale(1.5).add(0, 0.5, 0));

            if (!level.isClientSide()) {
                // Add wind knock particle
                ((ServerLevel) level).sendParticles(ParticleTypes.GUST, entity.getX(), entity.getY(), entity.getZ(),
                        8, 0.4f, 0.4f, 0.4f, 0.1f);

                Vec3 start = player.position().add(0, player.getEyeHeight(), 0); // from eyes
                Vec3 end = entity.position().add(0, entity.getBbHeight() / 2, 0); // to middle of entity
                Vec3 dir = end.subtract(start);
                double distance = dir.length();
                dir = dir.normalize();

                // Step every 0.5 blocks, add cloud trail
                for (double i = 0; i < distance; i += 0.5) {
                    double pOffset = 0.05f + i / 15.0;
                    Vec3 pos = start.add(dir.scale(i));
                    ((ServerLevel) level).sendParticles(
                            ParticleTypes.CLOUD,
                            pos.x, pos.y, pos.z,
                            4, // count
                            pOffset, pOffset, pOffset, // spread
                            0.01f // speed
                    );
                }
            }

        }

        player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), DEFAULT_COOLDOWN);

    }

    private void executeHealth(Level level, Player player, ItemStack flute) {
        // We spawn the health trails
        for (int i = 0; i < HEALTH_ENTITIES; i++) {
            HealthFluteEntity entity = new HealthFluteEntity(EntityType.BAT, level);
            entity.setInvisible(true);
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 5, true, false, false));
            entity.setPos(player.position().add(0, 0.5, 0));

            level.addFreshEntity(entity);
        }


        player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), HEALTH_COOLDOWN);
    }

    private void executeWind(Level level, Player player, ItemStack flute) {
        Vec3 lookAngle = player.getLookAngle();

        level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.PAN_FLUTE_WIND_IMPULSE.get(), SoundSource.NEUTRAL, 0.5f, 1.0f);
        if (!level.isClientSide()) {
            ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 20, 0.1, 0.1, 0.1, 0.1);
        }

        player.setDeltaMovement(lookAngle.normalize().scale(2.0));
        player.setIgnoreFallDamageFromCurrentImpulse(true);

        player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), DEFAULT_COOLDOWN);
    }

    private void executeNature(Level level, Player player, ItemStack flute) {
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        // Future upgrades (charge, hold...)
        switch (getPower(stack)) {
            case 1 -> {
                    if (remainingUseDuration % 5 == 0) {
                    Player player = (Player) livingEntity;
                    Vec3 vec3 = player.getViewVector(1.0F);
                    Vec3 vec31 = vec3.normalize().scale(2.0f);

                    PhantomNoteProjectileEntity note = new PhantomNoteProjectileEntity(player, vec31.normalize(), level);
                    note.setGood(true);

                    double rx = level.random.nextDouble()*1.5-0.75;
                    double ry = level.random.nextDouble()-0.5;
                    double rz = level.random.nextDouble()*1.5-0.75;

                    double noteX = player.getX() + vec3.x * 1.35 + rx;
                    double noteY = player.getY(0.6) + ry;
                    double noteZ = note.getZ() + vec3.z * 1.35 + rz;
                    note.setPos(noteX, noteY, noteZ);

                    if (!level.isClientSide()) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, noteX, noteY, noteZ, 15, 0.1, 0.1, 0.1, 0.05);
                    }

                    level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 0.9f, 1.0f + level.random.nextFloat()/2);
                    level.addFreshEntity(note);

                    if (remainingUseDuration <= 5) {
                        releaseUsing(stack, level, livingEntity, remainingUseDuration);
                    }
                }
            }
            case null, default -> {}
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        Player player = ((Player) livingEntity);

        switch (getPower(stack)) {
            case 1 -> player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), DEFAULT_COOLDOWN);
            default -> player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), POWERLESS_COOLDOWN);
        }

    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        if (stack.get(ModDataComponents.PAN_FLUTE_LIST) == null || stack.get(ModDataComponents.PAN_FLUTE_LIST).isEmpty()) return 0;
        return switch (getPower(stack)) {
            case 1 -> 25;
            case null, default -> 0;
        };
    }

    private static SoundEvent getFluteSound(Player player, List<Integer> powers, Integer currentSound, SoundEvent fluteSound) {
        return switch (powers.get(currentSound)) {
            case 1 -> ModSounds.PAN_FLUTE_NOTES.get();
            case 2 -> ModSounds.PAN_FLUTE_PUSH.get();
            case 3 -> ModSounds.PAN_FLUTE_HEALTH.get();
            case 4 -> ModSounds.PAN_FLUTE_WIND.get();
            case 5 -> ModSounds.PAN_FLUTE_NATURE.get();
            default -> ModSounds.PAN_FLUTE_USE.get();
        };
    }

    public static Integer getPower(ItemStack stack) {
        List<Integer> powers = stack.get(ModDataComponents.PAN_FLUTE_LIST);
        Integer currentSound = stack.get(ModDataComponents.PAN_FLUTE_SOUND);
        return powers == null || currentSound == null ? null : powers.get(currentSound);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        List<Integer> powers = stack.get(ModDataComponents.PAN_FLUTE_LIST);
        Integer sound = stack.get(ModDataComponents.PAN_FLUTE_SOUND);

        if (powers != null && sound != null) {
            for (int i = 0; i < powers.size(); i++) {
                ChatFormatting color = ChatFormatting.GRAY;
                if (sound == i) {
                    color = ChatFormatting.GOLD;
                }

                tooltipComponents.add(
                        Component.translatable("item.faunaandorchestra.pan_flute." + powersString.get(powers.get(i)-1))
                                .withStyle(color));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
