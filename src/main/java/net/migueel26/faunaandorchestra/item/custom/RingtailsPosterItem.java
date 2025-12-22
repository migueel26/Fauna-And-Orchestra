package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.decorative.RingtailsPosterEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class RingtailsPosterItem extends Item {
    public static ResourceKey<PaintingVariant> variantKey = ResourceKey.create(
            Registries.PAINTING_VARIANT,
            ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "ringtails_poster"));
    public RingtailsPosterItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.faunaandorchestra.ringtails_poster.desc")
                .withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Direction facing = context.getClickedFace();

        if (facing.getAxis().isVertical()) {
            return InteractionResult.FAIL; // Can't place on floor/ceiling
        }

        if (!level.isClientSide) {
            // The variant we want to spawn
            RingtailsPosterEntity painting = new RingtailsPosterEntity(level, pos, facing,
                    level.registryAccess()
                            .registryOrThrow(Registries.PAINTING_VARIANT)
                            .getHolderOrThrow(variantKey));

            if (painting.survives()) {
                level.addFreshEntity(painting);
                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                painting.playPlacementSound();
                return InteractionResult.SUCCESS;
            } else {
                painting.discard();
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.FAIL;
    }
}
