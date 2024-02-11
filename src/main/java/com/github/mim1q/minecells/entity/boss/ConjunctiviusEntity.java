package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.client.render.conjunctivius.ConjunctiviusEyeRenderer;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusBarrageGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusMoveAroundGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusTargetGoal;
import com.github.mim1q.minecells.registry.*;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ConjunctiviusEntity extends MineCellsBossEntity {

  public final AnimationProperty spikeOffset = new AnimationProperty(5.0F, MathUtils::easeInOutQuad);

  public static final TrackedData<Boolean> DASH_CHARGING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> DASH_RELEASING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> AURA_CHARGING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> AURA_RELEASING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> BARRAGE_ACTIVE = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<BlockPos> ANCHOR_TOP = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
  public static final TrackedData<BlockPos> ANCHOR_LEFT = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
  public static final TrackedData<BlockPos> ANCHOR_RIGHT = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
  public static final TrackedData<Integer> STAGE = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.INTEGER);

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
  }

  @Override
  protected void initGoals() {
    var auraGoal = new ConjunctiviusAuraGoal(this, s -> {
      s.cooldownGetter = () -> this.auraCooldown;
      s.cooldownSetter = (cooldown) -> this.auraCooldown = this.stageAdjustedCooldown(cooldown);
      s.stateSetter = this::switchAuraState;
      s.chargeSound = MineCellsSounds.SHOCKER_CHARGE;
      s.releaseSound = MineCellsSounds.SHOCKER_RELEASE;
      s.soundVolume = 2.0F;
      s.damage = 10.0F;
      s.radius = 8.0D;
      s.defaultCooldown = 200;
      s.actionTick = 30;
      s.chance = 0.05F;
      s.length = 60;
    });

    var dashGoal = (new ConjunctiviusDashGoal(this, s -> {
      s.cooldownGetter = () -> this.dashCooldown;
      s.cooldownSetter = (cooldown) -> this.dashCooldown = this.stageAdjustedCooldown(cooldown);
      s.stateSetter = this::switchDashState;
      s.chargeSound = MineCellsSounds.CONJUNCTIVIUS_DASH_CHARGE;
      s.releaseSound = MineCellsSounds.CONJUNCTIVIUS_DASH_RELEASE;
      s.soundVolume = 2.0F;
      s.speed = 1.0F;
      s.damage = 20.0F;
      s.defaultCooldown = 200;
      s.actionTick = 30;
      s.alignTick = 26;
      s.chance = 0.1F;
      s.length = 70;
      s.rotate = false;
      s.margin = 0.5D;
      s.particle = MineCellsParticles.SPECKLE.get(0xFF0000);
    }));

    this.goalSelector.add(2, dashGoal);
    this.goalSelector.add(9, auraGoal);
    this.goalSelector.add(10, new ConjunctiviusMoveAroundGoal(this));

    this.targetSelector.add(0, new ConjunctiviusTargetGoal(this));
  }

  public void addStageGoals(int stage) {
    if (stage == 3) {
      this.goalSelector.add(2, new ConjunctiviusBarrageGoal.Targeted(this, 0.15D, 0.1F));
      return;
    }
    if (stage == 7) {
      this.goalSelector.add(2, new ConjunctiviusBarrageGoal.Around(this, 0.15D, 0.02F));
    }
  }

  @Override
  public void tick() {
    this.bodyYaw = this.spawnRot;
    this.prevBodyYaw = this.spawnRot;
    this.setYaw(this.spawnRot);

    super.tick();

    if (getWorld().isClient()) {
      if (this.getDashState() != TimedActionGoal.State.IDLE || this.getAuraState() == TimedActionGoal.State.RELEASE) {
        this.spikeOffset.setupTransitionTo(0.0F, 10.0F);
      } else {
        this.spikeOffset.setupTransitionTo(5.0F, 40.0F);
      }
      this.spawnParticles();
    } else {
      for (Entity e : getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.25D))) {
        if (e instanceof LivingEntity livingEntity && !(e instanceof SewersTentacleEntity)) {
          this.tryAttack(livingEntity);
          this.knockback(livingEntity);
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
        this.bossBar.setVisible(closestPlayerNearby && playersInArea.size() > 0);

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
      }
      if (this.deathTime == 60) {
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

  public int stageAdjustedCooldown(int cooldown) {
    int stage = this.getStage();
    return switch (stage) {
      case 3 -> (cooldown * 4) / 5;
      case 5 -> (cooldown * 3) / 4;
      case 7 -> (cooldown * 2) / 3;
      default -> cooldown;
    };
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
      this.dataTracker.set(STAGE, stage);
      this.addStageGoals(stage);
    }
  }

  public ConjunctiviusEyeRenderer.EyeState getEyeState() {
    boolean stageBeginning = this.stageTicks > 0 && this.stageTicks < 30;
    if (stageBeginning || !this.isAlive()) {
      return ConjunctiviusEyeRenderer.EyeState.SHAKING;
    }
    if (this.dataTracker.get(BARRAGE_ACTIVE)) {
      return ConjunctiviusEyeRenderer.EyeState.GREEN;
    }
    if (this.getDashState() != TimedActionGoal.State.IDLE) {
      return ConjunctiviusEyeRenderer.EyeState.YELLOW;
    }
    return ConjunctiviusEyeRenderer.EyeState.PINK;
  }

  public static DefaultAttributeContainer.Builder createConjunctiviusAttributes() {
    return createHostileAttributes()
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
    nbt.putIntArray("roomBox", new int[]{
      this.roomBox.getMinX(), this.roomBox.getMinY(), this.roomBox.getMinZ(),
      this.roomBox.getMaxX(), this.roomBox.getMaxY(), this.roomBox.getMaxZ()
    });
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
    if (this.getStage() > 3) {
      this.addStageGoals(3);
    }
    this.stageTicks = nbt.getInt("stageTicks");
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource source) {
    return MineCellsSounds.CONJUNCTIVIUS_HIT;
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

  protected static class ConjunctiviusDashGoal extends TimedDashGoal<ConjunctiviusEntity> {

    public ConjunctiviusDashGoal(ConjunctiviusEntity entity, Consumer<TimedDashSettings> settings) {
      super(entity, settings, null);
    }

    @Override
    public boolean canStart() {
      return super.canStart()
        && this.entity.canAttack()
        && !this.entity.moving;
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
