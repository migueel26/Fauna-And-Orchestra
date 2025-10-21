package net.migueel26.faunaandorchestra.util;

import com.google.common.base.Predicate;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlayerUtil {
    public static <T extends Entity> List<T> entitiesInFrontOf(
            Entity entity,
            double range,
            Level level,
            Class<T> entityClazz,
            @Nullable Predicate<? super T> entityFilter) {

        double halfWidth = 1.0;
        double coneAngleDeg = 60.0;
        double minDot = Math.cos(Math.toRadians(coneAngleDeg));

        List<T> targets = new ArrayList<>();
        Vec3 look = entity.getLookAngle().normalize();
        Vec3 pos = entity.position();
        Vec3 end = pos.add(look.scale(range));

        // Build a bounding box that covers from pos to end and inflate it slightly to include entity sizes
        AABB aabb = new AABB(
                Math.min(pos.x, end.x) - halfWidth,
                Math.min(pos.y, end.y) - halfWidth,
                Math.min(pos.z, end.z) - halfWidth,
                Math.max(pos.x, end.x) + halfWidth,
                Math.max(pos.y, end.y) + halfWidth,
                Math.max(pos.z, end.z) + halfWidth
        );


        Predicate<? super T> filter = entityFilter == null ? (t -> true) : entityFilter;

        double rangeSq = range * range;

        for (T candidate : level.getEntitiesOfClass(entityClazz, aabb, filter)) {
            if (candidate == entity) continue; // skip self

            Vec3 to = candidate.position().subtract(pos);
            double distSq = to.lengthSqr();
            if (distSq > rangeSq) continue; // out of range

            Vec3 toNorm = to.normalize();
            double dot = look.dot(toNorm);
            if (dot < minDot) continue; // outside cone

            targets.add(candidate);
        }

        return targets;
    }

    public static void spawnParticlesFromEntityTo(SimpleParticleType particleType, ServerLevel level, Entity startEntity, Entity endEntity, float startOffset, float endOffset) {
        Vec3 start = startEntity.position().add(0, startEntity.getEyeHeight() + startOffset, 0); // from eyes
        Vec3 end = endEntity.position().add(0, endEntity.getBbHeight() / 2 + endOffset, 0); // to middle of entity
        Vec3 dir = end.subtract(start);
        double distance = dir.length();
        dir = dir.normalize();

        // Step every 0.5 blocks, add cloud trail
        for (double i = 0; i < distance; i += 0.1) {
            double pOffset = 0.05f;
            Vec3 pos = start.add(dir.scale(i));
            ((ServerLevel) level).sendParticles(
                    particleType,
                    pos.x, pos.y, pos.z,
                    2, // count
                    pOffset, pOffset, pOffset, // spread
                    0.01f // speed
            );
        }
    }

    public static void spawnParticlesFromTo(SimpleParticleType particleType, int count, ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 dir = end.subtract(start);
        double distance = dir.length();
        dir = dir.normalize();

        // Step every 0.5 blocks, add cloud trail
        for (double i = 0; i < distance; i += 0.1) {
            double pOffset = 0.05f;
            Vec3 pos = start.add(dir.scale(i));
            ((ServerLevel) level).sendParticles(
                    particleType,
                    pos.x, pos.y, pos.z,
                    count, // count
                    pOffset, pOffset, pOffset, // spread
                    0.01f // speed
            );
        }
    }
}
