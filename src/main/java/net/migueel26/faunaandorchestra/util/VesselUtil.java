package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VesselUtil {
    public static int LIFETIME = 100;
    public static final List<? extends EntityType<?>> APT_ENTITIES = List.of(
            EntityType.SNOW_GOLEM,
            EntityType.PARROT,
            EntityType.PHANTOM,
            ModEntities.MACAW.get(),
            EntityType.VEX,
            EntityType.WITCH,
            EntityType.EVOKER,
            EntityType.LLAMA,
            EntityType.MAGMA_CUBE,
            EntityType.BLAZE,
            EntityType.PIGLIN_BRUTE,
            EntityType.GOAT,
            EntityType.IRON_GOLEM,
            EntityType.RABBIT,
            EntityType.MOOSHROOM,
            EntityType.CAVE_SPIDER,
            EntityType.DOLPHIN,
            EntityType.HOGLIN,
            EntityType.STRIDER,
            EntityType.SNIFFER,
            EntityType.PILLAGER,
            ModEntities.MANTIS.get()
    );

    // MAX 8 PER SOUND
    public static final Map<Map<? extends EntityType<?>, Integer>, Integer> SOUNDS = Map.of(
            Map.of(EntityType.LLAMA, 3,
                    EntityType.MAGMA_CUBE, 2,
                    EntityType.BLAZE, 1,
                    EntityType.PIGLIN_BRUTE, 1), 1,
            Map.of(EntityType.GOAT,2,
                    EntityType.IRON_GOLEM, 2,
                    EntityType.SNOW_GOLEM, 1), 2,
            Map.of(EntityType.VEX,4,
                    EntityType.WITCH, 1,
                    EntityType.EVOKER, 1), 3,
            Map.of(EntityType.RABBIT, 2,
                    ModEntities.MACAW.get(), 1,
                    EntityType.PARROT, 2,
                    EntityType.PHANTOM,1), 4,
            Map.of(EntityType.MOOSHROOM,1,
                    EntityType.CAVE_SPIDER, 1,
                    EntityType.DOLPHIN, 1,
                    EntityType.HOGLIN, 1,
                    EntityType.STRIDER, 1,
                    EntityType.SNIFFER, 1,
                    EntityType.PILLAGER, 1,
                    ModEntities.MANTIS.get(), 1), 5
    );

    public static void setVoiceName(ItemStack stack, String name) {
        stack.getOrCreateTag().putString("VoiceName", name);
    }

    public static String getVoiceName(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("VoiceName")) {
            return stack.getTag().getString("VoiceName");
        }
        return null;
    }

    public static ItemStack voiceOfEntity(EntityType<? extends Entity> entityType) {
        ItemStack stack = new ItemStack(ModItems.VOICE.get());

        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        if (entityId != null) {
            setVoiceName(stack, entityId.toString());
        }

        stack.setHoverName(Component.translatable("item.faunaandorchestra.voice")
                .append(Component.translatable(entityType.getDescriptionId())));

        return stack;
    }

    public static List<ItemStack> getAllVoiceItems() {
        return APT_ENTITIES.stream().map(VesselUtil::voiceOfEntity).toList();
    }

    public static boolean isEntityAptForVessel(Entity entity) {
        return APT_ENTITIES.contains(entity.getType());
    };
}
