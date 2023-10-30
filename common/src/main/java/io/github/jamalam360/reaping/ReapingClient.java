package io.github.jamalam360.reaping;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import io.github.jamalam360.reaping.pillager.ReapingPillagerRenderer;

public class ReapingClient {
	public static void initClient() {
		EntityRendererRegistry.register(Content.PILLAGER, ReapingPillagerRenderer::new);
	}
}
