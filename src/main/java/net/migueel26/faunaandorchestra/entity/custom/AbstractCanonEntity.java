package net.migueel26.faunaandorchestra.entity.custom;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class AbstractCanonEntity extends TamableAnimal implements GeoEntity {
    // Client Only
    protected ResourceLocation skin;
    protected AbstractCanonEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.skin = null;
    }

    public void setSkin(PlayerSkin skin) {
        ResourceLocation convertedSkin = null;
        if (skin != null) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            ResourceLocation baseSkin = skin.texture();

            NativeImage gray = null;
            BufferedImage master;

            try {
                if (resourceManager.getResource(baseSkin).isPresent()) {
                    master = ImageIO.read(resourceManager.getResource(baseSkin).get().open());
                } else {
                    master = ImageIO.read(resourceManager.getResource(DefaultPlayerSkin.getDefaultTexture()).get().open());
                }

                gray = new NativeImage(master.getWidth(), master.getHeight(), true);

                int rgb = 0, r = 0, g = 0, b = 0, a = 0;
                for (int y = 0; y < master.getHeight(); y++) {
                    for (int x = 0; x < master.getWidth(); x++) {
                        rgb = (int) (master.getRGB(x, y));
                        a = ((rgb >> 24) & 0xFF);
                        r = ((rgb >> 16) & 0xFF);
                        g = ((rgb >> 8) & 0xFF);
                        b = (rgb & 0xFF);

                        if (a == 0) continue;

                        rgb = (int) ((r + g + b) / 3);
                        rgb = (255 << 24) | (rgb << 16) | (rgb << 8) | rgb;

                        gray.setPixelRGBA(x, y, rgb);
                    }
                }


            } catch (IOException ignored) {
            }

            if (gray != null) {
                DynamicTexture dynTex = new DynamicTexture(gray);

                convertedSkin = Minecraft.getInstance()
                        .getTextureManager()
                        .register("anya_ghost", dynTex);

            }
        }

        this.skin = convertedSkin;
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
