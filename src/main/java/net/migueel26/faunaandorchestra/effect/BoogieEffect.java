package net.migueel26.faunaandorchestra.effect;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

public class BoogieEffect extends MobEffect {
    private int tick;
    protected BoogieEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof Player) {

            float i = (float) ((float) 8*asymmetricSine(tick*0.1, 0.5, 6, 4.0));
            livingEntity.setXRot(livingEntity.getXRot() + i);

        } else if (livingEntity instanceof PathfinderMob mob){
            float i = (float) ((float) 100 * asymmetricSine(tick*0.2, 0.5, 6));

            mob.setXRot(i - 40);
            mob.setNoAi(true);
        }

        tick++;
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public static double asymmetricSine(double tick, double up, double down) {
        // Periodo = PI
        double t = tick % Math.PI;
        if (t < 0) t += Math.PI;

        double y;
        if (t < Math.PI / 2.0) {

            double u = t / (Math.PI / 2.0);
            double angleUp = (Math.PI / 2.0) * Math.pow(u, up);
            y = Math.sin(angleUp);
        } else {

            double s = (t - Math.PI / 2.0) / (Math.PI / 2.0);
            double angleDown = (Math.PI / 2.0) + (Math.PI / 2.0) * (1 - Math.pow(1 - s, down));
            y = Math.sin(angleDown);
        }

        return y;
    }

    public static double asymmetricSine(double x, double p, double r, double frequency) {
        // scale input by frequency
        double t = (x * frequency) % (2 * Math.PI);
        if (t < 0) t += 2 * Math.PI;

        double y;
        if (t < Math.PI) {
            if (t < Math.PI / 2.0) {
                double u = t / (Math.PI / 2.0);
                double angleUp = (Math.PI / 2.0) * Math.pow(u, p);
                y = Math.sin(angleUp);
            } else {
                double s = (t - Math.PI / 2.0) / (Math.PI / 2.0);
                double angleDown = (Math.PI / 2.0) + (Math.PI / 2.0) * (1 - Math.pow(1 - s, r));
                y = Math.sin(angleDown);
            }
        } else {
            double t2 = t - Math.PI;
            if (t2 < Math.PI / 2.0) {
                double u = t2 / (Math.PI / 2.0);
                double angleUp = (Math.PI / 2.0) * Math.pow(u, p);
                y = -Math.sin(angleUp);
            } else {
                double s = (t2 - Math.PI / 2.0) / (Math.PI / 2.0);
                double angleDown = (Math.PI / 2.0) + (Math.PI / 2.0) * (1 - Math.pow(1 - s, r));
                y = -Math.sin(angleDown);
            }
        }
        return y;
    }
}
