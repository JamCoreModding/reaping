package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.Content;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(Parrot.class)
public class ParrotMixin {
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Inject(
			method = "method_6579",
			at = @At("TAIL")
	)
	private static void reaping$addPillagerImitation(HashMap hashMap, CallbackInfo ci) {
		hashMap.put(Content.PILLAGER.get(), SoundEvents.PARROT_IMITATE_PILLAGER);
	}
}
