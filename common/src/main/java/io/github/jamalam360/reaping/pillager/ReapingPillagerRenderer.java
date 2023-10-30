package io.github.jamalam360.reaping.pillager;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ReapingPillagerRenderer extends IllagerRenderer<ReapingPillager> {
	private static final ResourceLocation PILLAGER = new ResourceLocation("textures/entity/illager/pillager.png");

	public ReapingPillagerRenderer(EntityRendererProvider.Context context) {
		super(context, new IllagerModel<>(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(ReapingPillager pillager) {
		return PILLAGER;
	}
}
