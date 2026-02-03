package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.ButlerKoalaEntity;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class KoalaServePlayerGoal extends Goal {
    public static final double SERVE_DISTANCE = 4;
    protected final ButlerKoalaEntity koala;
    @Nullable
    protected Entity lookAt;
    protected final float lookDistance;
    protected final float probability;
    private final boolean onlyHorizontal;
    protected final Class<? extends LivingEntity> lookAtType;
    protected final TargetingConditions lookAtContext;
    private int lookTime;

    public KoalaServePlayerGoal(ButlerKoalaEntity koala, Class<? extends LivingEntity> lookAtType, float lookDistance) {
        this(koala, lookAtType, lookDistance, 0.02F);
    }

    public KoalaServePlayerGoal(ButlerKoalaEntity koala, Class<? extends LivingEntity> lookAtType, float lookDistance, float probability) {
        this(koala, lookAtType, lookDistance, probability, false);
    }

    public KoalaServePlayerGoal(ButlerKoalaEntity koala, Class<? extends LivingEntity> lookAtType, float lookDistance, float probability, boolean onlyHorizontal) {
        this.koala = koala;
        this.lookAtType = lookAtType;
        this.lookDistance = lookDistance;
        this.probability = probability;
        this.onlyHorizontal = onlyHorizontal;
        this.setFlags(EnumSet.of(Flag.LOOK));
        if (lookAtType == Player.class) {
            this.lookAtContext = TargetingConditions.forNonCombat().range((double)lookDistance).selector((p_25531_) -> {
                return EntitySelector.notRiding(koala).test(p_25531_);
            });
        } else {
            this.lookAtContext = TargetingConditions.forNonCombat().range((double)lookDistance);
        }

    }

    public boolean canUse() {
        if (this.koala.getRandom().nextFloat() >= this.probability) {
            return false;
        } else {
            if (this.koala.getTarget() != null) {
                this.lookAt = this.koala.getTarget();
            }

            if (this.lookAtType == Player.class) {
                this.lookAt = this.koala.level().getNearestPlayer(this.lookAtContext, this.koala, this.koala.getX(), this.koala.getEyeY(), this.koala.getZ());
            } else {
                this.lookAt = this.koala.level().getNearestEntity(this.koala.level().getEntitiesOfClass(this.lookAtType, this.koala.getBoundingBox().inflate((double)this.lookDistance, 3.0, (double)this.lookDistance), (p_148124_) -> {
                    return true;
                }), this.lookAtContext, this.koala, this.koala.getX(), this.koala.getEyeY(), this.koala.getZ());
            }

            return this.lookAt != null;
        }
    }

    public boolean canContinueToUse() {
        if (!this.lookAt.isAlive()) {
            return false;
        } else {
            return this.koala.distanceToSqr(this.lookAt) <= (double) (this.lookDistance * this.lookDistance);
        }
    }

    public void start() {

    }

    public void stop() {
        this.lookAt = null;
        if (koala.isAlive()) this.koala.setServing(false);
    }

    public void tick() {
        if (this.lookAt.isAlive()) {
            double d0 = this.onlyHorizontal ? this.koala.getEyeY() : this.lookAt.getEyeY();
            this.koala.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());

            if (this.koala.distanceToSqr(this.lookAt) <= SERVE_DISTANCE) {
                if (!koala.isServing()) {
                    koala.triggerAnim("butler_koala_controller", "serve");
                    koala.playSound(ModSounds.KOALA_SERVE.get(), 2.0f, 1.0f);
                    koala.setServing(true);
                }
            } else if (koala.isServing()) {
                koala.setServing(false);
            }
        }

    }
}
