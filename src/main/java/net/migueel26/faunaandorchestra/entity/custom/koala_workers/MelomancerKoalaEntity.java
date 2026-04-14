package net.migueel26.faunaandorchestra.entity.custom.koala_workers;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.screen.custom.MelomancerMenu;
import net.migueel26.faunaandorchestra.screen.custom.TailorMenu;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MelomancerKoalaEntity extends AbstractKoalaWorker {
    private static final RawAnimation SLEEP = RawAnimation.begin().thenPlay("sleep");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation MIX = RawAnimation.begin().thenPlay("mix");
    private static final RawAnimation EAT = RawAnimation.begin().thenPlay("eat");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    AnimationController<MelomancerKoalaEntity> controller = new AnimationController<>(this, "melomancer_koala_controller", 5, this::koalaState)
            .triggerableAnim("eat", EAT);
    // TALKABLE ENTITY
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/melomancer_koala_icon.png");
    public static final String RESOURCE = "dialogue.faunaandorchestra.melomancer_koala";
    // MIXING
    public static final int MAX_WORK_TIME = 100;//1200;
    public static final int START_PAUSE = 20;//600;
    public static final int END_PAUSE = 80;//900;
    public static final int EAT_TIME = (START_PAUSE + END_PAUSE) / 3;
    protected static final EntityDataAccessor<Boolean> MIXING = SynchedEntityData.defineId(MelomancerKoalaEntity.class, EntityDataSerializers.BOOLEAN);
    public ItemStackHandler inventory = new ItemStackHandler(9);

    public MelomancerKoalaEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MIXING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5f) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((MelomancerKoalaEntity) mob).isKoalaSleeping();
            }
        });
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0f) {
            final MelomancerKoalaEntity koala = (MelomancerKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && !koala.isKoalaSleeping() && (!koala.isMixing() || koala.isInLunchBreak());
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0f) {
            final MelomancerKoalaEntity koala = (MelomancerKoalaEntity) mob;
            @Override
            public boolean canUse() {
                return super.canUse() && !koala.isKoalaSleeping() && (!koala.hasWorkingStation() || koala.isInLunchBreak());
            }
        });
        this.goalSelector.addGoal(5, new FaunaRandomLookAroundGoal(this));
    }

    private <E extends GeoAnimatable> PlayState koalaState(AnimationState<E> state) {
        if (isKoalaSleeping() && hasWorkingStation()) {
            state.getController().setAnimation(SLEEP);
        } else if (isMixing() && !isInLunchBreak()) {
            state.getController().setAnimation(MIX);
        } else if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(ModItems.BATON) && stack.get(ModDataComponents.MUSICIAN_UUID) == null) {
            // We link the tailor to the baton
            stack.set(ModDataComponents.MUSICIAN_UUID, this.uuid);
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.WAX_OFF, getX(), getY() + 0.5f, getZ(), 20, 0.2, 0.2, 0.2, 0.05);
            }
            return InteractionResult.SUCCESS;

        } else if (isKoalaSleeping()) {
            player.displayClientMessage(Component.translatable("text.faunaandorchestra.sleeping_worker_koala"), true);
            return InteractionResult.SUCCESS;

        } else if (hasWorkingStation() && !isMixing()) {
            // We open the inventory
            this.openCustomMenu(player);
            return InteractionResult.SUCCESS;

        } else if (getDialogueTimer() == 0) {
            // The koala talks
            if (level().isClientSide()) {
                increaseDialogueTimer();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private void openCustomMenu(Player player) {
        if (!this.level().isClientSide()) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) ->
                    new MelomancerMenu(id, playerInventory, this), this.getDisplayName()), buf -> {
                buf.writeUUID(getUUID());
            });
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(MIXING, compound.getBoolean("Mixing"));
        if (compound.contains("Inventory")) {
            this.inventory.deserializeNBT(this.registryAccess(), compound.getCompound("Inventory"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Inventory", this.inventory.serializeNBT(this.registryAccess()));
        compound.putBoolean("Mixing", isMixing());
    }

    public boolean isMixing() {
        return entityData.get(MIXING);
    }

    public void setMixing(boolean mixing) {
        entityData.set(MIXING, mixing);
    }

    @Override
    public boolean isInLunchBreak() {
        return workTime >= START_PAUSE && workTime <= END_PAUSE;
    }

    @Override
    public boolean isWorking() {
        return isMixing();
    }

    @Override
    public boolean isWorkingStation(BlockState state) {
        return state.is(Tags.Blocks.CHESTS);
    }

    @Override
    public boolean isListening() {
        return !isKoalaSleeping();
    }

    @Override
    public void onStartListening(ConductorEntity conductor) {
        setKoalaSleeping(false);
    }

    @Override
    public void onStopListening() {
        setKoalaSleeping(true);
    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    public String getRandomDialogue(Player player) {
        // NOTE: % is the koala's name
        String dialogue = currentDialogue;
        boolean goodMorning = entityData.get(GOOD_MORNING);
        if (getDialogueTimer() <= 5) {
            if (isMixing()) {
                if (isInLunchBreak()) {
                    if (goodMorning) {
                        // Hey boss
                        dialogue = Component.translatable(RESOURCE + "2").getString();
                    } else {
                        int randomDialogue = random.nextIntBetweenInclusive(3, 20);
                        dialogue = Component.translatable(RESOURCE + randomDialogue).getString();
                    }
                } else {
                    // I'm working, don't bother me
                    dialogue = Component.translatable(RESOURCE + "1").getString();
                }
            } else {
                // Guide me to the table
                dialogue = Component.translatable(RESOURCE + "0").getString();
            }
            currentDialogue = dialogue;
        }
        return dialogue;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
