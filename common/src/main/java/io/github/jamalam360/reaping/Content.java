package io.github.jamalam360.reaping;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.level.entity.trade.SimpleTrade;
import dev.architectury.registry.level.entity.trade.TradeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.item.CurseOfBluntness;
import io.github.jamalam360.reaping.item.ReaperItem;
import io.github.jamalam360.reaping.pillager.ReapingPillager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.DispenserBlock;

public class Content {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Reaping.MOD_ID, Registries.ITEM);
	private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Reaping.MOD_ID, Registries.ENTITY_TYPE);
	private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Reaping.MOD_ID, Registries.MOB_EFFECT);
	private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Reaping.MOD_ID, Registries.ENCHANTMENT);
	private static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(Reaping.MOD_ID, Registries.CUSTOM_STAT);

	public static final RegistrySupplier<ReaperItem> IRON_REAPER = ITEMS.register(Reaping.id("iron_reaper"), () -> createReaper(Tiers.IRON, 1.0F));
	public static final RegistrySupplier<ReaperItem> GOLD_REAPER = ITEMS.register(Reaping.id("gold_reaper"), () -> createReaper(Tiers.GOLD, 0.75F));
	public static final RegistrySupplier<ReaperItem> DIAMOND_REAPER = ITEMS.register(Reaping.id("diamond_reaper"), () -> createReaper(Tiers.DIAMOND, 0.4F));
	public static final RegistrySupplier<ReaperItem> NETHERITE_REAPER = ITEMS.register(Reaping.id("netherite_reaper"), () -> createReaper(Tiers.NETHERITE, 0.2F));
	public static final RegistrySupplier<Item> HUMANOID_MEAT = ITEMS.register(Reaping.id("humanoid_meat"),
			() -> new Item(
					new Item.Properties()
							.arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)
							.food(new FoodProperties.Builder()
									.meat().alwaysEat()
									.nutrition(7).saturationMod(1.4f)
									.effect(new MobEffectInstance(MobEffects.HUNGER, 25 * 20), 1)
									.effect(new MobEffectInstance(MobEffects.CONFUSION, 35 * 20), 1)
									.effect(new MobEffectInstance(MobEffects.POISON, 10 * 20), 1)
									.build()
							)
			));
	public static final RegistrySupplier<EntityType<ReapingPillager>> PILLAGER = ENTITIES.register(Reaping.id("pillager"), () -> EntityType.Builder
			.of(ReapingPillager::new, MobCategory.MONSTER)
			.canSpawnFarFromPlayer()
			.sized(0.6F, 1.95F)
			.clientTrackingRange(8)
			.build("reaping_pillager"));
	public static final RegistrySupplier<ShrinkEffect> SHRINK = EFFECTS.register(Reaping.id("shrink"), ShrinkEffect::new);
	public static final RegistrySupplier<CurseOfBluntness> CURSE_OF_BLUNTNESS = ENCHANTMENTS.register(Reaping.id("curse_of_bluntness"), CurseOfBluntness::new);
	public static final ResourceLocation USE_REAPER_TOOL_STAT = Reaping.id("use_reaper_tool");

	public static void registerAll() {
		ITEMS.register();
		ENTITIES.register();
		EFFECTS.register();
		ENCHANTMENTS.register();
		STATS.register();

		EntityAttributeRegistry.register(PILLAGER, ReapingPillager::createReapingPillagerAttributes);

		DIAMOND_REAPER.listen((item) -> TradeRegistry.registerVillagerTrade(
				VillagerProfession.BUTCHER,
				5,
				new SimpleTrade(
						new ItemStack(Items.EMERALD, 13),
						ItemStack.EMPTY,
						item.getDefaultInstance(),
						3,
						30,
						1
				)
		));

		HUMANOID_MEAT.listen((meat) -> TradeRegistry.registerTradeForWanderingTrader(
				true,
				new SimpleTrade(
						new ItemStack(Items.EMERALD, 7),
						ItemStack.EMPTY,
						meat.getDefaultInstance(),
						6,
						20,
						1
				)
		));
	}

	private static ReaperItem createReaper(Tiers tier, float sharpnessModifier) {
		ReaperItem item = new ReaperItem(tier, new Item.Properties(), sharpnessModifier);
		DispenserBlock.registerBehavior(item, new ReaperItem.DispenserBehavior());
		return item;
	}

	static {
		STATS.register(USE_REAPER_TOOL_STAT, () -> USE_REAPER_TOOL_STAT);
	}
}
