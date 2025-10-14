package net.migueel26.faunaandorchestra.particles.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nullable;

public class StarParticle extends TextureSheetParticle {
    private final float rSpeed;
    protected StarParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.lifetime = 50;
        this.setSpriteFromAge(spriteSet);
        this.gravity = 0.5f;
        this.friction = 0.95f;
        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

        this.roll = level.random.nextFloat() * ((float)Math.PI * 2f); // random start angle
        this.oRoll = this.roll;

        this.rSpeed = (level.random.nextFloat() - 0.5f) * 0.6f;
    }

    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;
        this.roll += this.rSpeed;

        if (this.age >= 10) {
            this.alpha -= 0.05F;
        }
        if (this.alpha <= 0) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
                                       double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            StarParticle star = new StarParticle(clientLevel, pX, pY, pZ, this.spriteSet, pXSpeed, pYSpeed, pZSpeed);
            star.pickSprite(this.spriteSet);
            return star;
        }
    }
}
