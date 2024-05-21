package io.github.jamalam360.reaping;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ShrinkEffect extends MobEffect {

    protected ShrinkEffect() {
        super(MobEffectCategory.NEUTRAL, ChatFormatting.DARK_RED.getColor());
        this.addAttributeModifier(Attributes.SCALE, "4b234564-2452-4229-b3fd-b7c4d29277a9", -0.6F, Operation.ADD_MULTIPLIED_TOTAL);
    }
}
