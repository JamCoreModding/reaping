package io.github.jamalam360.reaping.content.effect;

import io.github.jamalam360.reaping.Reaping;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.datafix.fixes.ColorlessShulkerEntityFix;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ShrinkEffect extends MobEffect {
    public ShrinkEffect() {
        super(MobEffectCategory.NEUTRAL, 4738376);
        this.addAttributeModifier(Attributes.SCALE, Reaping.id("effect.reaping.scale"), -0.6F, Operation.ADD_MULTIPLIED_TOTAL);
    }
}
