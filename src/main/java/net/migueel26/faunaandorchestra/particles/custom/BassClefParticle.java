package net.migueel26.faunaandorchestra.particles.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nullable;

public class BassClefParticle extends TextureSheetParticle {
    public static int LIFETIME = 100;
    protected BassClefParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z);
        this.setSize(3.0f, 3.0f);
        this.scale(3.30f);
        this.lifetime = LIFETIME;
        this.friction = 0.8f;
        this.xd = 0;
        this.yd = 0.15f;
        this.zd = 0;
        this.age = 0;

        this.alpha = 0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (this.age <= 20 && alpha < 1) this.alpha += 0.1f;
        else if (this.age >= 60 && alpha > 0) this.alpha -= 0.05f;
        else if (this.alpha <= 0) this.remove();

        age++;

        super.tick();
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
            BassClefParticle voice = new BassClefParticle(clientLevel, pX, pY, pZ, this.spriteSet, pXSpeed, pYSpeed, pZSpeed);
            voice.pickSprite(this.spriteSet);
            return voice;
        }
    }
}
