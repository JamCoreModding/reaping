package io.github.jamalam360.reaping.fabric;

import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleTypes;

public class ReapingPlatformImpl {
	public static void setScale(LivingEntity entity, float scale) {
		ScaleTypes.BASE.getScaleData(entity).setScale(scale);
	}
}
