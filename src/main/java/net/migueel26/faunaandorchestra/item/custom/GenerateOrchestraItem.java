package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class GenerateOrchestraItem extends Item {
    public GenerateOrchestraItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos().above();
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;

            QuirkyFrogEntity quirkyFrog = ModEntities.QUIRKY_FROG.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);

            MantisEntity mantis = ModEntities.MANTIS.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);
            PenguinEntity penguin = ModEntities.PENGUIN.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);
            RedPandaEntity redPanda = ModEntities.RED_PANDA.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);
            MacawEntity macaw = ModEntities.MACAW.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);
            BeaverEntity beaver = ModEntities.BEAVER.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);
            LemurEntity lemur = ModEntities.LEMUR.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);
            MadameButterflyEntity madameButterfly = ModEntities.MADAME_BUTTERFLY.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);
            SeaLionEntity seaLion = ModEntities.SEA_LION.get().spawn(serverLevel, pos, MobSpawnType.SPAWN_EGG);

            quirkyFrog.tame(player);
            mantis.tame(player);
            penguin.tame(player);
            redPanda.tame(player);
            macaw.tame(player);
            beaver.tame(player);
            lemur.tame(player);
            madameButterfly.tame(player);
            seaLion.tame(player);

            quirkyFrog.setOrderedToSit(true);
            mantis.setOrderedToSit(true);
            penguin.setOrderedToSit(true);
            redPanda.setOrderedToSit(true);
            macaw.setOrderedToSit(true);
            beaver.setOrderedToSit(true);
            lemur.setOrderedToSit(true);
            madameButterfly.setOrderedToSit(true);
            seaLion.setOrderedToSit(true);

            mantis.setMusical(true);
            penguin.setMusical(true);
            redPanda.setMusical(true);
            macaw.setMusical(true);
            beaver.setMusical(true);
            lemur.setMusical(true);
            madameButterfly.setMusical(true);
            seaLion.setMusical(true);

            quirkyFrog.setHoldingBaton(true);
            mantis.setHoldingInstrument(true);
            penguin.setHoldingInstrument(true);
            redPanda.setHoldingInstrument(true);
            macaw.setHoldingInstrument(true);
            beaver.setHoldingInstrument(true);
            lemur.setHoldingInstrument(true);
            madameButterfly.setHoldingInstrument(true);
            seaLion.setHoldingInstrument(true);
        }

        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        MutableComponent instruments = Component.empty();
        Iterator<Item> iterator = MusicUtil.INSTRUMENTS.iterator();
        while (iterator.hasNext()) {
            instruments.append(Component.translatable(iterator.next().getDescriptionId()));
            if (iterator.hasNext()) {
                instruments.append(Component.literal(", "));
            }
        }
        tooltipComponents.add(instruments.withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}