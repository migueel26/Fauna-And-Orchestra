package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BriefcaseItem extends Item {
    public BriefcaseItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack briefcase = player.getItemInHand(usedHand);
        if (this.calculateHitResult(player).getType() != HitResult.Type.ENTITY
                  && (briefcase.get(ModDataComponents.BRIEFCASE_ANIMAL_LIST) == null
                  || briefcase.get(ModDataComponents.BRIEFCASE_ANIMAL_LIST).size() < 5)) {

            if (!level.isClientSide()) {
                if (briefcase.getOrDefault(ModDataComponents.OPENED, false)) {
                    briefcase.set(ModDataComponents.OPENED, false);
                } else {
                    briefcase.set(ModDataComponents.OPENED, true);
                }
            }

            return InteractionResultHolder.pass(briefcase);
        } else {
            return InteractionResultHolder.fail(briefcase);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // TODO: SHINING SQUARE? CUSTOM PARTICLE AND SOUND
            ItemStack briefcase = context.getItemInHand();
            List<String> animals = briefcase.get(ModDataComponents.BRIEFCASE_ANIMAL_LIST);
            if (animals != null && !animals.isEmpty() && !briefcase.getOrDefault(ModDataComponents.OPENED, false)) {
                if (!context.getLevel().isClientSide()) {
                    String animalString = animals.getFirst();
                    List<String> newAnimals = new ArrayList<>(animals);
                    newAnimals.removeFirst();
                    ServerLevel level = (ServerLevel) context.getLevel();
                    BlockPos block = context.getClickedPos().above();
                    briefcase.set(ModDataComponents.BRIEFCASE_ANIMAL_LIST, newAnimals);
                    if (newAnimals.isEmpty()) {
                        briefcase.set(ModDataComponents.OPENED, true);
                    }
                    spawnMusicalEntity(animalString, level, block, context.getPlayer());
                    level.sendParticles(ParticleTypes.PORTAL,
                            block.getX(), block.getY(), block.getZ(),
                            40, 0.5, 0.5, 0.5, 0F);
                } else {
                    context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.PLAYER_TELEPORT, SoundSource.BLOCKS);
                }
                return InteractionResult.SUCCESS;
            } else {
                return  InteractionResult.PASS;
            }
        }

    private void spawnMusicalEntity(String animalString, ServerLevel level, BlockPos block, Player player) {
        String[] elements = animalString.split(";");
        EntityType<? extends MusicalEntity> musicalEntityType = null;
        EntityType<? extends ConductorEntity> conductorEntityType = null;
        // Entity Type
        switch (elements[0]) {
            case "MantisEntity" -> musicalEntityType = ModEntities.MANTIS.get();
            case "PenguinEntity" -> musicalEntityType = ModEntities.PENGUIN.get();
            case "RedPandaEntity" -> musicalEntityType = ModEntities.RED_PANDA.get();
            case "MacawEntity" -> musicalEntityType = ModEntities.MACAW.get();
            case "QuirkyFrogEntity" -> conductorEntityType = ModEntities.QUIRKY_FROG.get();
        }
        // Holding Instrument
        boolean holdingInstrument = elements[1].equals("t");
        // Holding Sheet Music
        Item sheet = MusicUtil.getSheet(elements[2]);
        // Custom Name
        String customName = elements[3].equals("f") ? null : elements[3];

        if (musicalEntityType != null) {
            MusicalEntity musicalEntity = musicalEntityType.spawn(level, block, MobSpawnType.MOB_SUMMONED);
            if (musicalEntity != null) {
                musicalEntity.tame(player);
                musicalEntity.setHoldingInstrument(holdingInstrument);
                musicalEntity.setOrderedToSit(true);
                if (customName != null) {
                    musicalEntity.setCustomName(Component.literal(customName));
                }
            }
        } else if (conductorEntityType != null){
            ConductorEntity conductor = conductorEntityType.spawn(level, block, MobSpawnType.MOB_SUMMONED);
            if (conductor != null) {
                conductor.tame(player);
                conductor.setHoldingBaton(holdingInstrument);
                conductor.setOrderedToSit(true);
                if (sheet != Items.AIR) {
                    conductor.inventory.setStackInSlot(0, new ItemStack(sheet));
                }
                if (customName != null) {
                    conductor.setCustomName(Component.literal(customName));
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        List<String> animals = stack.get(ModDataComponents.BRIEFCASE_ANIMAL_LIST);
        if (Screen.hasShiftDown()) {
            if (animals == null || animals.isEmpty()) {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:briefcase_empty"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:briefcase_full"));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        if (animals != null) {
            for (String animalString : animals) {
                addStoredAnimal(tooltipComponents, animalString);
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private static void addStoredAnimal(List<Component> tooltipComponents, String animalString) {
        if (animalString != null) {
            String[] elements = animalString.split(";");
            MutableComponent typeName = null;
            String name = elements[3].equals("f") ? null : elements[3];
            switch (elements[0]) {
                case "MantisEntity" -> typeName = Component.translatable("entity.faunaandorchestra.mantis");
                case "PenguinEntity" -> typeName = Component.translatable("entity.faunaandorchestra.penguin");
                case "RedPandaEntity" -> typeName = Component.translatable("entity.faunaandorchestra.red_panda");
                case "MacawEntity" -> typeName = Component.translatable("entity.faunaandorchestra.macaw");
                case "QuirkyFrogEntity" -> typeName = Component.translatable("entity.faunaandorchestra.quirky_frog");
            }
            if (typeName != null) {
                if (name != null) {
                    tooltipComponents.add(typeName.append(Component.literal(" (" + name + ")")).withStyle(ChatFormatting.DARK_GRAY));
                } else {
                    tooltipComponents.add(typeName.withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        }
    }

    private HitResult calculateHitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(
                player, entity -> !entity.isSpectator() && entity.isPickable(), player.blockInteractionRange()
        );
    }
}
