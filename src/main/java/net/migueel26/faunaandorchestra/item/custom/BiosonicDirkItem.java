package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class BiosonicDirkItem extends Item {
    public BiosonicDirkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        HitResult hitResult = calculateHitResult(player);
        ItemStack stack = player.getItemInHand(usedHand);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity && livingEntity.isAlive() && !(livingEntity instanceof ArmorStand)) {
                if (!level.isClientSide()) {
                    if (livingEntity.getRandom().nextFloat() < 0.25F) {
                        livingEntity.hurt(player.damageSources().playerAttack(player), 4.5f);
                        ModEntities.LIVING_MUSIC.get().spawn((ServerLevel) level, livingEntity.blockPosition(), MobSpawnType.TRIGGERED);
                        level.playSound(null, livingEntity.blockPosition(), ModSounds.MAGIC_GROWTH.get(), player.getSoundSource(), 1.0f, 1.0f);
                    }
                    stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
                    player.getCooldowns().addCooldown(this, 20);
                }
                return InteractionResultHolder.success(stack);
            }
        }
        return super.use(level, player, usedHand);
    }

    private HitResult calculateHitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(
                player, entity -> !entity.isSpectator() && entity.isPickable(), player.blockInteractionRange()
        );
    }
}
