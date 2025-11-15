package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.TipCaseBlock;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import software.bernie.geckolib.model.GeoModel;

public class TipCaseModel extends GeoModel<TipCaseBlockEntity> {
    private static final ResourceLocation TIP_CASE_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/tip_case.png");
    private static final ResourceLocation TIP_CASE_HANDLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/tip_case_handle.png");
    private static final ResourceLocation TIP_CASE_FULL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/tip_case_full.png");
    private static final ResourceLocation TIP_CASE_HANDLE_FULL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/tip_case_handle_full.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/composer_gravestone.animation.json");
    private static final ResourceLocation TIP_CASE_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/tip_case.geo.json");
    private static final ResourceLocation TIP_CASE_HANDLE_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/tip_case_handle.geo.json");
    @Override
    public ResourceLocation getModelResource(TipCaseBlockEntity animatable) {
        return animatable.getBlockState().getValue(TipCaseBlock.PART) == BedPart.FOOT ?
                TIP_CASE_MODEL : TIP_CASE_HANDLE_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TipCaseBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        if (state.getValue(TipCaseBlock.PART) == BedPart.FOOT) {
            if (state.getValue(TipCaseBlock.TIPS) == TipCaseBlock.THIRD_REWARD) {
                return TIP_CASE_FULL_TEXTURE;
            } else {
                return TIP_CASE_TEXTURE;
            }
        } else {
            if (state.getValue(TipCaseBlock.TIPS) == TipCaseBlock.THIRD_REWARD) {
                return TIP_CASE_HANDLE_FULL_TEXTURE;
            } else {
                return TIP_CASE_HANDLE_TEXTURE;
            }
        }
    }

    @Override
    public ResourceLocation getAnimationResource(TipCaseBlockEntity animatable) {
        return ANIMATIONS;
    }
}
