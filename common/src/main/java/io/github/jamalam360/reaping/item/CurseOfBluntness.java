package io.github.jamalam360.reaping.item;

import io.github.jamalam360.reaping.Content;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public class CurseOfBluntness extends Enchantment {

    public CurseOfBluntness() {
        super(Enchantment.definition(Content.REAPERS_TAG, 1, 1, Enchantment.constantCost(25), Enchantment.constantCost(50), 8, EquipmentSlot.values()));
    }

    @Override
    public float getDamageBonus(int i, @Nullable EntityType<?> entityType) {
        return -0.4f;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof DamageEnchantment) && super.checkCompatibility(other);
    }

    @Override
    public boolean isCurse() {
        return true;
    }
}
