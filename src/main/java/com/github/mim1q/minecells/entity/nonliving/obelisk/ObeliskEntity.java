package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.network.s2c.ObeliskActivationS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.minecraft.predicate.entity.EntityPredicates.VALID_LIVING_ENTITY;

public abstract class ObeliskEntity extends Entity {
  private static final EntityDimensions HIDDEN_DIMENSIONS = EntityDimensions.changing(1.75F, 0.0F);

  private static final TrackedData<Boolean> HIDDEN = DataTracker.registerData(ObeliskEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private boolean wasHidden = true;
  private int activatedTicks = 1000;
  private int riseTicks = 1000;
  public final AnimationProperty bury = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);
  public final AnimationProperty glow = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);

  public ObeliskEntity(EntityType<?> type, World world) {
    super(type, world);
    this.intersectionChecked = true;
    this.ignoreCameraFrustum = true;
  }

  public abstract Item getActivationItem();
  public abstract EntityType<?> getEntityType();
  public abstract Box getBox();
  protected abstract void spawnEntity();

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient()) {
      this.clientTick();
    } else {
      this.serverTick();
    }
    this.activatedTicks++;
    this.riseTicks++;
  }

  protected void clientTick() {
    if (this.age % 20 == 0) {
      this.bury.setupTransitionTo(this.isHidden() ? 50.0F : 0.0F, 40.0F);
    }
    if (this.bury.getProgress() > 0.0F && this.bury.getProgress() < 1.0F) {
      this.spawnRiseParticles();
    }
    this.glow.setupTransitionTo(this.activatedTicks < 100 ? 1.0F : 0.0F, 10.0F);
    if (this.activatedTicks <= 40) {
      this.spawnActivationParticles(this.activatedTicks);
    }
  }

  protected void serverTick() {
    if (this.age % 20 == 0) {
      boolean hidden = isEntityPresent();
      this.setHidden(hidden);
      this.setPose(hidden ? EntityPose.SLEEPING : EntityPose.STANDING);
      if (hidden != this.wasHidden) {
        this.riseTicks = 0;
      }
      this.wasHidden = hidden;
    }
    if (this.age % 3 == 0 && this.riseTicks > 10 && this.riseTicks < 49) {
      this.playSound(SoundEvents.BLOCK_STONE_STEP, 1.0F, this.random.nextFloat() * 0.5F + 0.25F);
      this.playSound(SoundEvents.BLOCK_GRAVEL_HIT, 0.3F, this.random.nextFloat() * 0.5F + 0.5F);
    }
    if (this.activatedTicks == 40 && !this.isEntityPresent()) {
      this.spawnEntity();
    }
  }

  @Override
  public EntityDimensions getDimensions(EntityPose pose) {
    return pose == EntityPose.SLEEPING ? HIDDEN_DIMENSIONS : super.getDimensions(pose);
  }

  protected void spawnRiseParticles() {
    ParticleUtils.addInBox(
      (ClientWorld) getWorld(),
      new BlockStateParticleEffect(ParticleTypes.BLOCK, MineCellsBlocks.PRISON_COBBLESTONE.block.getDefaultState()),
      Box.of(this.getPos(), 2.5D, 0.25D, 2.0D),
      50,
      new Vec3d(-1.0D, 1.0D, -1.0D)
    );
  }

  public void resetActivatedTicks() {
    this.activatedTicks = 0;
  }

  protected void spawnActivationParticles(int activatedTicks) { }

  @Override
  public ActionResult interact(PlayerEntity user, Hand hand) {
    if (this.isHidden() || this.activatedTicks < 100 || this.isEntityPresent()) {
      return ActionResult.FAIL;
    }
    ItemStack stack = user.getStackInHand(hand);
    if (stack.isOf(this.getActivationItem())) {
      if (!getWorld().isClient) {
        this.playSound(MineCellsSounds.OBELISK, 1.0F, 1.0F);
        this.activatedTicks = 0;
        stack.setCount(stack.getCount() - 1);
        PlayerLookup.tracking(this).forEach((player) -> ServerPlayNetworking.send(player, ObeliskActivationS2CPacket.ID, new ObeliskActivationS2CPacket(this.getId())));
      }
      return ActionResult.SUCCESS;
    }
    user.sendMessage(Text.translatable("chat.minecells.obelisk_item_message", Text.translatable(this.getActivationItem().getTranslationKey())), true);
    return ActionResult.FAIL;
  }

  @Override
  public boolean damage(DamageSource source, float amount) {
    if (source.getName().equals("player") && source.getAttacker() instanceof PlayerEntity player) {
      this.interact(player, Hand.MAIN_HAND);
    }
    return false;
  }

  protected boolean isEntityPresent() {
    for (Entity e : getWorld().getOtherEntities(this, this.getBox(), VALID_LIVING_ENTITY)) {
      if (e.getType() == this.getEntityType()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
    return interact(player, hand);
  }

  @Override
  public boolean isCollidable() {
    return !this.isHidden();
  }

  @Override
  public boolean collidesWith(Entity other) {
    return !this.isHidden();
  }

  @Override
  public boolean canHit() {
    return true;
  }

  @Override
  protected void initDataTracker() {
    this.dataTracker.startTracking(HIDDEN, true);
  }

  public void setHidden(boolean hidden) {
    this.dataTracker.set(HIDDEN, hidden);
  }

  public boolean isHidden() {
    return this.dataTracker.get(HIDDEN);
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    this.setHidden(nbt.getBoolean("hidden"));
    if (nbt.contains("activatedTicks")) {
      this.activatedTicks = nbt.getInt("activatedTicks");
    }
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putBoolean("hidden", this.isHidden());
    nbt.putInt("activatedTicks", this.activatedTicks);
  }

  @Override
  public Packet<ClientPlayPacketListener> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }
}
