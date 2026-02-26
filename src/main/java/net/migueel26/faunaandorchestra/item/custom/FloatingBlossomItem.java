package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.client.entity.FloatingBlossomItemRenderer;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.misc.FloatingBlossomEntity;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Supplier;

public class FloatingBlossomItem extends AbstractGeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FloatingBlossomItem(Properties properties) {
        super(properties, FloatingBlossomItemRenderer::new);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide()) {
            Vec3 spawnPos = getBlossomPosition(level, player);
            FloatingBlossomEntity floatingBlossom = ModEntities.FLOATING_BLOSSOM.get().create(level);
            if (floatingBlossom != null) {
                floatingBlossom.moveTo(spawnPos);
                level.addFreshEntity(floatingBlossom);

                level.playSound(null, BlockPos.containing(spawnPos), ModSounds.WOW.get(), SoundSource.AMBIENT);
                ((ServerLevel) level).sendParticles(ModParticleTypes.REGULAR_NOTE.get(), spawnPos.x, spawnPos.y, spawnPos.z, 10, 0.1, 0.1, 0.1, 0.1);

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }
        }

        return InteractionResultHolder.consume(stack);
    }

    private static @NotNull Vec3 getBlossomPosition(Level level, Player player) {
        Vec3 spawnPos = player.getEyePosition().add(player.getViewVector(0.0F).scale(player.blockInteractionRange()));
        HitResult hitresult = level.clip(new ClipContext(player.getEyePosition(), spawnPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hitresult.getType() != HitResult.Type.MISS) {
            spawnPos = hitresult.getLocation();
        }
        return spawnPos;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
