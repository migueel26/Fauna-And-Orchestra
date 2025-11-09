package net.migueel26.faunaandorchestra.entity.custom;

import com.mojang.blaze3d.platform.NativeImage;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import vazkii.patchouli.api.PatchouliAPI;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AnyaGhost extends AbstractCanonEntity implements GeoEntity {
    protected UUID playerUUID;
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID = SynchedEntityData.defineId(AnyaGhost.class, EntityDataSerializers.OPTIONAL_UUID);
    private final AnimationController<AnyaGhost> anyaController = new AnimationController<>(this, "anya_ghost_controller", 5, this::anyaState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);


    public AnyaGhost(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.skin = null;
        this.playerUUID = null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PLAYER_UUID, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(PLAYER_UUID)) {
            playerUUID = entityData.get(PLAYER_UUID).orElse(null);
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putUUID("PlayerUUID", playerUUID);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("PlayerUUID")) {
            entityData.set(PLAYER_UUID, Optional.of(compound.getUUID("PlayerUUID")));
        }
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    private <E extends GeoAnimatable> PlayState anyaState(AnimationState<E> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) {
            this.setSkin(((AbstractClientPlayer) player).getSkin());
        }
        return InteractionResult.SUCCESS;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        if (tickCount >= 40 && playerUUID != null) {
            Player player = level().getNearestPlayer(this, 7.0);
            if (player != null) {
                this.setPlayerUUID(player.getUUID());
                if (level().isClientSide()) {
                    this.setSkin(((AbstractClientPlayer) player).getSkin());
                }

            }
        }

        if (level().isClientSide() && skin == null && playerUUID != null) {
            if (level().getPlayerByUUID(playerUUID) instanceof Player player) {
                this.setSkin(((AbstractClientPlayer) player).getSkin());
                this.setCustomName(player.getDisplayName());
            }
        }

        if (!level().isClientSide()) {
            if (tickCount == 350) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.POOF, position().x, position().y+0.5f, position().z, 40, 0.2, 0.5, 0.2, 0.3);
                ItemEntity itemEntity = new ItemEntity(level(), position().x, position().y+0.5f, position().z,
                        PatchouliAPI.get().getBookStack(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "symphonia")));
                itemEntity.addDeltaMovement(new Vec3(0, 0.3, 0));
                level().addFreshEntity(itemEntity);
            } else if (tickCount == 356) {
                this.discard();
            }
        }


    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.entityData.set(PLAYER_UUID, Optional.of(playerUUID));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(anyaController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }
}
