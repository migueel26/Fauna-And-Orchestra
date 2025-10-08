package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MusicalInkItem extends Item {
    String[] colors = new String[]{"§5", "§d", "§b", "§9"};
    public MusicalInkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (be instanceof SignBlockEntity signBE && state.getBlock() instanceof SignBlock signBlock) {
            SignText signText = signBE.getText(signBE.isFacingFrontText(context.getPlayer()));
            Component[] components = signText.getMessages(false);

            if (components[0].getString().toCharArray()[0] == '§') {
                return InteractionResult.FAIL;
            }

            int color = 0;
            SignText finalSignText = new SignText().setColor(DyeColor.WHITE).setHasGlowingText(true);

            for (int i = 0; i < components.length; i++) {

                char[] text = components[i].getString().toCharArray();
                StringBuilder newText = new StringBuilder();
                for (int c = 0; c < text.length; c++) {
                    String newChar = String.valueOf(text[c]);
                    if (text[c] != ' ') {
                        newChar = colors[color] + text[c];
                        color = nextColor(color);
                    }
                    newText.append(newChar);
                }

                finalSignText = finalSignText.setMessage(i, Component.literal(newText.toString()));

            }

            signBE.setText(finalSignText, signBE.isFacingFrontText(context.getPlayer()));
            context.getItemInHand().shrink(1);
            if (!context.getLevel().isClientSide()) {
                Vec3 pos = context.getClickLocation();
                ((ServerLevel) context.getLevel()).sendParticles(ModParticleTypes.MAGICAL_NOTE.get(), pos.x, pos.y, pos.z, 10, 0.4, 0.4, 0.4, 0.05);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    private int nextColor(int c) {
        return (c + 1) % colors.length;
    }
}
