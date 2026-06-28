package net.migueel26.faunaandorchestra.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class CustomSpawnEggItem extends Item {
    private final List<Supplier<? extends EntityType<? extends AgeableMob>>> musicians;

    @SafeVarargs
    public CustomSpawnEggItem(Item.Properties properties, Supplier<? extends EntityType<? extends AgeableMob>>... musicians) {
        super(properties);
        this.musicians = Arrays.asList(musicians);
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            for (Supplier<? extends EntityType<? extends AgeableMob>> entityType : musicians) {
                entityType.get().spawn(
                        (ServerLevel) level,
                        itemstack,
                        context.getPlayer(),
                        blockpos1,
                        MobSpawnType.SPAWN_EGG,
                        true,
                        !Objects.equals(blockpos, blockpos1) && direction == Direction.UP
                );
            }

            itemstack.shrink(1);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
        }

        return InteractionResult.CONSUME;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemstack);
        } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!(level.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemstack);
            } else if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {
                for (Supplier<? extends EntityType<? extends AgeableMob>> entitytype : musicians) {
                    entitytype.get().spawn((ServerLevel)level, itemstack, player, blockpos, MobSpawnType.SPAWN_EGG, false, false);
                }

                itemstack.shrink(1);
                player.awardStat(Stats.ITEM_USED.get(this));
                level.gameEvent(player, GameEvent.ENTITY_PLACE, blockpos);
                return InteractionResultHolder.consume(itemstack);
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!musicians.isEmpty()) {
            MutableComponent combinedTooltip = null;

            for (int i = 0; i < musicians.size(); i++) {
                Component entityName = musicians.get(i).get().getDescription();

                if (combinedTooltip == null) {
                    combinedTooltip = entityName.copy();
                } else if (i == musicians.size() - 1) {
                    combinedTooltip.append(" & ").append(entityName);
                } else {
                    combinedTooltip.append(", ").append(entityName);
                }
            }

            if (combinedTooltip != null) {
                tooltipComponents.add(combinedTooltip.withStyle(ChatFormatting.GRAY));
            }
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}
