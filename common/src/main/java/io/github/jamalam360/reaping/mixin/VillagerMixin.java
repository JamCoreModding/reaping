package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.item.ReaperItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin {
	@Inject(
			method = "mobInteract",
			at = @At("HEAD"),
			cancellable = true
	)
	private void reaping$preventTrading(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (player.getItemInHand(hand).getItem() instanceof ReaperItem) {
			cir.setReturnValue(InteractionResult.PASS);
		}
	}
}
