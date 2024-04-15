package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusBarrageGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusMoveAroundGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusTargetGoal;
import com.github.mim1q.minecells.registry.*;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.client.ClientUtil;
import dev.mim1q.gimm1q.interpolation.AnimatedProperty;
import dev.mim1q.gimm1q.interpolation.AnimatedProperty.EasingFunction;
import dev.mim1q.gimm1q.interpolation.Easing;
import dev.mim1q.gimm1q.interpolation.EasingUtils;
import dev.mim1q.gimm1q.screenshake.ScreenShakeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity.EyeState.SHAKING;
import static net.minecraft.entity.data.DataTracker.registerData;
import static net.minecraft.entity.data.TrackedDataHandlerRegistry.*;

public class ConjunctiviusEntity extends MineCellsBossEntity {

  public final AnimationProperty spikeOffset = new AnimationProperty(5.0F, MathUtils::easeInOutQuad);

  public static final TrackedData<Boolean> DASH_CHARGING = registerData(ConjunctiviusEntity.class, BOOLEAN);
  public static final TrackedData<Boolean> DASH_RELEASING = registerData(ConjunctiviusEntity.class, BOOLEAN);
  public static final TrackedData<Boolean> AURA_CHARGING = registerData(ConjunctiviusEntity.class, BOOLEAN);
  public static final TrackedData<Boolean> AURA_RELEASING = registerData(ConjunctiviusEntity.class, BOOLEAN);
  public static final TrackedData<Boolean> BARRAGE_ACTIVE = registerData(ConjunctiviusEntity.class, BOOLEAN);
  public static final TrackedData<BlockPos> ANCHOR_TOP = registerData(ConjunctiviusEntity.class, BLOCK_POS);
  public static final TrackedData<BlockPos> ANCHOR_LEFT = registerData(ConjunctiviusEntity.class, BLOCK_POS);
  public static final TrackedData<BlockPos> ANCHOR_RIGHT = registerData(ConjunctiviusEntity.class, BLOCK_POS);
  public static final TrackedData<Integer> STAGE = registerData(ConjunctiviusEntity.class, INTEGER);
  public static final TrackedData<Integer> TARGET_ID = registerData(ConjunctiviusEntity.class, INTEGER);
  public static final TrackedData<Vector3f> DASH_TARGET = registerData(ConjunctiviusEntity.class, VECTOR3F);

  // Stages:
  // 0 - has not seen any player yet
  // 1 - first full phase
  // 2 - first transition phase
  // 3 - second full phase
  // 4 - second transition phase
  // 5 - third full phase
  // 6 - third transition phase
  // 7 - fourth full phase

  private Vec3d spawnPos = Vec3d.ZERO;
  private Direction direction;
  private float spawnRot = 180.0F;
  private BlockBox roomBox = null;
  private int dashCooldown = 0;
  private int auraCooldown = 0;
  public int barrageCooldown = 0;
  public boolean moving = false;
  private int stageTicks = 1;
  private int lastStage = 0;

  private final HashMap<LivingEntity, Integer> hitEntities = new HashMap<>();

  private EasingFunction eyeEasing = Easing::lerp;
  private Vec3d eyeOffset = Vec3d.ZERO;
  private Vec3d lastEyeOffset = Vec3d.ZERO;

  private int blinkTimer = 0;
  private final AnimatedProperty eyeBlink = new AnimatedProperty(0.0F, MathUtils::lerp);

  private int lastTargetId = -1;

  public ConjunctiviusEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new ConjunctiviusMoveControl(this);
    this.navigation = new BirdNavigation(this, getWorld());
    this.setNoGravity(true);
    this.ignoreCameraFrustum = true;
    this.experiencePoints = 5000;
    this.noClip = true;
    this.setRotation(180.0F, 0.0F);
    this.bodyYaw = 180.0F;
    this.prevBodyYaw = 180.0F;
    this.prevYaw = 180.0F;
    this.headYaw = 180.0F;
    this.prevHeadYaw = 180.0F;
    this.bossBar.setVisible(false);
  }

  @Nullable
  @Override
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    this.spawnPos = Vec3d.ofCenter(this.getBlockPos());
    this.roomBox = this.createBox();
    this.direction = Direction.NORTH;
    this.spawnRot = this.direction.asRotation();
    BlockPos topAnchor = this.getBlockPos().add(0, 9, 0);
    BlockPos leftAnchor = this.getBlockPos().add(11, 0, 0);
    BlockPos rightAnchor = this.getBlockPos().add(-11, 0, 0);
    this.setAnchors(topAnchor, leftAnchor, rightAnchor);
    return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
  }

  protected BlockBox createBox() {
    BlockPos startPos = this.getBlockPos();
    int minX = startPos.getX() - 11;
    int maxX = startPos.getX() + 11;
    int minY = startPos.getY() - 10;
    int maxY = startPos.getY() + 9;
    int minZ = startPos.getZ() - 25;
    int maxZ = startPos.getZ() + 2;
    return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(DASH_RELEASING, false);
    this.dataTracker.startTracking(DASH_CHARGING, false);
    this.dataTracker.startTracking(AURA_RELEASING, false);
    this.dataTracker.startTracking(AURA_CHARGING, false);
    this.dataTracker.startTracking(BARRAGE_ACTIVE, false);
    this.dataTracker.startTracking(ANCHOR_TOP, this.getBlockPos());
    this.dataTracker.startTracking(ANCHOR_LEFT, this.getBlockPos());
    this.dataTracker.startTracking(ANCHOR_RIGHT, this.getBlockPos());
    this.dataTracker.startTracking(STAGE, 0);
    this.dataTracker.startTracking(TARGET_ID, -1);
    this.dataTracker.startTracking(DASH_TARGET, new Vector3f(0.0F, 0.0F, 0.0F));
  }

  @Override
  protected void initGoals() {
    this.goalSelector.clear(it -> true);
    this.targetSelector.clear(it -> true);

    var auraGoal = new ConjunctiviusAuraGoal(this, s -> {
      s.cooldownGetter = () -> this.auraCooldown;
      s.cooldownSetter = (cooldown) -> this.auraCooldown = cooldown;
      s.stateSetter = this::switchAuraState;
      s.chargeSound = MineCellsSounds.SHOCKER_CHARGE;
      s.releaseSound = MineCellsSounds.SHOCKER_RELEASE;
      s.soundVolume = 2.0F;
      s.damage = getDamage(1f);
      s.radius = 8.0D;
      s.defaultCooldown = 400;
      s.actionTick = 30;
      s.chance = 0.05F;
      s.length = 60;
    });

    var dashGoal = (new ConjunctiviusDashGoal(this, s -> {
      s.cooldownGetter = () -> this.dashCooldown;
      s.cooldownSetter = (cooldown) -> this.dashCooldown = cooldown;
      s.stateSetter = this::switchDashState;
      s.chargeSound = MineCellsSounds.CONJUNCTIVIUS_DASH_CHARGE;
      s.releaseSound = MineCellsSounds.CONJUNCTIVIUS_DASH_RELEASE;
      s.soundVolume = 2.0F;
      s.defaultCooldown = getStageAdjustedValue(300, 250, 200, 150);
      s.actionTick = getStageAdjustedValue(40, 35, 30, 30);
      s.chance = 0.1F;
      s.length = getStageAdjustedValue(105, 90, 83, 65);
    }, getStageAdjustedValue(50, 45, 40, 30)));

    this.goalSelector.add(4, dashGoal);
    this.goalSelector.add(9, auraGoal);
    this.goalSelector.add(10, new ConjunctiviusMoveAroundGoal(this));
    addStageGoals(getStage());

    this.targetSelector.add(0, new ConjunctiviusTargetGoal(this));
    this.targetSelector.add(0, new ActiveTargetGoal<>(this, PigEntity.class, false));
  }

  public void addStageGoals(int stage) {
    if (stage >= 3) {
      this.goalSelector.add(2, new ConjunctiviusBarrageGoal.Targeted(this, s -> {
        s.chance = 0.2F;
        s.length = getStageAdjustedValue(60, 80, 100, 120);
        s.interval = getStageAdjustedValue(8, 6, 5, 4);
        s.cooldown = getStageAdjustedValue(20 * 16, 20 * 14, 20 * 12, 20 * 10);
      }));
      this.goalSelector.add(2, new ConjunctiviusBarrageGoal.Around(this, s -> {
        s.chance = 0.05F;
        s.length = getStageAdjustedValue(20, 26, 32, 37);
        s.interval = getStageAdjustedValue(8, 6, 5, 4);
        s.cooldown = 40;
        s.speed = 0.1f;
        s.count = () -> random.nextBetween(2, 5);
        s.minPause = getStageAdjustedValue(10, 8, 6, 4);
        s.maxPause = getStageAdjustedValue(20, 16, 12, 8);
      }));
    }
  }

  @Override
  public void tick() {
    this.bodyYaw = this.spawnRot;
    this.prevBodyYaw = this.spawnRot;
    this.setYaw(this.spawnRot);

    super.tick();

    if (getWorld().isClient()) {
      calculateEyeOffset();
      if (this.getDashState() != TimedActionGoal.State.IDLE || this.getAuraState() == TimedActionGoal.State.RELEASE) {
        this.spikeOffset.setupTransitionTo(0.0F, 10.0F, Easing::easeInQuad);
      } else {
        this.spikeOffset.setupTransitionTo(5.0F, 30.0F, Easing::easeInOutQuad);
      }
      this.spawnParticles();

      var blinkTime = this.getBlinkTicks();
      if (blinkTime > 0) {
        this.blinkTimer = Math.max(blinkTime, this.blinkTimer);
      } else {
        this.blinkTimer = Math.max(0, this.blinkTimer - 1);
      }

      if (this.blinkTimer > 0) {
        this.eyeBlink.transitionTo(4.0F, 1.5F);
      } else {
        this.eyeBlink.transitionTo(0.0F, 4.0F);
      }

    } else {
      for (Entity e : getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.2D))) {
        if (e instanceof LivingEntity livingEntity && !(e instanceof SewersTentacleEntity)) {
          var lastHit = this.hitEntities.getOrDefault(livingEntity, 0);

          if (age - lastHit < 40) continue;

          livingEntity.damage(getWorld().getDamageSources().mobAttack(this), getDamage(0.5f));
          this.hitEntities.put(livingEntity, age);
        }
      }
      BlockPos.iterateOutwards(this.getBlockPos(), 3, 4, 3).forEach((blockPos) -> {
        if (getWorld().getBlockState(blockPos).isIn(MineCellsBlockTags.CONJUNCTIVIUS_BREAKABLE)) {
          getWorld().breakBlock(blockPos, true);
        }
      });

      if (!getWorld().getBlockState(this.getBlockPos().down(2)).isAir()) {
        this.move(MovementType.SELF, new Vec3d(0.0D, 0.1D, 0.0D));
      }

      if (this.age % 20 == 0) {
        // Handle bossbar visibility
        boolean closestPlayerNearby = getWorld().getClosestPlayer(this, 32.0D) != null;
        List<PlayerEntity> playersInArea = getWorld().getPlayers(TargetPredicate.DEFAULT, this, Box.from(this.roomBox).expand(2.0D));
        this.bossBar.setVisible(closestPlayerNearby && !playersInArea.isEmpty());

        this.switchStages(this.getStage());

        if (!this.isInFullStage()) {
          var tentacles = getWorld().getEntitiesByClass(SewersTentacleEntity.class, Box.from(roomBox.expand(10)), Objects::nonNull);
          if (this.stageTicks > 30 && tentacles.isEmpty() && this.getStage() != 0) {
            this.setStage(this.getStage() + 1);
          } else if (this.getStage() != 0) {
            this.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.PROTECTED, 30, 0, false, false));
          }
        }
      }
    }

    int stage = this.getStage();
    if (stage != this.lastStage) {
      this.stageTicks = 0;
    }
    this.lastStage = stage;
    this.stageTicks++;
  }

  private void calculateEyeOffset() {
    this.lastEyeOffset = this.eyeOffset;

    Vec3d targetPos = ClientUtil.getClientCameraPos();

    var targetId = this.dataTracker.get(TARGET_ID);

    if (targetId != -1) {
      var entity = getWorld().getEntityById(targetId);
      if (entity != null) targetPos = entity.getPos();
    }

    Vec3d entityPos = this.getPos().add(0.0D, 2.5D, 0.0D);
    Vec3d diff = targetPos.subtract(entityPos);
    float rotation = this.bodyYaw;

    Vec3d rotatedDiff = MathUtils.vectorRotateY(diff, rotation * MathHelper.RADIANS_PER_DEGREE + MathHelper.HALF_PI);
    float xOffset = (float) -rotatedDiff.x;
    float yOffset = (float) -rotatedDiff.y;
    float distance = 1.0F - ((float) rotatedDiff.z - 2.5F) / 30.0F;
    distance = MathHelper.clamp(distance, 0.25F, 1.0F);

    xOffset *= distance * 0.5F;
    yOffset *= distance * 0.5F;

    if (getEyeState() == SHAKING) {
      xOffset += (this.random.nextFloat() - 0.5F) * 2.0F;
      yOffset += (this.random.nextFloat() - 0.5F) * 2.0F;
      this.eyeEasing = Easing::easeOutBack;
    } else {
      this.eyeEasing = Easing::lerp;
    }

    xOffset = MathHelper.clamp(xOffset, -6F, 6F);
    yOffset = MathHelper.clamp(yOffset, -4F, 4F);

    this.eyeOffset = new Vec3d(xOffset, yOffset, 0.0D);
  }

  public Vec3d getEyeOffset(float tickDelta) {
    return EasingUtils.interpolateVec(this.lastEyeOffset, this.eyeOffset, tickDelta, this.eyeEasing);
  }

  private int getBlinkTicks() {
    var targetId = this.dataTracker.get(TARGET_ID);
    if (targetId != lastTargetId) {
      lastTargetId = targetId;
      return 4;
    }

    if (hurtTime == maxHurtTime - 1)  return 3;
    if (this.age % (20 * 20) == 0) return 5;
    if (this.deathTime > 40 || this.getStage() == 0) return 1;
    return 0;
  }

  public int getEyelidFrame(float progress) {
    return (int) this.eyeBlink.update(progress);
  }

  @Override
  public void setTarget(@Nullable LivingEntity target) {
    super.setTarget(target);
    this.dataTracker.set(TARGET_ID, target == null ? -1 : target.getId());
  }

  @Override
  protected void updatePostDeath() {
    if (getWorld().isClient()) {
      int interval = this.deathTime >= 55 ? 1 : 10;
      if (this.deathTime % interval == 0) {
        getWorld().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 2.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
      }
    } else {
      if (this.deathTime == 1) {
        this.playSound(MineCellsSounds.CONJUNCTIVIUS_DYING, 2.0F, 1.0F);
        ScreenShakeUtils.shakeAround(
          (ServerWorld) getWorld(),
          this.getPos(),
          0.5f,
          80,
          30,
          40,
          "minecells:conjunctivius_death"
        );
      }
      if (this.deathTime == 60) {
        ScreenShakeUtils.shakeAround(
          (ServerWorld) getWorld(),
          this.getPos(),
          1f,
          40,
          30,
          40,
          "minecells:conjunctivius_death"
        );

        this.playSound(MineCellsSounds.CONJUNCTIVIUS_DEATH, 2.0F, 1.0F);
        this.remove(RemovalReason.KILLED);
      }
    }
    this.deathTime++;
  }

  @Override
  public void onDeath(DamageSource damageSource) {
    super.onDeath(damageSource);
    if (getWorld().isClient || getWorld().getServer() == null) {
      return;
    }

    var server = getWorld().getServer();
    var advancement = server.getAdvancementLoader().get(MineCells.createId("conjunctivius"));

    if (advancement == null) {
      return;
    }

    getWorld().getPlayers(TargetPredicate.DEFAULT, this, Box.from(this.getRoomBox().expand(10))).forEach(
      player -> ((ServerPlayerEntity) player).getAdvancementTracker().grantCriterion(advancement, "conjunctivius_killed")
    );
  }

  protected void switchStages(int stage) {
    if (stage == 0 && this.getTarget() != null) {
      this.setStage(1);
      return;
    }
    float healthPercent = this.getHealth() / this.getMaxHealth();
    if (stage == 1 && healthPercent <= 0.8F) {
      this.spawnTentacles();
      this.setStage(2);
      return;
    }
    if (stage == 3 && healthPercent <= 0.55F) {
      this.spawnTentacles();
      this.setStage(4);
      return;
    }
    if (stage == 5 && healthPercent <= 0.3F) {
      this.spawnTentacles();
      this.setStage(6);
    }
  }

  protected void spawnTentacles() {
    int playerAmount = getWorld().getPlayers(TargetPredicate.DEFAULT, this, Box.from(this.roomBox).expand(4.0D)).size();
    for (int i = 0; i < 2 + 2 * playerAmount; i++) {
      SewersTentacleEntity tentacle = MineCellsEntities.SEWERS_TENTACLE.create(getWorld());
      if (tentacle != null) {
        tentacle.setVariant(this.getStage() == 1 ? 0 : this.getStage() == 3 ? 1 : 2);
        tentacle.setPosition(this.getTentaclePos());
        tentacle.setSpawnedByBoss(true);
        getWorld().spawnEntity(tentacle);
      }
    }
  }

  private Vec3d getTentaclePos() {
    BlockPos center = this.roomBox.getCenter().add(
      this.random.nextInt(16) - 8,
      0,
      this.random.nextInt(16) - 8
    );
    return Vec3d.ofCenter(center);
  }

  protected void spawnParticles() {
    Vec3d pos = this.getPos().add(0.0D, this.getHeight() * 0.5D, 0.0D);
    if (this.getAuraState() == TimedActionGoal.State.CHARGE) {
      ParticleUtils.addAura((ClientWorld) getWorld(), pos, MineCellsParticles.AURA, 2, 7.5D, -0.01D);
    } else if (this.getAuraState() == TimedActionGoal.State.RELEASE) {
      ParticleUtils.addAura((ClientWorld) getWorld(), pos, MineCellsParticles.AURA, 50, 7.0D, 0.01D);
      ParticleUtils.addAura((ClientWorld) getWorld(), pos, MineCellsParticles.AURA, 10, 1.0D, 0.5D);
    }

    if ((this.getEyeState() == SHAKING || this.age % 5 == 0) && random.nextFloat() < 0.33f) {
      ParticleUtils.addInBox(
        (ClientWorld) getWorld(),
        ParticleTypes.FALLING_WATER,
        Box.of(getPos().add(0.0, 0.25, 0.0), 2.0, 0.5, 2.0),
        this.getEyeState() == SHAKING ? 3 : 1,
        Vec3d.ZERO
      );
    }

    int stage = this.getStage();
    if (this.isInFullStage() || stage == 0) {
      return;
    }
    if (this.stageTicks == 1) {
      Vec3d offset = switch (stage) {
        case 2 -> new Vec3d(2.2D, -0.85D, 0.0D);
        case 4 -> new Vec3d(-2.2D, -0.85D, 0.0D);
        default -> new Vec3d(0.0D, 1.75D, 0.0D);
      };
      Vec3d target = switch (stage) {
        case 2 -> Vec3d.ofCenter(this.getRightAnchor());
        case 4 -> Vec3d.ofCenter(this.getLeftAnchor());
        default -> Vec3d.ofCenter(this.getTopAnchor());
      };
      this.spawnChainBreakingParticles(offset, target);
    }
  }

  private void spawnChainBreakingParticles(Vec3d offset, Vec3d target) {
    ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, MineCellsBlocks.BIG_CHAIN.getDefaultState());
    Vec3d diff = target.subtract(this.getPos().add(offset));
    double length = diff.length();
    for (int i = 0; i < length; i++) {
      Vec3d pos = this.getPos().add(offset).add(diff.multiply(i / length));
      getWorld().addParticle(particle, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
    }
  }

  @Override
  protected void decrementCooldowns() {
    this.dashCooldown = Math.max(0, this.dashCooldown - 1);
    this.auraCooldown = Math.max(0, this.auraCooldown - 1);
    this.barrageCooldown = Math.max(0, this.barrageCooldown - 1);
  }

  @Override
  public boolean damage(DamageSource source, float amount) {
    if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
      return super.damage(source, amount * 0.5F);
    }
    return super.damage(source, amount);
  }

  protected void switchDashState(TimedActionGoal.State state, boolean value) {
    switch (state) {
      case CHARGE -> this.dataTracker.set(DASH_CHARGING, value);
      case RELEASE -> this.dataTracker.set(DASH_RELEASING, value);
    }
  }

  protected void switchAuraState(TimedActionGoal.State state, boolean value) {
    switch (state) {
      case CHARGE -> this.dataTracker.set(AURA_CHARGING, value);
      case RELEASE -> this.dataTracker.set(AURA_RELEASING, value);
    }
  }

  public TimedActionGoal.State getDashState() {
    return this.getStateFromTrackedData(DASH_CHARGING, DASH_RELEASING);
  }

  public TimedActionGoal.State getAuraState() {
    return this.getStateFromTrackedData(AURA_CHARGING, AURA_RELEASING);
  }

  public void setAnchors(BlockPos top, BlockPos left, BlockPos right) {
    this.dataTracker.set(ANCHOR_TOP, top);
    this.dataTracker.set(ANCHOR_LEFT, left);
    this.dataTracker.set(ANCHOR_RIGHT, right);
  }

  public BlockPos getTopAnchor() {
    return this.dataTracker.get(ANCHOR_TOP);
  }

  public BlockPos getLeftAnchor() {
    return this.dataTracker.get(ANCHOR_LEFT);
  }

  public BlockPos getRightAnchor() {
    return this.dataTracker.get(ANCHOR_RIGHT);
  }

  public int getStage() {
    return this.dataTracker.get(STAGE);
  }

  public boolean isInFullStage() {
    int stage = this.getStage();
    return stage == 1 || stage == 3 || stage == 5 || stage == 7;
  }

  public boolean canAttack() {
    return this.isInFullStage() && this.stageTicks > 40;
  }

  public void setStage(int stage) {
    if (stage != this.getStage()) {
      this.playSound(MineCellsSounds.CONJUNCTIVIUS_SHOUT, 2.0F, 1.0F);
      ScreenShakeUtils.shakeAround(
        (ServerWorld) getWorld(),
        this.getPos(),
        1f,
        50,
        20,
        40,
        "minecells:conjunctivius_roar"
      );
      this.dataTracker.set(STAGE, stage);
      this.initGoals();
    }
  }

  public EyeState getEyeState() {
    boolean stageBeginning = this.stageTicks > 0 && this.stageTicks < 30;
    if (stageBeginning || !this.isAlive()) {
      return SHAKING;
    }
    if (this.dataTracker.get(BARRAGE_ACTIVE)) {
      return EyeState.GREEN;
    }
    if (this.getDashState() != TimedActionGoal.State.IDLE) {
      return EyeState.YELLOW;
    }
    return EyeState.PINK;
  }

  public  <T> T getStageAdjustedValue(T stage1, T stage3, T stage5, T stage7) {
    int stage = this.getStage();
    return switch (stage) {
      case 3, 4 -> stage3;
      case 5, 6 -> stage5;
      case 7 -> stage7;
      default -> stage1;
    };
  }

  public static DefaultAttributeContainer.Builder createConjunctiviusAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D)
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 10.0D)
      .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 6.0D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
      .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 5.0D);
  }

  public BlockBox getRoomBox() {
    return this.roomBox;
  }

  public Vec3d getSpawnPos() {
    return this.spawnPos;
  }

  public Vec3d getDashTarget() {
    return new Vec3d(this.dataTracker.get(DASH_TARGET));
  }

  @Override
  public boolean isPushable() {
    return false;
  }

  public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
    return false;
  }

  protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
  }

  public void travel(Vec3d movementInput) {
    if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
      this.updateVelocity(0.02F, movementInput);
      this.move(MovementType.SELF, this.getVelocity());
      this.setVelocity(this.getVelocity().multiply(0.85D));
    }
  }

  @Override
  public void setPosition(double x, double y, double z) {
    BlockBox box = this.getRoomBox();
    if (box == null) {
      super.setPosition(x, y, z);
      return;
    }
    super.setPosition(
      MathHelper.clamp(x, box.getMinX(), box.getMaxX()),
      MathHelper.clamp(y, box.getMinY(), box.getMaxY()),
      MathHelper.clamp(z, box.getMinZ(), box.getMaxZ())
    );
  }

  public boolean isClimbing() {
    return false;
  }

  @Override
  public int getMaxHeadRotation() {
    return 360;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt("dashCooldown", this.dashCooldown);
    nbt.putIntArray("spawnPos", new int[]{
      (int) this.spawnPos.x, (int) this.spawnPos.y, (int) this.spawnPos.z
    });
    if (this.roomBox != null) {
      nbt.putIntArray("roomBox", new int[]{
        this.roomBox.getMinX(), this.roomBox.getMinY(), this.roomBox.getMinZ(),
        this.roomBox.getMaxX(), this.roomBox.getMaxY(), this.roomBox.getMaxZ()
      });
    }
    nbt.putFloat("spawnRot", this.spawnRot);
    nbt.putIntArray("anchors", new int[]{
      this.getTopAnchor().getX(), this.getTopAnchor().getY(), this.getTopAnchor().getZ(),
      this.getLeftAnchor().getX(), this.getLeftAnchor().getY(), this.getLeftAnchor().getZ(),
      this.getRightAnchor().getX(), this.getRightAnchor().getY(), this.getRightAnchor().getZ(),
    });
    nbt.putInt("stage", this.dataTracker.get(STAGE));
    nbt.putInt("stageTicks", this.stageTicks);
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.dashCooldown = nbt.getInt("dashCooldown");
    if (nbt.contains("spawnPos") && nbt.getIntArray("spawnPos").length == 3) {
      int[] posArray = nbt.getIntArray("spawnPos");
      this.spawnPos = new Vec3d(posArray[0], posArray[1], posArray[2]);
    }
    if (nbt.contains("roomBox") && nbt.getIntArray("roomBox").length == 6) {
      int[] boxArray = nbt.getIntArray("roomBox");
      this.roomBox = new BlockBox(boxArray[0], boxArray[1], boxArray[2], boxArray[3], boxArray[4], boxArray[5]);
    }
    this.spawnRot = nbt.getFloat("spawnRot");
    this.direction = Direction.fromRotation(this.spawnRot);
    if (nbt.contains("anchors") && nbt.getIntArray("anchors").length == 9) {
      int[] anchors = nbt.getIntArray("anchors");
      BlockPos anchorTop = new BlockPos(anchors[0], anchors[1], anchors[2]);
      BlockPos anchorLeft = new BlockPos(anchors[3], anchors[4], anchors[5]);
      BlockPos anchorRight = new BlockPos(anchors[6], anchors[7], anchors[8]);
      this.setAnchors(anchorTop, anchorLeft, anchorRight);
    }
    this.setStage(nbt.getInt("stage"));
    this.stageTicks = nbt.getInt("stageTicks");
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource source) {
    return MineCellsSounds.CONJUNCTIVIUS_HIT;
  }

  public enum EyeState {
    SHAKING(-1),
    PINK(0),
    YELLOW(1),
    GREEN(2),
    BLUE(3);

    public final int index;

    EyeState(int index) {
      this.index = index;
    }
  }

  protected static class ConjunctiviusMoveControl extends MoveControl {
    public ConjunctiviusMoveControl(ConjunctiviusEntity entity) {
      super(entity);
    }

    public void tick() {
      if (this.state == State.MOVE_TO) {
        Vec3d vec3d = new Vec3d(this.targetX - this.entity.getX(), this.targetY - this.entity.getY(), this.targetZ - this.entity.getZ());
        double d = vec3d.length();
        vec3d = vec3d.normalize();
        if (!this.willCollide(vec3d, MathHelper.ceil(d))) {
          this.state = State.WAIT;
        }
      }
    }

    private boolean willCollide(Vec3d direction, int steps) {
      Box box = this.entity.getBoundingBox().withMinY(this.entity.getY() - 1.0D);
      for (int i = 1; i < steps; ++i) {
        box = box.offset(direction);
        if (!this.entity.getWorld().isSpaceEmpty(this.entity, box)) {
          return false;
        }
      }
      return true;
    }
  }

  protected static class ConjunctiviusDashGoal extends TimedActionGoal<ConjunctiviusEntity> {
    private Vec3d startPos;
    private Vec3d targetPos;
    private final int restTime;

    public ConjunctiviusDashGoal(ConjunctiviusEntity entity, Consumer<TimedActionSettings> settings, int restTime) {
      super(entity, settings, it -> it.canAttack() && !it.moving && it.getTarget() != null);
      this.setControls(EnumSet.of(Control.MOVE));
      this.startPos = entity.getPos();
      this.targetPos = entity.getPos();
      this.restTime = restTime;
    }

    @Override
    public void start() {
      super.start();
      startPos = this.entity.getPos();
      var target = this.entity.getTarget();
      targetPos = target == null ? startPos : target.getPos();
      entity.getDataTracker().set(DASH_TARGET, new Vector3f((float) targetPos.x, (float) targetPos.y + 1, (float) targetPos.z));
    }

    @Override
    protected void release() {
      var delta = (this.ticks() - this.actionTick) / (float) (this.length - this.actionTick - restTime);
      var tickPos = EasingUtils.interpolateVec(startPos, targetPos, delta, Easing::easeInOutCubic);

      if (this.ticks() <= this.length - restTime + 2) {
        var entities = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(0.75));
        for (Entity e : entities) {
          if (e instanceof LivingEntity livingEntity) {
            livingEntity.damage(entity.getWorld().getDamageSources().mobAttack(entity), entity.getDamage(2f));
            entity.hitEntities.put(livingEntity, entity.age);
          }
        }
      }

      if (this.ticks() == this.length - restTime) {
        if (this.entity.getWorld() instanceof ServerWorld serverWorld) {
          serverWorld.spawnParticles(
            ParticleTypes.EXPLOSION_EMITTER,
            targetPos.x, targetPos.y, targetPos.z,
            2,
            2.0, 2.0, 2.0, 0.0
          );
          ScreenShakeUtils.shakeAround(
            serverWorld,
            tickPos,
            1f,
            30,
            20,
            40D,
            "minecells:conjunctivius_smash"
          );
          serverWorld.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 2.0F, 1.0F);
        }
      }

      this.entity.setPosition(tickPos);
    }

    @Override
    public void stop() {
      super.stop();
      if (this.entity.getSpawnPos() != null) {
        this.entity.setVelocity(this.entity.getSpawnPos().subtract(this.entity.getPos()).normalize());
      }
    }
  }

  protected static class ConjunctiviusAuraGoal extends TimedAuraGoal<ConjunctiviusEntity> {

    public ConjunctiviusAuraGoal(ConjunctiviusEntity entity, Consumer<TimedAuraSettings> settings) {
      super(entity, settings, null);
    }

    @Override
    public boolean canStart() {
      return super.canStart()
        && this.entity.canAttack()
        && !this.entity.moving
        && this.entity.dashCooldown > this.length
        && !this.entity.getWorld().getPlayers(TargetPredicate.DEFAULT, this.entity, this.entity.getBoundingBox().expand(6.0D)).isEmpty();
    }
  }
}
