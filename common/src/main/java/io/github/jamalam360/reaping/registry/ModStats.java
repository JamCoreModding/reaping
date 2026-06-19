package io.github.jamalam360.reaping.registry;

import io.github.jamalam360.jamlib.api.registry.DeferredRegistry;
import io.github.jamalam360.jamlib.api.registry.RegistryObject;
import io.github.jamalam360.reaping.Reaping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModStats {
	private static final DeferredRegistry<Identifier> STATS = DeferredRegistry.create(Reaping.MOD_ID, BuiltInRegistries.CUSTOM_STAT);
	public static final RegistryObject<Identifier> USE_REAPER_TOOL_STAT = STATS.register(Reaping.id("use_reaper_tool"), () -> Reaping.id("use_reaper_tool"));

	public static void registerAll() {
		STATS.registerEntries();
	}
}
