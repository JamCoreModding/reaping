package io.github.jamalam360.reaping;

import dev.architectury.registry.level.entity.trade.SimpleTrade;
import dev.architectury.registry.level.entity.trade.TradeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.item.ReaperItem;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.level.block.DispenserBlock;

public class Content {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Reaping.MOD_ID, Registries.ITEM);
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Reaping.MOD_ID, Registries.MOB_EFFECT);
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
                            .alwaysEdible()
                            .nutrition(7)
                            .saturationModifier(1.4f)
                            .effect(new MobEffectInstance(MobEffects.HUNGER, 25 * 20), 1)
                            .effect(new MobEffectInstance(MobEffects.CONFUSION, 35 * 20), 1)
                            .effect(new MobEffectInstance(MobEffects.POISON, 10 * 20), 1)
                            .build()
                      )
          ));
    public static final RegistrySupplier<ShrinkEffect> SHRINK = EFFECTS.register(Reaping.id("shrink"), ShrinkEffect::new);
    public static final ResourceLocation USE_REAPER_TOOL_STAT = Reaping.id("use_reaper_tool");
    public static final TagKey<Item> REAPERS_TAG = TagKey.create(BuiltInRegistries.ITEM.key(), Reaping.id("reapers"));
    public static final ResourceKey<Enchantment> BLUNTNESS_CURSE = ResourceKey.create(Registries.ENCHANTMENT, Reaping.id("bluntness_curse"));

    public static void registerAll() {
        ITEMS.register();
        EFFECTS.register();
        STATS.register();

        DIAMOND_REAPER.listen((item) -> TradeRegistry.registerVillagerTrade(
              VillagerProfession.BUTCHER,
              5,
              new SimpleTrade(
                    new ItemCost(Items.EMERALD, 13),
                    Optional.empty(),
                    item.getDefaultInstance(),
                    3,
                    30,
                    1
              )
        ));

        HUMANOID_MEAT.listen((meat) -> TradeRegistry.registerTradeForWanderingTrader(
              true,
              new SimpleTrade(
                    new ItemCost(Items.EMERALD, 7),
                    Optional.empty(),
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
