package net.migueel26.faunaandorchestra.util;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BufferUtil {
    public static void writeUUIDList(FriendlyByteBuf buf, List<UUID> musicians) {
        buf.writeInt(musicians.size());
        for (UUID musician : musicians) {
            buf.writeUUID(musician);
        }
    }

    public static List<UUID> readUUIDList(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<UUID> musicians = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            musicians.add(buf.readUUID());
        }
        return musicians;
    }
}
