package io.github.jamalam360.reaping.fabric;

import io.github.jamalam360.reaping.Reaping;
import net.fabricmc.api.ModInitializer;

public class ReapingFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		Reaping.init();
	}
}
