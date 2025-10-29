package net.migueel26.faunaandorchestra.particles.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class SleepParticle extends TextureSheetParticle {

    public static final float SIZE = 0.25F;
    private final SpriteSet spriteSet;

    protected SleepParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.spriteSet = spriteSet;

        this.quadSize = 0.1F;

        this.setSize(SIZE, SIZE);
        this.lifetime = 100;
        this.gravity = 0F;
        this.alpha = 0F;

        this.yd = Math.abs(ySpeed);
        this.xd = 0;
        this.zd = 0;
        this.age = 0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.age++;

        this.y += this.yd;
        this.yd *= 0.98;

        if (this.quadSize < SIZE) {
            this.quadSize += 0.01F;
            if (this.quadSize > SIZE) this.quadSize = SIZE;
        }

        if (this.age < this.lifetime && this.alpha >= 0.0F) {
            if (this.age < 35 && alpha < 1F) {
                this.alpha += 0.05F;
            } else if (this.age >= 75 && alpha > 0F) {
                this.alpha -= 0.05F;
            } else if (this.age > 75) {
                this.remove();
            }
        } else {
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            SleepParticle particle = new SleepParticle(level, pX, pY, pZ, spriteSet, pXSpeed, pYSpeed, pZSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
