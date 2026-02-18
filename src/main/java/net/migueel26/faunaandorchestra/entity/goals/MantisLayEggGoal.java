package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.MantisEggBlock;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.migueel26.faunaandorchestra.entity.custom.variants.MantisVariant;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class MantisLayEggGoal extends MoveToBlockGoal {
    private final MantisEntity mantis;
    private MantisEntity partner;
    private boolean hasToKill;
    private boolean isPartnerMusical;
    private int partnerVariant;

    public MantisLayEggGoal(MantisEntity mantis, double speedModifier) {
        super(mantis, speedModifier, 16);
        this.mantis = mantis;
    }

    public boolean canUse() {
        return this.mantis.hasEgg() && super.canUse();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.mantis.hasEgg();
    }

    @Override
    public void start() {
        hasToKill = mantis.getRandom().nextFloat() > 0.2f;
        partner = mantis.level().getNearestEntity(MantisEntity.class, TargetingConditions.DEFAULT, mantis, mantis.getX(), mantis.getY(), mantis.getZ(), mantis.getBoundingBox().inflate(16));
        if (partner == null) {
            // In case the entity disappeared
            super.stop();
            return;
        }
        isPartnerMusical = partner.isMusical();
        partnerVariant = partner.getVariantId();
        if (!hasToKill) {
            super.start();
        }
    }

    public void tick() {
        if (hasToKill && partner != null) {
            mantis.lookAt(EntityAnchorArgument.Anchor.FEET, partner.position());
            mantis.attack();
            partner.kill();
            mantis.level().playSound(null, mantis.blockPosition(), ModSounds.MANTIS_ANGRY.get(), SoundSource.NEUTRAL, 1.0f, 1.5f);

            hasToKill = false;
            super.start();
        }

        super.tick();
        BlockPos blockpos = this.mantis.blockPosition();
        if (!this.mantis.isInWater() && this.isReachedTarget()) {
            if (this.mantis.layEggCounter < 1) {
                this.mantis.setLayingEgg(true);
            } else if (this.mantis.layEggCounter > this.adjustedTickDelay(100)) {
                Level level = this.mantis.level();
                level.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                BlockPos blockpos1 = this.blockPos.above();
                BlockState blockstate = ModBlocks.MANTIS_EGG.get().defaultBlockState()
                        .setValue(MantisEggBlock.EGGS, this.mantis.getRandom().nextInt(4) + 1)
                        .setValue(MantisEggBlock.MOTHER_MUSICAL, this.mantis.isMusical())
                        .setValue(MantisEggBlock.FATHER_MUSICAL, isPartnerMusical)
                        .setValue(MantisEggBlock.MOTHER_VARIANT, this.mantis.getVariantId())
                        .setValue(MantisEggBlock.FATHER_VARIANT, partnerVariant);
                level.setBlock(blockpos1, blockstate, 3);
                level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.mantis, blockstate));
                this.mantis.setHasEgg(false);
                this.mantis.setLayingEgg(false);
                this.mantis.setInLoveTime(600);
            }

            if (this.mantis.isLayingEgg()) {
                ++this.mantis.layEggCounter;
            }
        }

    }

    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return !level.isEmptyBlock(pos.above()) ? false : MantisEggBlock.isLand(level, pos);
    }
}
