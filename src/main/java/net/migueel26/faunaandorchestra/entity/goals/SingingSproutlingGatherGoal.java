package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.SproutlingEntity;
import net.migueel26.faunaandorchestra.entity.custom.WiseTree;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;

public class SingingSproutlingGatherGoal extends Goal {
    private SproutlingEntity director;
    int index;

    protected int tick;
    public SingingSproutlingGatherGoal(SproutlingEntity director) {
        this.director = director;
    }
    @Override
    public boolean canUse() {
        return director.getDirSproutlings() != null && director.getDirCentroid() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && !director.isDeadOrDying();
    }

    @Override
    public void start() {
        tick = 0;
        index = 0;
        director.getDirSproutlings().forEach(sproutling -> sproutling.setSinging(true));
        super.start();
    }

    @Override
    public void tick() {
        if (tick >= 140 && tick <= 198) {
            SproutlingEntity chorister = director.getDirSproutlings().get(index < 6 ? index : 5);
            // FIRST
            if (tick == 140) {
                sing(chorister);
                index++;
            } else if (tick == (140 + 10)){
                // LA CANCION EMPIEZA EN 150
                // POR CADA NOTA EL BICHO TIENE QUE CANTAR 15 TICKS ANTES
                if (!director.level().isClientSide()) {
                    director.level().playSound(null, director.blockPosition(), ModSounds.SPROUTLING_SONG.get(), SoundSource.NEUTRAL);
                }
            }

            if (tick == 150) {
                sing(chorister);
                index++;
            }

            if (tick == 157) {
                sing(chorister);
                index++;
            }

            if (tick == 179) {
                sing(chorister);
                index++;
            }

            if (tick == 188) {
                sing(chorister);
                index++;
            }

            if (tick == 198) {
                sing(chorister);
                index++;
            }
        }

        // GRAND FINALE
        if (tick == 220) {
            Vec3 pos = director.getDirCentroid();
            List<SproutlingEntity> sproutlings = director.getDirSproutlingsMinusDir();
            sproutlings.forEach(sproutling -> sproutling.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.0f));
            director.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.0f);
        }

        if (tick == 280) {
            if (!director.level().isClientSide()) {
                Vec3 centroid = director.getDirCentroid();
                ((ServerLevel) director.level()).sendParticles(ParticleTypes.CLOUD, centroid.x, centroid.y, centroid.z,
                        100, 0.25, 0.25, 0.25, 0.3);
                director.level().playSound(null, BlockPos.containing(centroid), ModSounds.SUCCESSFUL_TAME.get(), SoundSource.NEUTRAL);
            }

            WiseTree wiseTree = new WiseTree(ModEntities.WISE_TREE.get(), director.level());
            wiseTree.setOwnerUUID(director.getDirOwnerUUID());
            wiseTree.setPos(director.getDirCentroid());
            wiseTree.setTame(true, false);
            Player player = director.level().getPlayerByUUID(director.getDirOwnerUUID());
            if (player != null && player.isAlive()) {
                wiseTree.lookAt(EntityAnchorArgument.Anchor.FEET, player.position());
                if (!director.level().isClientSide()) {
                    ModAdvancements.WISE_TREE.trigger((ServerPlayer) player);
                }
            }

            director.level().addFreshEntity(wiseTree);

            director.getDirSproutlingsMinusDir().forEach(Entity::discard);
            director.discard();
        }


        tick++;

        super.tick();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void sing(SproutlingEntity sproutling) {
        sproutling.triggerAnim("sproutling_controller", "sing_trigger");
        if (!sproutling.level().isClientSide()) {
            ((ServerLevel) sproutling.level()).sendParticles(ParticleTypes.NOTE, sproutling.getX(), sproutling.getY()+0.5, sproutling.getZ(),
                    1, 0, 0, 0, 0);
        }
    }
}
