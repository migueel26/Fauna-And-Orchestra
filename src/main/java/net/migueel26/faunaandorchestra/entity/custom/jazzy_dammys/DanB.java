package net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.entity.custom.TravellingMusician;
import net.migueel26.faunaandorchestra.entity.goals.DanBFindJazzyDammysGoal;
import net.migueel26.faunaandorchestra.entity.goals.JazzyDammysRunAwayGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.networking.StartAmbientMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.StopMusicS2CPayload;
import net.migueel26.faunaandorchestra.util.AdvancementUtil;
import net.migueel26.faunaandorchestra.util.ModSavedData;
import net.migueel26.faunaandorchestra.util.RecipesUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class DanB extends TravellingMusician implements Npc, GeoEntity {
    // ANIMATION
    private static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final String MYTHS_DATA_KEY = "faunaandorchestra.myths";
    private final AnimationController<DanB> jazzyDammyController = new AnimationController<>(this, "dan_b_controller", 5, this::jazzyDammyState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // TALKABLE
    protected static final EntityDataAccessor<Integer> CONFIDENCE = SynchedEntityData.defineId(DanB.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(DanB.class, EntityDataSerializers.BOOLEAN);
    public static final int COOL_CONFIDENCE = 35;
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/dan_b_icon.png");
    protected int confidence;
    public static final String RESOURCE = "dialogue.faunaandorchestra.dan_b";
    public String currentDialogue;

    // MYTHS
    protected static final EntityDataAccessor<Integer> CURRENT_MYTH = SynchedEntityData.defineId(DanB.class, EntityDataSerializers.INT);

    // MUSIC
    protected List<Player> playersListening = new ArrayList<>();
    private int nearbyPlayersSearchDelay = 0;
    protected Denise denise;
    protected Denzel denzel;
    protected Delroy delroy;

    public DanB(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.setCustomName(Component.translatable("entity.faunaandorchestra.dan_b"));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CONFIDENCE, 0);
        builder.define(GOOD_MORNING, true);
        builder.define(CURRENT_MYTH, -1);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new JazzyDammysRunAwayGoal(this, 2.0));
        this.goalSelector.addGoal(1, new DanBFindJazzyDammysGoal(this));
    }

    private <E extends GeoAnimatable> PlayState jazzyDammyState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else if (isPlaying()) {
            state.getController().setAnimation(PLAYING);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (getDialogueTimer() == 0) {

            if (level().isClientSide()) {
                increaseDialogueTimer();
            } else {
                activateMyth(player);

                this.confidence = ModSavedData.getConfidence((ServerLevel) level(), this, player.getUUID());
                setConfidence(confidence);
                if (this.confidence >= COOL_CONFIDENCE) {
                    //ModAdvancements.BEFRIEND_FAUST.get().trigger((ServerPlayer) player);
                }
                ModSavedData.saveConfidence((ServerLevel) level(), this, player.getUUID(), this.confidence + 1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private void activateMyth(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data = persistentData.getCompound(ServerPlayer.PERSISTED_NBT_TAG);
        int myths = data.getInt(MYTHS_DATA_KEY);

        boolean updatePersistentData = false;

        if (!AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "myths/dan_myth0") && AdvancementUtil.hasAdvancement(player, ResourceLocation.DEFAULT_NAMESPACE, "story/lava_bucket")) {
            ModAdvancements.DAN_MYTH0.get().trigger((ServerPlayer) player);
            setCurrentMyth(0);
            setGoodMorning(false);
            data.putInt(MYTHS_DATA_KEY, myths | 1);
            updatePersistentData = true;
        } else if (!AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "myths/dan_myth1") && AdvancementUtil.hasAdvancement(player, ResourceLocation.DEFAULT_NAMESPACE, "adventure/hero_of_the_village")) {
            ModAdvancements.DAN_MYTH1.get().trigger((ServerPlayer) player);
            setCurrentMyth(1);
            setGoodMorning(false);
            data.putInt(MYTHS_DATA_KEY, myths | 2);
            updatePersistentData = true;
        } else if (!AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "myths/dan_myth2") && AdvancementUtil.hasAdvancement(player, ResourceLocation.DEFAULT_NAMESPACE, "nether/explore_nether")) {
            ModAdvancements.DAN_MYTH2.get().trigger((ServerPlayer) player);
            setCurrentMyth(2);
            data.putInt(MYTHS_DATA_KEY, myths | 4);
            setGoodMorning(false);
            updatePersistentData = true;

            player.addItem(RecipesUtil.recipeOfItem(ModItems.FLORAL_BOOTS.get()));
        }

        if (updatePersistentData) {
            persistentData.put(ServerPlayer.PERSISTED_NBT_TAG, data);
        }

    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    public String getRandomDialogue(Player player) {
        // NOTE: % is the player's name
        // # is laugh and it's animated
        String dialogue = currentDialogue;
        boolean goodMorning = entityData.get(GOOD_MORNING);
        this.confidence = getConfidence();
        if (getDialogueTimer() <= 5) {
            if (getCurrentMyth() != -1) {
                dialogue = Component.translatable(RESOURCE + ".myth" + getCurrentMyth()).getString();
            } else if (confidence == -1) {
                dialogue = Component.translatable(RESOURCE + "2").getString();
                String[] arr = dialogue.split("%");
                dialogue = arr[0] + player.getDisplayName().getString() + arr[1];
            } else if (confidence == 0 && goodMorning) {
                dialogue = Component.translatable(RESOURCE + "0").getString();
            } else if (confidence > 0 && confidence <= COOL_CONFIDENCE && goodMorning) {
                dialogue = Component.translatable(RESOURCE + "1").getString();
                String[] arr = dialogue.split("%");
                dialogue = arr[0] + player.getDisplayName().getString() + arr[1];
            } else if (confidence > COOL_CONFIDENCE && goodMorning) {
                dialogue = Component.translatable(RESOURCE + "1s").getString();
                String[] arr = dialogue.split("%");
                dialogue = arr[0] + player.getDisplayName().getString() + arr[1];
            } else {
                int randomDialogue = random.nextIntBetweenInclusive(3, 20);
                dialogue = Component.translatable(RESOURCE + randomDialogue).getString();
                if (randomDialogue >= 16 && confidence > COOL_CONFIDENCE && randomDialogue < 20) dialogue = Component.translatable(RESOURCE + randomDialogue + "s").getString();
            }
            currentDialogue = dialogue;
        }
        return dialogue;
    }

    @Override
    public void tick() {
        if (isPlaying() && !level().isClientSide()) {
            if (nearbyPlayersSearchDelay < 60) {
                nearbyPlayersSearchDelay++;
                playersListening = new ArrayList<>();
            } else {
                List<Player> nearbyPlayers = this.level().getEntitiesOfClass(
                        Player.class, this.getBoundingBox().inflate(32.0, 32.0, 32.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

                List<Player> newPlayers = new ArrayList<>(nearbyPlayers);
                List<Player> exitPlayers = new ArrayList<>(playersListening);
                exitPlayers.removeAll(nearbyPlayers);
                newPlayers.removeAll(playersListening);

                for (Player player : newPlayers) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new StartAmbientMusicS2CPayload(this.uuid));
                    ModAdvancements.MEET_JAZZY_DAMMYS.get().trigger((ServerPlayer) player);
                }

                for (Player player : exitPlayers) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new StopMusicS2CPayload(this.uuid));
                }

                playersListening = nearbyPlayers;

                }

        } else {
            nearbyPlayersSearchDelay = 0;
            playersListening = new ArrayList<>();
        }

        super.tick();
    }

    @Override
    protected void tickDeath() {
        if (denise != null) denise.setDanB(null);
        if (denzel != null) denzel.setDanB(null);
        if (delroy != null) delroy.setDanB(null);
        super.tickDeath();
    }

    @Override
    public void setConfidence(int confidence) {
        entityData.set(CONFIDENCE, confidence);
    }

    @Override
    public int getConfidence() {
        return entityData.get(CONFIDENCE);
    }

    @Override
    public void setGoodMorning(boolean goodMorning) {
        entityData.set(GOOD_MORNING, goodMorning);
    }

    @Override
    public boolean getGoodMorning() {
        return entityData.get(GOOD_MORNING);
    }

    public Denise getDenise() {
        return denise;

    }
    public void setDenise(Denise denise) {
        this.denise = denise;
    }

    public Denzel getDenzel() { return denzel; }
    public void setDenzel(Denzel denzel) {
        this.denzel = denzel;
    }

    public Delroy getDelroy() {
        return delroy;
    }

    public void setDelroy(Delroy delroy) {
        this.delroy = delroy;
    }

    public void setCurrentMyth(int myth) {
        entityData.set(CURRENT_MYTH, myth);
    }

    public int getCurrentMyth() {
        return entityData.get(CURRENT_MYTH);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(jazzyDammyController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
