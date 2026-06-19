package io.github.jamalam360.reaping;

import io.github.jamalam360.reaping.content.item.ReaperItem;
import io.github.jamalam360.reaping.registry.ModEffects;
import io.github.jamalam360.reaping.registry.ModEnchantmentIds;
import io.github.jamalam360.reaping.registry.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Reaper {
	public static InteractionResult reapEntity(@Nullable LivingEntity attacker, LivingEntity target, ItemStack reaper) {
		if (target.isDeadOrDying() || target.isBlocking()) {
			return InteractionResult.PASS;
		}

		if (!(target.level() instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		}

		int lootingLvl = getEnchantmentLevel(Enchantments.LOOTING, target.level(), reaper);
		if (target instanceof Animal animal) {
			if (!target.isBaby()) {
				dropEntityStacks(attacker, target);
				animal.setBaby(true);
				target.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 1.0f);
				hurt(serverLevel, attacker, target);
				ExperienceOrb.award((ServerLevel) target.level(), target.position(), 1);
			} else {
				target.kill((ServerLevel) target.level());
				target.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
				target.spawnAtLocation((ServerLevel) target.level(), new ItemStack(Items.BONE, lootingLvl == 0 ? 1 : target.level().getRandom().nextInt(lootingLvl) + 1));
				ExperienceOrb.award((ServerLevel) target.level(), target.position(), 2);
			}

			finishReap(attacker, target, reaper, true);
		} else if (!target.hasEffect(Holder.direct(ModEffects.SHRINK.get())) && (target instanceof AbstractVillager || (target instanceof Player && Reaping.CONFIG.get().allowReapingPlayers))) {
			target.spawnAtLocation((ServerLevel) target.level(), new ItemStack(ModItems.HUMANOID_MEAT.get(), lootingLvl == 0 ? 1 : target.level().getRandom().nextInt(lootingLvl) + 1));
			target.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 1.0f);
			hurt(serverLevel, attacker, target);
			target.addEffect(new MobEffectInstance(Holder.direct(ModEffects.SHRINK.get()), 30 * 20));
			finishReap(attacker, target, reaper, false);
		}

		return InteractionResult.SUCCESS;
	}

	private static void finishReap(@Nullable LivingEntity attacker, LivingEntity target, ItemStack reaper, boolean eligibleForDeath) {
		double deathChance = 0.35D;

		if (reaper.getItem() instanceof ReaperItem reaperItem) {
			deathChance *= reaperItem.getSharpnessModifier();

			int bluntnessLevel = getEnchantmentLevel(ModEnchantmentIds.BLUNTNESS_CURSE, target.level(), reaper);

			if (bluntnessLevel > 0) {
				deathChance += 0.4D;
			}

			if (attacker instanceof Player player) {
				player.getCooldowns().addCooldown(reaper, reaperItem.getCooldownTicks());
			}
		}

		deathChance = Math.clamp(deathChance, 0.0D, 1.0D);
		if (eligibleForDeath && target.level().getRandom().nextDouble() <= deathChance) {
			target.kill((ServerLevel) target.level());
		}
	}

	private static void dropEntityStacks(@Nullable LivingEntity attacker, LivingEntity target) {
		Optional<ResourceKey<LootTable>> maybeLootTableKey = target.getLootTable();
		if (maybeLootTableKey.isEmpty()) {
			return;
		}

		LootTable table = target.level().getServer().reloadableRegistries().getLootTable(maybeLootTableKey.get());
		DamageSource source = getDamageSource(attacker, target);
		LootParams.Builder builder = new LootParams.Builder((ServerLevel) target.level())
				.withParameter(LootContextParams.THIS_ENTITY, target)
				.withParameter(LootContextParams.ORIGIN, target.position())
				.withParameter(LootContextParams.DAMAGE_SOURCE, source)
				.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity())
				.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity());

		if (attacker instanceof Player player) {
			builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
		}

		table.getRandomItems(builder.create(LootContextParamSets.ENTITY), target.getLootTableSeed(), s -> target.spawnAtLocation((ServerLevel) target.level(), s));
	}

	private static void hurt(ServerLevel level, @Nullable LivingEntity attacker, LivingEntity target) {
		target.hurtServer(level, getDamageSource(attacker, target), 1.0f);
	}

	private static DamageSource getDamageSource(@Nullable LivingEntity attacker, LivingEntity target) {
		if (attacker == null) {
			return target.damageSources().generic();
		} else if (attacker instanceof Player player) {
			return target.damageSources().playerAttack(player);
		} else {
			return target.damageSources().mobAttack(attacker);
		}
	}

	private static int getEnchantmentLevel(ResourceKey<Enchantment> enchantment, Level level, ItemStack stack) {
		Optional<Holder.Reference<Enchantment>> maybeHolder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(enchantment);
		return maybeHolder.map(r -> EnchantmentHelper.getItemEnchantmentLevel(r, stack)).orElse(0);
	}
}
