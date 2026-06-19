package io.github.jamalam360.reaping.content.item;

import io.github.jamalam360.reaping.Reaper;
import io.github.jamalam360.reaping.registry.ModStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ReaperItem extends Item {
    private final ToolMaterial material;
    private final float sharpnessModifier;

    public ReaperItem(ToolMaterial material, Properties properties, float sharpnessModifier) {
        super(properties
              .stacksTo(1)
                .durability(material.durability())
              .component(DataComponents.TOOL, createToolProperties())
                .attributes(createAttributes(material))
        );
        this.material = material;
        this.sharpnessModifier = sharpnessModifier;
    }

    private static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2, false);
    }

    private static ItemAttributeModifiers createAttributes(ToolMaterial material) {
        float damage;
        if (material == ToolMaterial.IRON) {
            damage = 3.4f;
        } else if (material == ToolMaterial.GOLD) {
            damage = 4.3f;
        } else if (material == ToolMaterial.DIAMOND) {
            damage = 5.2f;
        } else if (material == ToolMaterial.NETHERITE) {
            damage = 6.8f;
        } else {
            throw new IllegalArgumentException("Invalid Reaper tool material: " + material);
        }

        return ItemAttributeModifiers.builder()
              .add(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND
              )
              .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.8f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
              .build();
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, @NonNull Player player, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (Reaper.reapEntity(player, entity, stack) == InteractionResult.SUCCESS) {
            player.getItemInHand(hand).hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            player.awardStat(ModStats.USE_REAPER_TOOL_STAT.get(), 1);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public float getSharpnessModifier() {
        return this.sharpnessModifier;
    }

    public int getCooldownTicks() {
        if (material == ToolMaterial.IRON) {
            return 45;
        } else if (material == ToolMaterial.GOLD) {
            return 18;
        } else if (material == ToolMaterial.DIAMOND) {
            return 28;
        } else if (material == ToolMaterial.NETHERITE) {
            return 23;
        } else {
            throw new IllegalArgumentException("Invalid Reaper tool material: " + material);
        }
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
                return Reaper.reapEntity(null, livingEntity, stack) == InteractionResult.SUCCESS;
            }

            return false;
        }

        @Override
        protected @NonNull ItemStack execute(BlockSource source, @NonNull ItemStack stack) {
            BlockPos blockPos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            this.setSuccess(tryReapEntity(source.level(), blockPos, stack));

            if (this.isSuccess()) {
                stack.hurtAndBreak(1, source.level(), null, (ignored) -> {});
            }

            return stack;
        }
    }
}
