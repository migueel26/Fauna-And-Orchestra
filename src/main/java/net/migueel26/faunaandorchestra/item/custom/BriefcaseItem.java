package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BriefcaseItem extends Item {
    public static final int MAX_CAPACITY = 6;

    public BriefcaseItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack briefcase = player.getItemInHand(usedHand);
        if (this.calculateHitResult(player).getType() != HitResult.Type.ENTITY) {
            CompoundTag itemTag = briefcase.getOrCreateTag();
            ListTag entityList;

            // We get the list if there is one
            if (itemTag.contains(ModDataComponents.BRIEFCASE_ANIMAL_LIST, Tag.TAG_LIST)) {
                entityList = itemTag.getList(ModDataComponents.BRIEFCASE_ANIMAL_LIST, Tag.TAG_COMPOUND);
            } else {
                entityList = new ListTag();
            }

            if (entityList.isEmpty() || entityList.size() < 6) {
                if (!level.isClientSide()) {
                    if (itemTag.getBoolean(ModDataComponents.OPENED)) {
                        itemTag.putBoolean(ModDataComponents.OPENED, false);
                    } else {
                        itemTag.putBoolean(ModDataComponents.OPENED, true);
                    }
                }
                return InteractionResultHolder.pass(briefcase);
            }
        }
        return InteractionResultHolder.fail(briefcase);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack briefcase = context.getItemInHand();
        CompoundTag itemTag = briefcase.getOrCreateTag();
        ListTag entityList;

        // We get the list if there is one
        if (itemTag.contains(ModDataComponents.BRIEFCASE_ANIMAL_LIST, Tag.TAG_LIST)) {
            entityList = itemTag.getList(ModDataComponents.BRIEFCASE_ANIMAL_LIST, Tag.TAG_COMPOUND);
        } else {
            entityList = new ListTag();
        }

        if (!entityList.isEmpty() && !itemTag.getBoolean(ModDataComponents.OPENED)) {
            if (!context.getLevel().isClientSide()) {
                CompoundTag animalTag = entityList.getCompound(entityList.size() - 1);

                ServerLevel level = (ServerLevel) context.getLevel();
                BlockPos block = context.getClickedPos().above();

                // We spawn the animal
                spawnMusicalEntity(animalTag, level, block, context.getPlayer());

                // We remove the animal tag
                entityList.remove(entityList.size() - 1);

                // We open the briefcase if empty
                if (entityList.isEmpty()) {
                    itemTag.putBoolean(ModDataComponents.OPENED, true);
                }

                // We save the new list
                itemTag.put(ModDataComponents.BRIEFCASE_ANIMAL_LIST, entityList);

                level.sendParticles(ParticleTypes.PORTAL,
                        block.getX(), block.getY(), block.getZ(),
                        40, 0.5, 0.5, 0.5, 0F);


            }
            context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0f, 1.0f);

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    private void spawnMusicalEntity(CompoundTag animalTag, ServerLevel level, BlockPos pos, Player player) {
        Optional<Entity> entityOpt = EntityType.create(animalTag, level);
        if (entityOpt.isPresent()) {
            Entity entity = entityOpt.get();
            entity.setPos(pos.getCenter().x(), pos.getY(), pos.getCenter().z());

            level.addFreshEntity(entity);

            if (entity instanceof MusicalEntity musicalEntity && musicalEntity.isHoldingInstrument()) {
                musicalEntity.searchForConductor();
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            if (nbt != null && nbt.contains(ModDataComponents.BRIEFCASE_ANIMAL_LIST, Tag.TAG_LIST)) {
                ListTag animals = nbt.getList(ModDataComponents.BRIEFCASE_ANIMAL_LIST, Tag.TAG_COMPOUND);

                if (Screen.hasShiftDown()) {
                    if (animals.size() < 6) {
                        tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:briefcase_empty"));
                    }
                    if (!animals.isEmpty()) {
                        tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:briefcase_full"));
                    }
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
                }

                for (int i = 0; i < animals.size(); i++) {
                    addStoredAnimal(tooltipComponents, animals.getCompound(i));
                }

            } else {
                if (Screen.hasShiftDown()) {
                    tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:briefcase_empty"));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
                }
            }
        } else {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:briefcase_empty"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
            }
        }

        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private static void addStoredAnimal(List<Component> tooltipComponents, CompoundTag entityTag) {
        MutableComponent typeName = null;

        if (entityTag.contains("id")) {
            String id = entityTag.getString("id");

            typeName = EntityType.byString(id)
                    .map(EntityType::getDescription) // We get the name
                    .orElse(Component.literal(id)) // We get the literal if there's an error
                    .copy();
        }

        String name = null;

        if (entityTag.contains("DisplayName")) {
            name = entityTag.getString("DisplayName");
        }

        if (typeName != null) {
            if (name != null) {
                tooltipComponents.add(typeName.append(Component.literal(" (" + name + ")")).withStyle(ChatFormatting.DARK_GRAY));
            } else {
                tooltipComponents.add(typeName.withStyle(ChatFormatting.DARK_GRAY));
            }
        }

    }

    private HitResult calculateHitResult(Player player) {
        double reach = player.getAttributeValue(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get());
        return ProjectileUtil.getHitResultOnViewVector(
                player, entity -> !entity.isSpectator() && entity.isPickable(), reach
        );
    }
}