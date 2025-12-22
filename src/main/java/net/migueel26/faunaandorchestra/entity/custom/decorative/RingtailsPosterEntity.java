package net.migueel26.faunaandorchestra.entity.custom.decorative;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class RingtailsPosterEntity extends Painting {
    public RingtailsPosterEntity(EntityType<? extends Painting> entityType, Level level) {
        super(entityType, level);
    }

    public RingtailsPosterEntity(Level level, BlockPos pos, Direction direction, Holder<PaintingVariant> variant) {
        super(level, pos, direction, variant);
    }

    public void dropItem(@Nullable Entity brokenEntity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof Player) {
                Player player = (Player)brokenEntity;
                if (player.isCreative()) {
                    return;
                }
            }

            this.spawnAtLocation(ModItems.RINGTAILS_POSTER.get());
        }

    }
}
