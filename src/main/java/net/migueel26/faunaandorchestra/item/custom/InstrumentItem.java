package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import org.checkerframework.checker.units.qual.A;

import java.util.List;


public class InstrumentItem extends Item {
    private final SoundEvent SOUND;
    private final EntityType MUSICAL_ANIMAL;
    public InstrumentItem(Properties properties, SoundEvent sound, EntityType mantis) {
        super(properties);
        this.SOUND = sound;
        this.MUSICAL_ANIMAL = mantis;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.calculateHitResult(player).getType() != HitResult.Type.ENTITY) {
            level.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SOUND, SoundSource.NEUTRAL,
                    0.5F,
                    0.5F + level.getRandom().nextFloat()
            );
            player.getCooldowns().addCooldown(this, 35);

            List<Entity> entities = level.getEntities(player, player.getBoundingBox().inflate(10));
            for (Entity entity : entities) {
                if (entity instanceof MusicalEntity musicalEntity && musicalEntity.isMusical() && !musicalEntity.isTame()) {
                    musicalEntity.tryToTame(player);
                }
            }

            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip." + stack.getItem()));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
    
    private HitResult calculateHitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(
                player, entity -> !entity.isSpectator() && entity.isPickable(), player.blockInteractionRange()
        );
    }
    
    public SoundEvent getSound() {
        return SOUND;
    }
}
