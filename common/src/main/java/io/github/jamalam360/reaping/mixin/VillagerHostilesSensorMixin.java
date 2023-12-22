package io.github.jamalam360.reaping.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.jamalam360.reaping.Content;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {
	@Shadow
	@Final
	@Mutable
	private static ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES;

	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void reaping$addPillager(CallbackInfo ci) {
		ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.<EntityType<?>, Float>builder().put(Content.PILLAGER.get(), 15F).putAll(ACCEPTABLE_DISTANCE_FROM_HOSTILES).build();
	}
}
