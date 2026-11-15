package net.migueel26.faunaandorchestra.entity.custom.village_trotters;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.TravellingMusician;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.goals.CorneliusFindVillageTrottersGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Cornelius extends TravellingMusician implements GeoEntity {
    // ANIMATION
    private static final RawAnimation CONDUCTING = RawAnimation.begin().thenPlay("conducting");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("holding_baton");
    protected final AnimationController<Cornelius> corneliusController = new AnimationController<>(this, "cornelius_controller", 5, this::corneliusState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // TALKABLE
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/cornelius_icon.png");
    public static final String RESOURCE = "dialogue.faunaandorchestra.cornelius";
    public String currentDialogue;

    // MUSIC
    protected List<Player> playersListening = new ArrayList<>();
    private int nearbyPlayersSearchDelay = 0;

    // VILLAGE TROTTERS
    private final UUID[] villageTrotters = new UUID[VillageTrotterEntity.VillageTrotter.values().length];

    public Cornelius(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.setCustomName(Component.translatable("entity.faunaandorchestra.cornelius"));
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CorneliusFindVillageTrottersGoal(this));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        for (int i = 0; i < villageTrotters.length; i++) {
            villageTrotters[i] = compound.hasUUID("VillageTrotter" + i) ? compound.getUUID("VillageTrotter" + i) : null;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        for (int i = 0; i < villageTrotters.length; i++) {
            if (villageTrotters[i] != null) {
                compound.putUUID("VillageTrotter" + i, villageTrotters[i]);
            }
        }
    }

    public void setVillageTrotter(VillageTrotterEntity.VillageTrotter type, @Nullable UUID uuid) {
        villageTrotters[type.getId()] = uuid;
    }

    @Nullable
    public UUID getVillageTrotter(VillageTrotterEntity.VillageTrotter type) {
        return villageTrotters[type.getId()];
    }

    public UUID[] getVillageTrotters() {
        return villageTrotters;
    }

    public boolean hasVillageTrotters() {
        return Arrays.stream(villageTrotters).anyMatch(Objects::nonNull);
    }

    private <E extends GeoAnimatable> PlayState corneliusState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else if (isPlaying()) {
            state.getController().setAnimation(CONDUCTING);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        this.discard();
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setConfidence(int confidence) {

    }

    @Override
    public int getConfidence() {
        return 0;
    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    public String getRandomDialogue(Player player) {
        return "";
    }

    @Override
    public void setGoodMorning(boolean goodMorning) {

    }

    @Override
    public boolean getGoodMorning() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(corneliusController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
