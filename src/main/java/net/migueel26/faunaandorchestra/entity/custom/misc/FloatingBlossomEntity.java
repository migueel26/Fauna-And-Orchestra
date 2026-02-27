package net.migueel26.faunaandorchestra.entity.custom.misc;

import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.neoforged.neoforge.common.Tags;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class FloatingBlossomEntity extends Entity implements GeoEntity {
    public final int MAX_LIFETIME = 520;
    private final int DIAMETER = 13;
    private List<BlockPos> posList;
    protected final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected final RawAnimation DIE = RawAnimation.begin().thenPlay("die");
    protected final RawAnimation INIT = RawAnimation.begin().thenPlay("init");
    public static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(FloatingBlossomEntity.class, EntityDataSerializers.INT);
    private final AnimationController<FloatingBlossomEntity> controller = new AnimationController<>(this, "blossom_controller", 0, this::blossomState)
            .triggerableAnim("init", INIT)
            .triggerableAnim("die", DIE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FloatingBlossomEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(LIFETIME, MAX_LIFETIME);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("Lifetime")) {
            this.entityData.set(LIFETIME, compoundTag.getInt("Lifetime"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Lifetime", getLifetime());
    }

    private <E extends GeoAnimatable> PlayState blossomState(AnimationState<E> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        if (!this.isRemoved()) {
            this.pushEntities();
        }

        if (getLifetime() == MAX_LIFETIME) {
            triggerAnim("blossom_controller", "init");
            initList();
        } else if (getLifetime() == 40) {
            triggerAnim("blossom_controller", "die");
        } else if (getLifetime() == 0) {
            this.discard();
        }

        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel && !this.isRemoved()) {
            if (getLifetime() % 10 == 0) {
                serverLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, getX(), getY(), getZ(), 50, 3.0f, 2.0f, 3.0f, 0.01f);
            }

            if (getLifetime() <= MAX_LIFETIME - 40 && getLifetime() % 10 == 0 && posList != null && !posList.isEmpty()) {
                int tries;
                var registry = level().registryAccess().registryOrThrow(Registries.BLOCK);

                List<Block> tallFlowers = registry.getTag(BlockTags.TALL_FLOWERS)
                        .map(namedTag -> namedTag.stream().map(Holder::value).toList())
                        .orElse(List.of());

                List<Block> smallFlowers = registry.getTag(BlockTags.SMALL_FLOWERS)
                        .map(namedTag -> namedTag.stream().map(Holder::value).toList())
                        .orElse(List.of());

                List<Block> flowers = new ArrayList<>(tallFlowers);
                flowers.addAll(smallFlowers);
                flowers.remove(Blocks.WITHER_ROSE);

                for (int i = 0; i <= 1; i++) {
                    Block flower = flowers.get(random.nextInt(flowers.size()));
                    BlockPos.MutableBlockPos posToPlace = posList.remove(random.nextInt(posList.size())).mutable();

                    tries = 0;

                    while (!level().getBlockState(posToPlace).isEmpty() && tries <= DIAMETER) {
                        posToPlace.move(0, 1, 0);
                        tries++;
                    }

                    while (!(level().getBlockState(posToPlace.below()).is(BlockTags.DIRT) && level().getBlockState(posToPlace).isEmpty()) && tries <= DIAMETER) {
                        posToPlace.move(0, -1, 0);
                        tries++;
                    }

                    if (tries <= DIAMETER) {
                        if (flower instanceof DoublePlantBlock) {
                            DoublePlantBlock.placeAt(level(), flower.defaultBlockState(), posToPlace, 3);
                        } else {
                            level().setBlock(posToPlace, flower.defaultBlockState(), 3);
                        }
                        level().playSound(null, posToPlace, SoundEvents.FLOWERING_AZALEA_PLACE, SoundSource.BLOCKS);
                    }
                }
            }
        }

        decreaseLifetime();

        super.tick();
    }

    private void initList() {
        posList = new ArrayList<>();
        for (int i = 0; i < DIAMETER; i++) {
            for (int j = 0; j < DIAMETER; j++) {
                if (Math.sqrt(Math.pow(i - 6, 2) + Math.pow(j - 6, 2)) <= 6) {
                    BlockPos pos = new BlockPos((int) (getX() + i - 6), (int) getY(), (int) (getZ() + j - 6));
                    posList.add(pos);
                }
            }
        }

        posList.remove(blockPosition());
    }

    protected void pushEntities() {
        if (this.level().isClientSide()) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::doPush);
        } else {
            List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
                int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
                if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                    int j = 0;

                    for (Entity entity : list) {
                        if (!entity.isPassenger()) {
                            ++j;
                        }
                    }
                }

                for (Entity entity1 : list) {
                    this.doPush(entity1);
                }
            }
        }
    }

    protected void doPush(Entity entity) {
        entity.push(this);
    }

    public int getLifetime() {
        return entityData.get(LIFETIME);
    }

    public void decreaseLifetime() {
        entityData.set(LIFETIME, getLifetime() - 1);
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
