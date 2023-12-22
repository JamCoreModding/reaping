package io.github.jamalam360.reaping;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import io.github.jamalam360.jamlib.JamLibPlatform;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.reaping.item.ReaperItem;
import io.github.jamalam360.reaping.pillager.ReapingPillager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class Reaping {
	public static final String MOD_ID = "reaping";
	public static final String MOD_NAME = "Reaping";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final ConfigManager<Config> CONFIG = new ConfigManager<>(MOD_ID, Config.class);

	public static void init() {
		LOGGER.info("Initializing Reaping on " + JamLibPlatform.getPlatform().name());
		Content.registerAll();
		EnvExecutor.runInEnv(Env.CLIENT, () -> ReapingClient::initClient);
	}

	public static InteractionResult reap(@Nullable LivingEntity attacker, LivingEntity target, ItemStack reaper) {
		int lootingLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, reaper);
		InteractionResult result = InteractionResult.PASS;

		if (target.level().isClientSide) {
			return InteractionResult.SUCCESS;
		}

		if (!target.isDeadOrDying() && !target.isBlocking()) {
			if (target instanceof Animal animal) {
				if (!target.isBaby()) {
					dropEntityStacks(attacker, target, reaper);
					animal.setBaby(true);
					target.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 1.0f);

					if (attacker != null) {
						if (attacker instanceof Player player) {
							target.hurt(target.damageSources().playerAttack(player), 1.0f);
						} else {
							target.hurt(target.damageSources().mobAttack(attacker), 1.0f);
						}
					} else {
						target.hurt(target.damageSources().generic(), 1.0f);
					}

					target.level().addFreshEntity(EntityType.EXPERIENCE_ORB.create(target.level()));
				} else {
					target.kill();
					target.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
					target.spawnAtLocation(new ItemStack(Items.BONE, lootingLvl == 0 ? 1 : target.level().random.nextInt(lootingLvl) + 1));
					target.level().addFreshEntity(EntityType.EXPERIENCE_ORB.create(target.level()));
					target.level().addFreshEntity(EntityType.EXPERIENCE_ORB.create(target.level()));
				}

				result = InteractionResult.SUCCESS;
			} else if (!target.hasEffect(Content.SHRINK.get()) && (target instanceof AbstractVillager || (target instanceof Player && CONFIG.get().allowReapingPlayers))) {
				target.spawnAtLocation(new ItemStack(Content.HUMANOID_MEAT.get(), lootingLvl == 0 ? 1 : target.level().random.nextInt(lootingLvl) + 1));
				target.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 1.0f);

				if (attacker != null) {
					if (attacker instanceof Player player) {
						target.hurt(target.damageSources().playerAttack(player), 1.0f);
					} else {
						target.hurt(target.damageSources().mobAttack(attacker), 1.0f);
					}
				} else {
					target.hurt(target.damageSources().generic(), 1.0f);
				}

				// 30 Seconds
				target.addEffect(new MobEffectInstance(Content.SHRINK.get(), 30 * 20));
			}
		}

		if (result == InteractionResult.SUCCESS) {
			double chance = 0.45D;

			if (reaper.getItem() instanceof ReaperItem reaperItem) {
				chance *= reaperItem.getSharpnessModifier();

				int bluntnessLevel = EnchantmentHelper.getItemEnchantmentLevel(Content.CURSE_OF_BLUNTNESS.get(), reaper);

				if (bluntnessLevel > 0) {
					chance += 0.4D;
				}

				int sharpnessLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, reaper);

				if (sharpnessLevel > 0) {
					chance -= 0.1D * sharpnessLevel;
				}

				chance = Math.max(0.0D, Math.min(1.0D, chance));

				if (attacker instanceof Player player) {
					player.getCooldowns().addCooldown(reaperItem, reaperItem.getCooldownTicks());
				}
			}

			if (attacker.level().random.nextDouble() <= chance) {
				target.kill();
			}
		}

		return result;
	}

	private static void dropEntityStacks(@Nullable LivingEntity attacker, LivingEntity target, ItemStack stack) {
		if (!target.level().isClientSide) {
			LootTable table = target.level().getServer().getLootData().getLootTable(target.getLootTable());
			DamageSource source;

			if (attacker != null) {
				if (attacker instanceof Player player) {
					source = target.damageSources().playerAttack(player);
				} else {
					source = target.damageSources().mobAttack(attacker);
				}
			} else {
				source = target.damageSources().generic();
			}

			LootParams.Builder builder = (new LootParams.Builder((ServerLevel) target.level())).withParameter(LootContextParams.THIS_ENTITY, target).withParameter(LootContextParams.ORIGIN, target.position()).withParameter(LootContextParams.DAMAGE_SOURCE, source).withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, source.getDirectEntity());

			if (attacker instanceof Player player) {
				builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
			}

			int lootingLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, stack);
			int rollTimes = lootingLvl == 0 ? 1 : target.level().random.nextInt(lootingLvl) + 1;

			for (int i = 0; i < rollTimes; i++) {
				table.getRandomItems(builder.create(LootContextParamSets.ENTITY), target.getLootTableSeed(), target::spawnAtLocation);
			}
		}
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
