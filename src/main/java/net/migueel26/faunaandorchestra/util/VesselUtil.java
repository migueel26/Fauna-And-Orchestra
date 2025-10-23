package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VesselUtil {
    public static final List<? extends EntityType<?>> APT_ENTITIES = List.of(
            EntityType.BREEZE,
            EntityType.PARROT,
            EntityType.PHANTOM,
            ModEntities.MACAW.get()
    );

    // MAX 8 PER SOUND
    public static final Map<Map<? extends EntityType<?>, Integer>, Integer> SOUNDS = Map.of(
            Map.of(EntityType.HUSK,1), 1,
            Map.of(EntityType.HUSK,2), 2,
            Map.of(EntityType.HUSK,3), 3,
            Map.of(EntityType.BREEZE, 2,
                    ModEntities.MACAW.get(), 1,
                    EntityType.PARROT, 2,
                    EntityType.PHANTOM,1), 4,
            Map.of(EntityType.HUSK,4), 5
    );

    public static ItemStack voiceOfEntity(EntityType<? extends Entity> entityType) {
        ItemStack stack = new ItemStack(ModItems.VOICE.get());

        stack.applyComponents(DataComponentPatch.builder()
                .set(ModDataComponents.FAUNA_NAME.get(), BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString())
                .set(DataComponents.ITEM_NAME, Component.translatable("item.faunaandorchestra.voice")
                        .append(Component.translatable(entityType.getDescriptionId())))
                .build());

        return stack;
    }

    public static List<ItemStack> getAllVoiceItems() {
        return APT_ENTITIES.stream().map(VesselUtil::voiceOfEntity).toList();
    }

    public static boolean isEntityAptForVessel(Entity entity) {
        return APT_ENTITIES.contains(entity.getType());
    };
}
