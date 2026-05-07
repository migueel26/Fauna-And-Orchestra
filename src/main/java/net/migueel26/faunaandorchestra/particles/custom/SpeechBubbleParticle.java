package net.migueel26.faunaandorchestra.particles.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class SpeechBubbleParticle extends TextureSheetParticle {
    private final ItemStack itemStack;
    private final TextureAtlasSprite bubbleSprite;

    protected SpeechBubbleParticle(ClientLevel level, double x, double y, double z, ItemParticleOption options) {
        super(level, x, y, z);
        this.itemStack = options.getItem();
        this.lifetime = 60;
        this.quadSize = 0.3f;
        this.hasPhysics = false;

        this.xd = 0;
        this.yd = 0f;
        this.zd = 0;

        // Cargamos la textura del bocadillo desde el Atlas de Bloques
        this.bubbleSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "item/speech_bubble"));

        this.setSprite(this.bubbleSprite);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        super.render(buffer, camera, partialTicks);

        TextureAtlasSprite itemSprite = getItemSprite();
        this.setSprite(itemSprite);
        this.quadSize = 0.15f;
        this.setColor(0.0f, 0.0f, 0.0f);

        this.y += 0.05f;
        this.yo += 0.05f;

        super.render(buffer, camera, partialTicks);

        this.setSprite(this.bubbleSprite);
        this.quadSize = 0.3f;
        this.setColor(1.0f, 1.0f, 1.0f);
        this.y -= 0.05f;
        this.yo -= 0.05f;
    }

    private TextureAtlasSprite getItemSprite() {
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this.itemStack.getItem());
        
        TextureAtlasSprite inventorySprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(itemId.withPrefix("item/"));

        if (!inventorySprite.contents().name().getPath().contains("missingno")) {
            return inventorySprite;
        }

        return Minecraft.getInstance().getItemRenderer()
                .getModel(this.itemStack, this.level, null, 0)
                .getParticleIcon();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    public static class Provider implements ParticleProvider<ItemParticleOption> {
        public Provider(SpriteSet sprites) {}

        @Override
        public Particle createParticle(ItemParticleOption type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new SpeechBubbleParticle(level, x, y, z, type);
        }
    }
}