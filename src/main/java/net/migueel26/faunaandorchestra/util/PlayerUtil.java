package net.migueel26.faunaandorchestra.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}
