package net.migueel26.faunaandorchestra.particles.custom;

import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class DrippingMusicParticle {
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double pXSpeed, double pYSpeed, double pZSpeed) {
            FallAndLandParticle dripparticle = new FallAndLandParticle(level, x, y, z, Fluids.EMPTY, ModParticleTypes.DRIPPING_MUSIC.get());
            dripparticle.setColor(0.8235F, 0.6863F, 1.0F);
            dripparticle.pickSprite(this.spriteSet);
            return dripparticle;
        }
    }
    @OnlyIn(Dist.CLIENT)
    static class FallAndLandParticle extends FallingParticle {
        protected final ParticleOptions landParticle;

        FallAndLandParticle(ClientLevel level, double x, double y, double z, Fluid type, ParticleOptions landParticle) {
            super(level, x, y, z, type);
            this.landParticle = landParticle;
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class FallingParticle extends TextureSheetParticle {
        private final Fluid type;
        FallingParticle(ClientLevel level, double x, double y, double z, Fluid type) {
            this(level, x, y, z, type, (int)(64.0 / (Math.random() * 0.8 + 0.2)));
        }

        FallingParticle(ClientLevel level, double x, double y, double z, Fluid type, int lifetime) {
            super(level, x, y, z);
            this.lifetime = lifetime;
            this.setSize(0.01F, 0.01F);
            this.gravity = 0.06F;
            this.type = type;
        }

        protected Fluid getType() {
            return this.type;
        }

        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
        }

        public void tick() {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.preMoveUpdate();
            if (!this.removed) {
                this.yd -= (double)this.gravity;
                this.move(this.xd, this.yd, this.zd);
                this.postMoveUpdate();
                if (!this.removed) {
                    this.xd *= (double)0.98F;
                    this.yd *= (double)0.98F;
                    this.zd *= (double)0.98F;
                    if (this.type != Fluids.EMPTY) {
                        BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
                        FluidState fluidstate = this.level.getFluidState(blockpos);
                        if (fluidstate.getType() == this.type && this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
                            this.remove();
                        }

                    }
                }
            }
        }

        protected void preMoveUpdate() {
            if (this.lifetime-- <= 0) {
                this.remove();
            }

        }

        protected void postMoveUpdate() {
        }
    }
}
