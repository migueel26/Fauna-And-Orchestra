package net.migueel26.faunaandorchestra.entity.custom.projectile;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.CrawlingDiscordBlock;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThrownDiscordBomb extends ThrowableItemProjectile implements ItemSupplier {
    public ThrownDiscordBomb(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownDiscordBomb(Level level, LivingEntity shooter) {
        super(ModEntities.THROWN_DISCORD_BOMB.get(), shooter, level);
    }

    public ThrownDiscordBomb(Level level, double x, double y, double z) {
        super(ModEntities.THROWN_DISCORD_BOMB.get(), x, y, z, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.BOOGIE_BOMB.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            Vec3 vec3 = result.getLocation();
            level().playSound(null, vec3.x, vec3.y, vec3.z, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.NEUTRAL, 1.0f, 1.0f);
            level().playSound(null, vec3.x, vec3.y, vec3.z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 1.0f, 0.5f);
            ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, vec3.x, vec3.y, vec3.z, 50, 0.25, 0.25, 0.25, 0.1);
            ((ServerLevel) level()).sendParticles(ParticleTypes.SCULK_SOUL, vec3.x, vec3.y, vec3.z, 80, 0.75, 0.75, 0.75, 0.2);
            level().setBlock(BlockPos.containing(vec3.x, vec3.y, vec3.z), ModBlocks.CRAWLING_DISCORD.get().defaultBlockState().setValue(CrawlingDiscordBlock.MAX_GENERATION, 7), 3);

            this.discard();
        }
    }
}
