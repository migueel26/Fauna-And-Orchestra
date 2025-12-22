package net.migueel26.faunaandorchestra.entity.custom;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public abstract class AbstractCanonEntity extends TamableAnimal implements GeoEntity {
    // Client Only
    protected ResourceLocation skin;
    protected AbstractCanonEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.skin = null;
    }

    public void setSkin(ResourceLocation skinLocation) {
        ResourceLocation convertedSkin = null;

        if (skinLocation != null) {
            Minecraft mc = Minecraft.getInstance();
            NativeImage master = null;

            try {
                Optional<Resource> resource = mc.getResourceManager().getResource(skinLocation);
                InputStream stream;

                if (resource.isPresent()) {
                    stream = resource.get().open();
                } else {
                    ResourceLocation defaultLoc = DefaultPlayerSkin.getDefaultSkin(mc.getUser().getGameProfile().getId());
                    stream = mc.getResourceManager().getResource(defaultLoc).get().open();
                }

                master = NativeImage.read(stream);
                stream.close();

                NativeImage gray = new NativeImage(master.getWidth(), master.getHeight(), true);

                for (int y = 0; y < master.getHeight(); y++) {
                    for (int x = 0; x < master.getWidth(); x++) {
                        int pixel = master.getPixelRGBA(x, y);

                        int a = (pixel >> 24) & 0xFF;
                        int b = (pixel >> 16) & 0xFF;
                        int g = (pixel >> 8) & 0xFF;
                        int r = (pixel & 0xFF);

                        if (a == 0) {
                            gray.setPixelRGBA(x, y, 0);
                            continue;
                        }

                        int avg = (r + g + b) / 3;

                        int grayPixel = FastColor.ABGR32.color(a, avg, avg, avg);

                        gray.setPixelRGBA(x, y, grayPixel);
                    }
                }

                master.close();

                if (gray != null) {
                    DynamicTexture dynTex = new DynamicTexture(gray);
                    String dynamicId = "ghost_" + skinLocation.getPath().replace('/', '_');

                    convertedSkin = mc.getTextureManager().register(dynamicId, dynTex);
                }

            } catch (IOException e) {
                e.printStackTrace();
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
