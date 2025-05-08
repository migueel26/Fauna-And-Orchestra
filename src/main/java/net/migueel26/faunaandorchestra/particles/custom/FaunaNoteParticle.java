package net.migueel26.faunaandorchestra.particles.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class FaunaNoteParticle extends TextureSheetParticle {
    private final double initialX;
    private final double initialZ;
    private double amplitude;
    private double frequency;

    protected FaunaNoteParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.scale(4.0F);
        this.setSize(0.5F, 0.5F);
        this.lifetime = 160;
        this.initialX = x;
        this.initialZ = z;
        this.frequency = 0.1 + (double) (this.random.nextFloat() / 75.0F) * (this.random.nextBoolean() ? -1 : 1);
        this.amplitude = 0.5;
        this.gravity = 3.0E-6F;
        this.xd = xSpeed;
        this.yd = ySpeed + (double)(this.random.nextFloat() / 500.0F);
        this.zd = zSpeed;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.age++;
        this.amplitude += 0.015F;
        //this.Zamplitude += 0.015F;
        if (this.age < this.lifetime && this.alpha >= 0.0F) {
            //this.zd = this.zd + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
            //this.xd = this.xd + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
            this.yd = this.yd - (double)this.gravity;
            this.move(0, yd, 0);
            this.x = this.initialX + Math.sin(this.age * frequency) * amplitude;
            this.z = this.initialZ + Math.cos(this.age * frequency) * amplitude;
            if (this.age > this.lifetime - 60 && this.alpha > 0.015F) {
                this.alpha -= 0.015F;
            }
        } else {
            this.remove();
        }
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class NoteProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public NoteProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            FaunaNoteParticle faunaNoteParticle = new FaunaNoteParticle(level, pX, pY, pZ, spriteSet, pXSpeed, pYSpeed, pZSpeed);
            faunaNoteParticle.pickSprite(this.spriteSet);
            return faunaNoteParticle;
        }
    }

    public static class TrebleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        public TrebleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            FaunaNoteParticle faunaNoteParticle = new FaunaNoteParticle(level, pX, pY, pZ, spriteSet, pXSpeed, pYSpeed, pZSpeed);
            faunaNoteParticle.pickSprite(spriteSet);
            faunaNoteParticle.scale(1.25F);
            return faunaNoteParticle;
        }
    }
}
