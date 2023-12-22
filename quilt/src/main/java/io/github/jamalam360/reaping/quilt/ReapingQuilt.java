package io.github.jamalam360.reaping.quilt;

import io.github.jamalam360.reaping.Reaping;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class ReapingQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        Reaping.init();
    }
}
