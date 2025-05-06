package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class QuirkyFrogConductingChoirGoal extends Goal {
    private QuirkyFrogEntity conductor;
    private boolean startSinging;
    private int tick;
    private int lookCooldown;
    private LinkedList<QuirkyFrogEntity> choir;
    public QuirkyFrogConductingChoirGoal(QuirkyFrogEntity quirkyFrog) {
        this.conductor = quirkyFrog;
    }

    @Override
    public boolean canUse() {
        return !conductor.isTame() && conductor.isReady() && !conductor.isSinging() && conductor.getFrogConductor() == null
                && !conductor.isDeadOrDying();
    }

    @Override
    public boolean canContinueToUse() {
        return !conductor.isDeadOrDying() && !conductor.isTame() && conductor.getFrogChoir().size() == 4;
    }

    @Override
    public void start() {
        this.startSinging = false;
        this.tick = 50;
        this.lookCooldown = 0;
        for (QuirkyFrogEntity chorister : conductor.getFrogChoir()) {
            chorister.setFrogConductor(conductor);

            System.out.println("IN!");
        }

        this.choir = new LinkedList<>(conductor.getFrogChoir());
    }

    @Override
    public void stop() {
        for (QuirkyFrogEntity chorister : conductor.getFrogChoir()) {
            chorister.setSinging(false);
            chorister.setFrogConductor(null);
        }

        conductor.setReady(false);
        conductor.setConducting(false);
        conductor.setFrogChoir(Collections.emptyList());
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
                /*conductor.level().playSound(null,
                        conductor.getX(), conductor.getY(), conductor.getZ(),

                        );*/
            } else {
                if (tick % 30 == 0) {
                    QuirkyFrogEntity chorister = choir.pollFirst();
                    croac(chorister);
                    choir.addLast(chorister);
                }
            }

            tick++;
        }

        if (lookCooldown <= 0) {
            conductor.getLookControl().setLookAt(getCentroid());
            lookCooldown = 20;
        } else {
            lookCooldown--;
        }

        super.tick();
    }

    private void croac(QuirkyFrogEntity chorister) {
        if (chorister == null) this.stop();
        else chorister.triggerAnim("quirky_frog_controller", "croac");
    }

    private Vec3 getCentroid() {
        if (!conductor.getFrogChoir().isEmpty()) {
            List<QuirkyFrogEntity> orchestra = conductor.getFrogChoir();
            double n = orchestra.size();

            return new Vec3(
                    orchestra.stream().map(Entity::getX).reduce(0.0, Double::sum)/n,
                    conductor.getY(),
                    orchestra.stream().map(Entity::getZ).reduce(0.0, Double::sum)/n);
        } else {
            return new Vec3(0.0,0.0,0.0);
        }
    }
}
