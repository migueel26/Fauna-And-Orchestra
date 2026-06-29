package net.migueel26.faunaandorchestra.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class OverwhelmingSlownessEffect extends MobEffect {
    public OverwhelmingSlownessEffect() {
        super(MobEffectCategory.HARMFUL, 0x2A0747);

        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                "71074b79-20c7-4bc2-bc08-33df45f26194",
                -0.90D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }
}
