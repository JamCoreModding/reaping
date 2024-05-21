package io.github.jamalam360.reaping.item;

import io.github.jamalam360.reaping.Content;
import io.github.jamalam360.reaping.Reaping;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class ReaperItem extends TieredItem {

    private final ItemAttributeModifiers modifiers;
    private final float sharpnessModifier;

    public ReaperItem(Tier tier, Properties properties, float sharpnessModifier) {
        super(tier, properties.stacksTo(1).arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES).durability(tier.getUses()));
        this.sharpnessModifier = sharpnessModifier;

        float attackDamage = switch ((Tiers) tier) {
            case IRON -> 3.4f;
            case GOLD -> 4.3f;
            case DIAMOND -> 5.2f;
            case NETHERITE -> 6.8f;
            default -> throw new IllegalArgumentException("Invalid Reaper tool material: " + tier);
        };

        this.modifiers = ItemAttributeModifiers.builder()
              .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "3cd506aa-2a1b-40be-b09f-7cee6b94af56", attackDamage, Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
              .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "05a49562-180e-4b2e-8290-7d6b5c363bfc", -2.8f, Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
              .build();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.modifiers;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (Reaping.reap(player, entity, stack) == InteractionResult.SUCCESS) {
            player.getItemInHand(hand).hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            player.awardStat(Content.USE_REAPER_TOOL_STAT, 1);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public float getSharpnessModifier() {
        return this.sharpnessModifier;
    }

    public int getCooldownTicks() {
        return switch ((Tiers) this.getTier()) {
            case IRON -> 45;
            case GOLD -> 18;
            case DIAMOND -> 28;
            case NETHERITE -> 23;
            default -> throw new IllegalArgumentException("Invalid Reaper tool material: " + this.getTier());
        };
    }

    public static class DispenserBehavior extends OptionalDispenseItemBehavior {

        private static boolean tryReapEntity(ServerLevel level, BlockPos pos, ItemStack stack) {
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos), e -> {
                if (e instanceof Player player) {
                    return !player.isSpectator();
                }

                return true;
            });

            for (LivingEntity livingEntity : entities) {
                return Reaping.reap(null, livingEntity, stack) == InteractionResult.SUCCESS;
            }

            return false;
        }

        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            BlockPos blockPos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            this.setSuccess(tryReapEntity(source.level(), blockPos, stack));

            if (this.isSuccess()) {
                stack.hurtAndBreak(1, source.level().random, null, () -> stack.setCount(0));
            }

            return stack;
        }
    }
}
