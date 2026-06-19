package io.github.jamalam360.reaping;

import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.jamlib.api.config.ConfigManager;
import io.github.jamalam360.jamlib.api.platform.Platform;
import io.github.jamalam360.reaping.registry.ModEffects;
import io.github.jamalam360.reaping.registry.ModItems;
import io.github.jamalam360.reaping.registry.ModStats;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reaping {
    public static final String MOD_ID = "reaping";
    public static final String MOD_NAME = "Reaping";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final ConfigManager<Config> CONFIG = new ConfigManager<>(MOD_ID, Config.class);

    public static void init() {
        LOGGER.info("Initializing Reaping on {}", Platform.getModLoader());
        JamLib.checkForJarRenaming(Reaping.class);
        ModEffects.registerAll();
        ModItems.registerAll();
        ModStats.registerAll();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
