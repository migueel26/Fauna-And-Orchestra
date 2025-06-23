package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.migueel26.faunaandorchestra.networking.StartFrogChoirMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.StopMusicS2CPayload;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

public class QuirkyFrogConductingChoirGoal extends Goal {
    private QuirkyFrogEntity conductor;
    private boolean startSinging;
    private int tick;
    private int times;
    private LinkedList<QuirkyFrogEntity> choir;
    public QuirkyFrogConductingChoirGoal(QuirkyFrogEntity quirkyFrog) {
        this.conductor = quirkyFrog;
    }

    @Override
    public boolean canUse() {
        return !conductor.isTame() && conductor.isReady() && !conductor.isSinging() && conductor.getFrogConductor() == null
                && !conductor.isDeadOrDying() && !conductor.getFrogChoir().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return !conductor.isDeadOrDying() && !conductor.isTame() && conductor.getFrogChoir().size() == 4
                && conductor.isInSittingPose();
    }

    @Override
    public void start() {
        this.startSinging = false;
        this.conductor.setInSittingPose(true);
        this.tick = 50;
        for (QuirkyFrogEntity chorister : conductor.getFrogChoir()) {
            chorister.setFrogConductor(conductor);
        }
        this.times = 0;
        this.choir = new LinkedList<>(conductor.getFrogChoir());
    }

    @Override
    public void stop() {
        for (QuirkyFrogEntity chorister : conductor.getFrogChoir()) {
            chorister.setSinging(false);
            chorister.setFrogConductor(null);
            chorister.setReady(false);

            Vec3 vec3 = this.getPosition();
            if (vec3 != null) chorister.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.conductor.getSpeed());
        }

        PacketDistributor.sendToAllPlayers(new StopMusicS2CPayload(conductor.getUUID()));

        this.startSinging = false;
        this.conductor.setInSittingPose(false);
        conductor.setMusical(false);
        conductor.setReady(false);
        conductor.setConducting(false);
        conductor.setFrogChoir(Collections.emptyList());

        Vec3 vec3 = this.getPosition();
        if (vec3 != null) this.conductor.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.conductor.getSpeed());
        super.stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return super.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        if (!startSinging) {

            // Before the frogs start singing we have to ensure all of them are in position
            if (tick == 0) {
                // We check all frogs are in position
                boolean allReady = true;
                Iterator<QuirkyFrogEntity> iterator = conductor.getFrogChoir().iterator();
                while (allReady && iterator.hasNext()) {
                    allReady = allReady && iterator.next().isReady();
                }

                if (allReady) {
                    this.startSinging = true;
                    this.tick = 0;
                } else {
                    this.tick = 50;
                }

            } else {
                tick--;
            }

        } else {
            if (!conductor.isConducting()) {
                conductor.setConducting(true);
                PacketDistributor.sendToAllPlayers(new StartFrogChoirMusicS2CPayload(conductor.getUUID()));
                conductor.setMusical(true);
            } else {
                croacIfRightTick(tick);
            }

            if (tick >= 99) {
                times++;
                tick = 20;
            }

            tick++;
        }

        if (times == 8 ||
                conductor.isTame() ||
                this.conductor.level().getEntitiesOfClass(
                Player.class, this.conductor.getBoundingBox().inflate(45.0, 45.0, 45.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE).isEmpty()) {
            this.stop();
        }

        super.tick();
    }

    private void croacIfRightTick(int tick) {
        // CRO - CRO - CRO () CRO - CRO - CRO
        if (tick == 22 || tick == 24 || tick == 26 || tick == 32 || tick == 34 || tick == 36 ||
        // CRO - CRO - CRO - CRO - CRÓ
            tick == 42 || tick == 44 || tick == 47 || tick == 49 || tick == 52 ||
        // CRO - CRO - CRO - cro - crooo
            tick == 59 || tick == 62 || tick == 65 || tick == 68 || tick == 71 ||
        // CRO - CRO - CRO - croooo
            tick == 82 || tick == 85 || tick ==  88 || tick == 91) {
            QuirkyFrogEntity chorister = choir.pollFirst();
            if (chorister == null) {
                this.stop();
            } else {
                croac(chorister);
                choir.addLast(chorister);
                conductor.getLookControl().setLookAt(chorister);
            }
        }
    }

    private void croac(QuirkyFrogEntity chorister) {
        chorister.triggerAnim("quirky_frog_croac_controller", "croac");
        ((ServerLevel) chorister.level()).sendParticles(ParticleTypes.NOTE,
                chorister.getX(), chorister.getY() + 1.5F, chorister.getZ(),
                1, 0, 0, 0, 1);
    }

    @Nullable
    protected Vec3 getPosition() {
        return DefaultRandomPos.getPos(this.conductor, 10, 7);
    }
}
