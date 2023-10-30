package io.github.jamalam360.reaping.pillager;

import com.google.common.collect.Maps;
import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.reaping.Content;
import io.github.jamalam360.reaping.Reaping;
import io.github.jamalam360.reaping.item.ReaperItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReapingPillager extends AbstractIllager implements InventoryCarrier {
	private final SimpleContainer inventory = new SimpleContainer(5);
	private int cooldown = 0;

	public ReapingPillager(EntityType<ReapingPillager> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createReapingPillagerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.42F)
				.add(Attributes.MAX_HEALTH, 26.0)
				.add(Attributes.ATTACK_DAMAGE, 6.0)
				.add(Attributes.FOLLOW_RANGE, 32.0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		this.writeInventoryToTag(compoundTag);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.readInventoryFromTag(compoundTag);
		this.setCanPickUpLoot(true);
	}

	@Override
	public AbstractIllager.@NotNull IllagerArmPose getArmPose() {
		return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
	}

	@Override
	public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
		return 0.0F;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(
			ServerLevelAccessor serverLevelAccessor,
			DifficultyInstance difficultyInstance,
			MobSpawnType mobSpawnType,
			@Nullable SpawnGroupData spawnGroupData,
			@Nullable CompoundTag compoundTag
	) {
		RandomSource randomSource = serverLevelAccessor.getRandom();
		this.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
		this.populateDefaultEquipmentEnchantments(randomSource, difficultyInstance);
		return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
		Item item = randomSource.nextDouble() > 0.2F ? Content.IRON_REAPER.get() : Content.GOLD_REAPER.get();
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(item));
	}

	@Override
	protected void enchantSpawnedWeapon(RandomSource randomSource, float f) {
		super.enchantSpawnedWeapon(randomSource, f);
		if (randomSource.nextInt(300) == 0) {
			ItemStack stack = this.getMainHandItem();
			if (stack.getItem() instanceof ReaperItem) {
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
				map.putIfAbsent(Enchantments.SHARPNESS, 1);
				EnchantmentHelper.setEnchantments(map, stack);
				this.setItemSlot(EquipmentSlot.MAINHAND, stack);
			}
		}
	}

	@Override
	public boolean isAlliedTo(Entity entity) {
		if (super.isAlliedTo(entity)) {
			return true;
		} else if (entity instanceof LivingEntity && ((LivingEntity) entity).getMobType() == MobType.ILLAGER) {
			return this.getTeam() == null && entity.getTeam() == null;
		} else {
			return false;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.PILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.PILLAGER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.PILLAGER_HURT;
	}

	@Override
	public @NotNull SimpleContainer getInventory() {
		return this.inventory;
	}

	@Override
	protected void pickUpItem(ItemEntity item) {
		ItemStack stack = item.getItem();
		if (stack.getItem() instanceof BannerItem) {
			super.pickUpItem(item);
		} else if (this.wantsItem(stack)) {
			this.onItemPickup(item);
			ItemStack itemStack2 = this.inventory.addItem(stack);
			if (itemStack2.isEmpty()) {
				item.discard();
			} else {
				stack.setCount(itemStack2.getCount());
			}
		}
	}

	private boolean wantsItem(ItemStack itemStack) {
		return this.hasActiveRaid() && itemStack.is(Items.WHITE_BANNER);
	}

	@Override
	public @NotNull SlotAccess getSlot(int i) {
		int j = i - 300;
		return j >= 0 && j < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, j) : super.getSlot(i);
	}

	@Override
	public void applyRaidBuffs(int i, boolean bl) {
		Raid raid = this.getCurrentRaid();
		boolean bl2 = this.random.nextFloat() <= raid.getEnchantOdds();
		if (bl2) {
			ItemStack itemStack = new ItemStack(Items.CROSSBOW);
			Map<Enchantment, Integer> map = Maps.newHashMap();
			if (i > raid.getNumGroups(Difficulty.NORMAL)) {
				map.put(Enchantments.SHARPNESS, 2);
			} else if (i > raid.getNumGroups(Difficulty.EASY)) {
				map.put(Enchantments.SHARPNESS, 1);
			}

			EnchantmentHelper.setEnchantments(map, itemStack);
			this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
		}
	}

	@Override
	public @NotNull SoundEvent getCelebrateSound() {
		return SoundEvents.PILLAGER_CELEBRATE;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.cooldown > 0) {
			this.cooldown--;
		}
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (!(this.getMainHandItem().getItem() instanceof ReaperItem)
				|| !(target instanceof LivingEntity) || cooldown > 0) return super.doHurtTarget(target);

		float g = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + (float) EnchantmentHelper.getKnockbackBonus(this);

		int i = EnchantmentHelper.getFireAspect(this);
		if (i > 0) {
			target.setSecondsOnFire(i * 4);
		}

		InteractionResult result = Reaping.reap(this, (LivingEntity) target, this.getMainHandItem());
		if (result == InteractionResult.SUCCESS) {
			if (g > 0.0F) {
				((LivingEntity) target)
						.knockback(
								g * 0.5F,
								Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
								-Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))
						);
				this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
			}

			this.doEnchantDamageEffects(this, target);
			this.swing(InteractionHand.MAIN_HAND);
			this.cooldown = ((ReaperItem) this.getMainHandItem().getItem()).getCooldownTicks() + 60;
		} else if (result == InteractionResult.PASS) {
			return super.doHurtTarget(target);
		}

		return result == InteractionResult.SUCCESS;
	}

	@Override
	public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkManager.createAddEntityPacket(this);
	}
}
