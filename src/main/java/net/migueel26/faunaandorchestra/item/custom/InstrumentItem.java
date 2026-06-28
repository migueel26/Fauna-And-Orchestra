package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.entity.custom.WiseTree;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

import java.util.List;


public class InstrumentItem extends Item {
    private final SoundEvent SOUND;

    public InstrumentItem(Properties properties, SoundEvent sound) {
        super(properties);
        this.SOUND = sound;
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

            // Try to Tame OR Wake Up
            List<Entity> entities = level.getEntities(player, player.getBoundingBox().inflate(10));
            for (Entity entity : entities) {
                if (entity instanceof MusicalEntity musicalEntity && musicalEntity.isMusical() && !musicalEntity.isTame()
                && musicalEntity.getInstrument().get().equals(this)) {
                    // If MusicalEntity
                    musicalEntity.tryToTame(player);

                } else if (entity instanceof WanderingKoalaEntity koala && koala.isKoalaSleeping()) {
                    // If Sleeping Koala
                    koala.wakeUp();

                } else if (entity instanceof WiseTree wiseTree) {
                    // If Wise Tree
                    wiseTree.tryToWater(this);
                }
            }

            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip." + stack.getItem()));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
    
    private HitResult calculateHitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(
                player, entity -> !entity.isSpectator() && entity.isPickable(), player.getAttributeValue(ForgeMod.ENTITY_REACH.get())
        );
    }
    
    public SoundEvent getSound() {
        return SOUND;
    }
}
