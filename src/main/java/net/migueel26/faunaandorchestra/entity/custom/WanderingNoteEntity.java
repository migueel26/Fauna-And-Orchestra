package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class WanderingNoteEntity extends AmbientCreature {
    public int scheduleDeath = -1;
    private static final EntityDataAccessor<Integer> TEXTURE_INDEX =
            SynchedEntityData.defineId(WanderingNoteEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(WanderingNoteEntity.class, EntityDataSerializers.INT);
    @Nullable
    private BlockPos targetPosition;
    public WanderingNoteEntity(EntityType<? extends AmbientCreature> p_27403_, Level p_27404_) {
        super(p_27403_, p_27404_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE_INDEX, this.random.nextInt(8));
        builder.define(LIFETIME, 0);
    }

    @Override
    public void tick() {
        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        increaseLifetime();

        if (getLifetime() == 255 || scheduleDeath == 0) {
            this.discard();
        }

        if (scheduleDeath > 0) {
            scheduleDeath--;
        }
        super.tick();
    }

    private void increaseLifetime() {
        this.entityData.set(LIFETIME, getLifetime()+1);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void push(Entity entity) {

    }

    @Override
    protected void pushEntities() {

    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.BUTTERFLY_NET)) {
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CLOUD,
                        getX(), getY(), getZ(),
                        5, 0, 0, 0, 0.05);
            }
            level().playSound(null, blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 1.0f, 0.75f);
            if (hand.equals(InteractionHand.MAIN_HAND)) {
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            } else {
                stack.hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
            }
            player.addItem(new ItemStack(ModItems.WANDERING_NOTE.get(), 1));
            this.scheduleDeath = 3;
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        BlockPos blockpos = this.blockPosition();
        if (this.targetPosition != null
                && (!this.level().isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level().getMinBuildHeight())) {
            this.targetPosition = null;
        }

        if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0)) {
            this.targetPosition = BlockPos.containing(
                    this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7),
                    this.getY() + (double)this.random.nextInt(6) - 2.0,
                    this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7)
            );
        }

        double d2 = (double)this.targetPosition.getX() + 0.5 - this.getX();
        double d0 = (double)this.targetPosition.getY() + 0.1 - this.getY();
        double d1 = (double)this.targetPosition.getZ() + 0.5 - this.getZ();
        Vec3 vec3 = this.getDeltaMovement();
        Vec3 vec31 = vec3.add((Math.signum(d2) * 0.5 - vec3.x) * 0.1F, (Math.signum(d0) * 0.7F - vec3.y) * 0.1F, (Math.signum(d1) * 0.5 - vec3.z) * 0.1F);
        this.setDeltaMovement(vec31);
        float f = (float)(Mth.atan2(vec31.z, vec31.x) * 180.0F / (float)Math.PI) - 90.0F;
        float f1 = Mth.wrapDegrees(f - this.getYRot());
        this.zza = 0.5F;
        this.setYRot(this.getYRot() + f1);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public int getTextureIndex() {
        return this.entityData.get(TEXTURE_INDEX);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setTextureIndex(int index) {
        this.entityData.set(TEXTURE_INDEX, index);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("TextureIndex")) {
            this.setTextureIndex(tag.getInt("TextureIndex"));
        }
        if (tag.contains("Lifetime")) {
            this.entityData.set(LIFETIME, tag.getInt("Lifetime"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("TextureIndex", this.getTextureIndex());
        tag.putInt("Lifetime", this.entityData.get(LIFETIME));
    }
}
