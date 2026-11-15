package net.migueel26.faunaandorchestra.entity.custom;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class AbstractCanonEntity extends TamableAnimal implements GeoEntity {
    protected ResourceLocation skin;
    protected AbstractCanonEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.skin = null;
    }

    // The original (coloured) skin the current grey texture was generated from
    protected ResourceLocation skinSource;

    public void setSkin(PlayerSkin skin) {
        if (skin == null) {
            releaseSkin();
            return;
        }

        ResourceLocation baseSkin = skin.texture();
        // Don't regenerate the texture if the skin hasn't changed
        if (this.skin != null && baseSkin.equals(skinSource)) return;

        // Downloaded skins aren't in the ResourceManager, they only exist as GPU textures, so we read the pixels back from the texture itself
        NativeImage gray = readTexture(baseSkin);
        if (gray == null) gray = readTexture(DefaultPlayerSkin.getDefaultTexture());
        if (gray == null) return;

        for (int y = 0; y < gray.getHeight(); y++) {
            for (int x = 0; x < gray.getWidth(); x++) {
                int pixel = gray.getPixelRGBA(x, y);
                int a = (pixel >>> 24) & 0xFF;
                if (a == 0) continue;

                int c1 = (pixel >> 16) & 0xFF;
                int c2 = (pixel >> 8) & 0xFF;
                int c3 = pixel & 0xFF;
                int avg = (c1 + c2 + c3) / 3;

                gray.setPixelRGBA(x, y, (255 << 24) | (avg << 16) | (avg << 8) | avg);
            }
        }

        releaseSkin();
        this.skin = Minecraft.getInstance().getTextureManager().register("anya_ghost", new DynamicTexture(gray));
        this.skinSource = baseSkin;
    }

    @Nullable
    private static NativeImage readTexture(ResourceLocation location) {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(location);
        if (texture == null || texture.getId() <= 0) return null;

        texture.bind();
        int width = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        if (width <= 0 || height <= 0) return null;

        NativeImage image = new NativeImage(width, height, false);
        image.downloadTexture(0, false);
        return image;
    }

    private void releaseSkin() {
        if (this.skin != null) {
            Minecraft.getInstance().getTextureManager().release(this.skin);
        }
        this.skin = null;
        this.skinSource = null;
    }

    @Override
    public void onClientRemoval() {
        releaseSkin();
        super.onClientRemoval();
    }

    public ResourceLocation getSkin() {
        return skin;
    }

    @NotNull
    private static String getFolder(String path) {
        String[] pathL = path.split("/");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pathL.length - 1; i++) {
            builder.append(pathL[i]);
            builder.append("/");
        }
        path = builder.toString();
        return path;
    }
}
