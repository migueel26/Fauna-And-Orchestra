package net.migueel26.faunaandorchestra.entity.goals;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public class AlertWhenAttackedGoal extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private static final int ALERT_RANGE_Y = 10;
    private int timestamp;
    private final Class<? extends Mob> mobAlerted;

    public AlertWhenAttackedGoal(PathfinderMob mob, Class<? extends Mob> mobAlerted) {
        super(mob, true);
        this.mobAlerted = mobAlerted;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if (i != this.timestamp && livingentity != null) {
            if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                if (mobAlerted.isAssignableFrom(livingentity.getClass())) {
                    return false;
                }

                return this.canAttack(livingentity, HURT_BY_TARGETING);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        this.alertOthers();

        super.start();
    }

    protected void alertOthers() {
        double d0 = this.getFollowDistance();
        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0, d0);
        List<? extends Mob> list = this.mob.level().getEntitiesOfClass(mobAlerted, aabb, EntitySelector.NO_SPECTATORS);
        Iterator iterator = list.iterator();

        while(true) {
            Mob mob;
            boolean flag;
            do {
                do {
                    do {
                        do {
                            do {
                                if (!iterator.hasNext()) {
                                    return;
                                }

                                mob = (Mob)iterator.next();
                            } while(this.mob == mob);
                        } while(mob.getTarget() != null);
                    } while(this.mob instanceof TamableAnimal && ((TamableAnimal)this.mob).getOwner() != ((TamableAnimal)mob).getOwner());
                } while(mob.isAlliedTo(this.mob.getLastHurtByMob()));

                flag = false;

            } while(flag);

            this.alertOther(mob, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(Mob mob, LivingEntity target) {
        mob.setTarget(target);
    }
}
