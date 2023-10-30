package io.github.jamalam360.reaping.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CurseOfBluntness extends Enchantment {
	public CurseOfBluntness() {
		super(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
	}

	@Override
	public float getDamageBonus(int i, MobType mobType) {
		return -0.4F;
	}

	@Override
	protected boolean checkCompatibility(Enchantment other) {
		return !(other instanceof DamageEnchantment) && super.checkCompatibility(other);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return stack.getItem() instanceof ReaperItem;
	}

	@Override
	public boolean isCurse() {
		return true;
	}
}
