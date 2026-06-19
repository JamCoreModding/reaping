package io.github.jamalam360.reaping.registry;

import io.github.jamalam360.reaping.Reaping;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantmentIds {
    public static final ResourceKey<Enchantment> BLUNTNESS_CURSE = ResourceKey.create(Registries.ENCHANTMENT, Reaping.id("bluntness_curse"));
}
