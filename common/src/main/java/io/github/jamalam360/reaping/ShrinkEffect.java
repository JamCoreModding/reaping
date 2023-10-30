package io.github.jamalam360.reaping;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class ShrinkEffect extends MobEffect {
	protected ShrinkEffect() {
		super(MobEffectCategory.NEUTRAL, ChatFormatting.DARK_RED.getColor());
	}

	@Override
	public void onEffectStarted(LivingEntity target, int strength) {
		super.onEffectStarted(target, strength);
		ReapingPlatform.setScale(target, 0.3F);
	}

	public void onEffectFinished(LivingEntity target) {
		ReapingPlatform.setScale(target, 1.0F);
	}
}
