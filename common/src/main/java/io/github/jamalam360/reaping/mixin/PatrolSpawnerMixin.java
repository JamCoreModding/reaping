package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.Content;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PatrolSpawner.class)
public class PatrolSpawnerMixin {
	@Redirect(
			method = "spawnPatrolMember",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"
			)
	)
	private Entity reaping$spawnPillager(EntityType instance, Level arg) {
		if (arg.random.nextFloat() < 0.15F) {
			return Content.PILLAGER.get().create(arg);
		} else {
			return instance.create(arg);
		}
	}
}
