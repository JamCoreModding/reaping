package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.ShrinkEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(
			method = "onEffectRemoved",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/effect/MobEffect;removeAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V"
			)
	)
	private void reaping$updateScaleOnEffectRemoved(MobEffectInstance mobEffectInstance, CallbackInfo ci) {
		if (mobEffectInstance.getEffect() instanceof ShrinkEffect shrinkEffect) {
			shrinkEffect.onEffectFinished((LivingEntity) (Object) this);
		}
	}
}
