package net.migueel26.faunaandorchestra.entity.custom.misc;

import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class MailbirdMacawEntity extends MacawEntity {
    private boolean isFlyingAway = false;
    private int flyAwayTicks = 0;
    private static final int MAX_FLY_AWAY_TICKS = 100;
    public MailbirdMacawEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public RegistryObject<Item> getInstrument() {
        return ModItems.ICON;
    }

    @Override
    protected void registerGoals() {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.isFlyingAway = compound.getBoolean("isFlyingAway");
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("isFlyingAway", isFlyingAway);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.isFlyingAway) {
            this.flyAwayTicks++;

            if (this.flyAwayTicks >= MAX_FLY_AWAY_TICKS) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 0.5D, this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.1f);
                this.discard();
            }
        }
    }

    public void flyAway() {
        this.isFlyingAway = true;
        this.flyAwayTicks = 0;

        Vec3 flightTarget = findFlightTarget();

        this.getNavigation().moveTo(flightTarget.x, flightTarget.y, flightTarget.z, 1.5D);
    }

    private Vec3 findFlightTarget() {
        BlockPos currentPos = this.blockPosition();

        for (int i = 0; i < 10; i++) {
            int dx = this.random.nextInt(17) - 8;
            int dy = this.random.nextInt(8) + 8;
            int dz = this.random.nextInt(17) - 8;

            BlockPos targetPos = currentPos.offset(dx, dy, dz);

            if (this.level().isEmptyBlock(targetPos)) {
                return Vec3.atCenterOf(targetPos);
            }
        }

        return Vec3.atCenterOf(currentPos.above(15));
    }
}
