package io.github.jamalam360.reaping.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.github.jamalam360.reaping.Content;
import io.github.jamalam360.reaping.Reaping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ReaperItem extends TieredItem implements Vanishable {
	private final Multimap<Attribute, AttributeModifier> modifiers;
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

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.8f, AttributeModifier.Operation.ADDITION));
		this.modifiers = builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.modifiers : super.getDefaultAttributeModifiers(slot);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (Reaping.reap(player, entity, stack) == InteractionResult.SUCCESS) {
			player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
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

			if (this.isSuccess() && stack.hurt(1, source.level().random, null)) {
				stack.setCount(0);
			}

			return stack;
		}
	}
}
