package io.github.jamalam360.reaping.forge;

import io.github.jamalam360.reaping.Reaping;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reaping.MOD_ID)
public class ReapingForge {
    public ReapingForge() {
        EventBuses.registerModEventBus(Reaping.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Reaping.init();
    }
}
