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

import java.util.List;

public class VesselUtil {
    public static final List<EntityType> APT_ENTITIES = List.of(
            EntityType.BREEZE,
            EntityType.PIG,
            ModEntities.MACAW.get()
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

    public static boolean isEntityAptForVessel(Entity entity) {
        return APT_ENTITIES.contains(entity.getType());
    };
}
