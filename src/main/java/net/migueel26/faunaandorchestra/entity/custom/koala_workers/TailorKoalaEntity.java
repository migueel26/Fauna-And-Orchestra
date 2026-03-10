package net.migueel26.faunaandorchestra.entity.custom.koala_workers;

import net.migueel26.faunaandorchestra.entity.custom.TalkableEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class TailorKoalaEntity extends AgeableMob implements Npc, TalkableEntity, GeoEntity {
    protected TailorKoalaEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public ResourceLocation getIcon() {
        return null;
    }

    @Override
    public String getRandomDialogue(Player player) {
        return "";
    }

    @Override
    public Pair<Integer, Integer> getIconSize() {
        return null;
    }

    @Override
    public Pair<Integer, Integer> getIconLocation() {
        return null;
    }

    @Override
    public int getDialogueTimer() {
        return 0;
    }

    @Override
    public void increaseDialogueTimer() {

    }

    @Override
    public void resetDialogueTimer() {

    }

    @Override
    public void setGoodMorning(boolean goodMorning) {

    }

    @Override
    public boolean getGoodMorning() {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }
}
