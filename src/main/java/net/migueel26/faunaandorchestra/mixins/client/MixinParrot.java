package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Parrot.class)
public class MixinParrot extends Animal {
    @Shadow BlockPos jukebox;

    protected MixinParrot(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
            )
    )
    private boolean redirectIsJukebox(BlockState state, Block block) {
        return !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX) || this.level().getEntitiesOfClass(ConductorEntity.class,
                getBoundingBox().inflate(5.0D)).stream().filter(ConductorEntity::isConducting).findAny().isEmpty();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}
