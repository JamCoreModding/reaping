package io.github.jamalam360.reaping.registry;

import io.github.jamalam360.jamlib.api.registry.DeferredRegistry;
import io.github.jamalam360.jamlib.api.registry.RegistryObject;
import io.github.jamalam360.reaping.Reaping;
import io.github.jamalam360.reaping.content.effect.ShrinkEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {
	private static final DeferredRegistry<MobEffect> EFFECTS = DeferredRegistry.create(Reaping.MOD_ID, BuiltInRegistries.MOB_EFFECT);
    public static final RegistryObject<MobEffect> SHRINK = EFFECTS.register(Reaping.id("shrink"), ShrinkEffect::new);

	public static void registerAll() {
		EFFECTS.registerEntries();
	}
}
