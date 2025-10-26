package net.migueel26.faunaandorchestra.entity.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import oshi.util.tuples.Pair;

public interface TalkableEntity {
    ResourceLocation getIcon();
    String getRandomDialogue(Player player);
    Pair<Integer, Integer> getIconSize();
    Pair<Integer, Integer> getIconLocation();
    int getDialogueTimer();
    void increaseDialogueTimer();
    void resetDialogueTimer();
    void setGoodMorning(boolean goodMorning);
    default int getTextBoxOffset() {
        return 0;
    }
    boolean getGoodMorning();
}
