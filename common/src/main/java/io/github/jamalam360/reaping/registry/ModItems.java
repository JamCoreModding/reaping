package io.github.jamalam360.reaping.registry;

import io.github.jamalam360.jamlib.api.registry.DeferredRegistry;
import io.github.jamalam360.jamlib.api.registry.RegistryObject;
import io.github.jamalam360.reaping.Reaping;
import io.github.jamalam360.reaping.content.item.ReaperItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.List;

public class ModItems {
	private static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Reaping.MOD_ID, BuiltInRegistries.ITEM);
	public static final RegistryObject<Item> IRON_REAPER = ITEMS.register(Reaping.id("iron_reaper"), (k) -> createReaper(k, ToolMaterial.IRON, 1.0F));
	public static final RegistryObject<Item> GOLD_REAPER = ITEMS.register(Reaping.id("gold_reaper"), (k) -> createReaper(k, ToolMaterial.GOLD, 0.75F));
	public static final RegistryObject<Item> DIAMOND_REAPER = ITEMS.register(Reaping.id("diamond_reaper"), (k) -> createReaper(k, ToolMaterial.DIAMOND, 0.4F));
	public static final RegistryObject<Item> NETHERITE_REAPER = ITEMS.register(Reaping.id("netherite_reaper"), (k) -> createReaper(k, ToolMaterial.NETHERITE, 0.2F));
	public static final RegistryObject<Item> HUMANOID_MEAT = ITEMS.register(Reaping.id("humanoid_meat"),
			(k) -> new Item(
					new Item.Properties()
							.food(new FoodProperties.Builder()
											.alwaysEdible()
											.nutrition(7)
											.saturationModifier(1.4f)
											.build(),
									Consumable.builder()
											.onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
													new MobEffectInstance(MobEffects.HUNGER, 25 * 20),
													new MobEffectInstance(MobEffects.NAUSEA, 35 * 20),
													new MobEffectInstance(MobEffects.POISON, 10 * 20))))
											.build()
							)
							.setId(k)
			));

	public static void registerAll() {
		ITEMS.registerEntries();
	}

	private static ReaperItem createReaper(ResourceKey<Item> key, ToolMaterial material, float sharpnessModifier) {
		Item.Properties properties = new Item.Properties().setId(key);
		if (material == ToolMaterial.NETHERITE) {
			properties = properties.fireResistant();
		}

		ReaperItem item = new ReaperItem(material, properties, sharpnessModifier);
		DispenserBlock.registerBehavior(item, new ReaperItem.DispenserBehavior());
		return item;
	}
}
