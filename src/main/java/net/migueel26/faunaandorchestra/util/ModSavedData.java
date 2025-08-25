package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.migueel26.faunaandorchestra.entity.custom.TalkableEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ModSavedData extends SavedData {
    public final Map<UUID, List<Entry>> ANIMAL_MAP = new HashMap<>();
    public static ModSavedData create() {
        return new ModSavedData();
    }

    public static ModSavedData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        ModSavedData data = ModSavedData.create();
        ListTag listTag = tag.getList("TalkingEntityList",10);
        for (int i = 0; i < listTag.size(); i++) {
            Entry entry = new Entry(listTag.getCompound(i));
            List<Entry> list = data.ANIMAL_MAP.getOrDefault(entry.playerUUID, new ArrayList<>());
            if (!list.isEmpty()) {
                ArrayList<Entry> newList = new ArrayList<>(list);
                newList.add(entry);
                data.ANIMAL_MAP.put(entry.playerUUID, newList);
            } else {
                data.ANIMAL_MAP.put(entry.playerUUID, List.of(entry));
            }
        }
        return data;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag listTag = new ListTag();
        for (List<Entry> entries : ANIMAL_MAP.values()) {
            for (Entry entry : entries) {
                listTag.add(entry.save(new CompoundTag()));
            }
        }
        tag.put("TalkingEntityList", listTag);
        return tag;
    }

    public static ModSavedData from(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new Factory<>(ModSavedData::create, ModSavedData::load), "talking_entity_data");
    }

    public static int getConfidence(ServerLevel level, TalkableEntity entity, UUID player) {
        int confidence = 0;
        Map<UUID, List<ModSavedData.Entry>> map = ModSavedData.from(level).ANIMAL_MAP;
        List<Entry> entryList = map.getOrDefault(player, null);

        String name = getTalkableEntityName(entity);

        if (entryList != null && name != null) {
            for (Entry entry : entryList) {
                if (name.equalsIgnoreCase(entry.talkingEntity)) {
                    confidence = entry.confidence;
                }
            }
        }

        return confidence;
    }

    public static void saveConfidence(ServerLevel level, TalkableEntity entity, UUID player, int confidence) {
        Map<UUID, List<ModSavedData.Entry>> map = ModSavedData.from(level).ANIMAL_MAP;
        List<Entry> entryList = map.getOrDefault(player, null);

        String name = getTalkableEntityName(entity);

        if (name != null) {
            if (entryList == null || entryList.isEmpty()) {
                map.put(player, List.of(new Entry(player, name, confidence)));
            } else {
                boolean found = false;
                for (Entry entry : entryList) {
                    if (entry.talkingEntity.equalsIgnoreCase(name)) {
                        entry.confidence = confidence;
                        found = true;
                    }
                }
                if (!found) {
                    ArrayList<Entry> newList = new ArrayList<>(entryList);
                    newList.add(new Entry(player, name, confidence));
                    map.put(player, newList);
                }
            }

            ModSavedData.from(level).setDirty();
        }

    }

    @Nullable
    private static String getTalkableEntityName(TalkableEntity entity) {
        String name = null;
        switch (entity) {
            case Faust faust -> name = "faust";
            case Orion orion -> name = "orion";
            case null, default -> {
            }
        }
        return name;
    }

    public static class Entry {
        public UUID playerUUID;
        public String talkingEntity;
        public int confidence;

        public Entry(UUID playerUUID, String talkingEntity, int confidence) {
            this.playerUUID = playerUUID;
            this.talkingEntity = talkingEntity;
            this.confidence = confidence;
        }

        public Entry(CompoundTag tag) {
            this(tag.getUUID("player"), tag.getString("animal"), tag.getInt("confidence"));
        }

        public CompoundTag save(CompoundTag tag) {
            tag.putUUID("player", playerUUID);
            tag.putString("animal", talkingEntity);
            tag.putInt("confidence", confidence);
            return tag;
        }
    }
}
