package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ObeliskEntity extends Entity {

  private static final TrackedData<Boolean> HIDDEN = DataTracker.registerData(ObeliskEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private int activatedTicks = 1000;
  public final AnimationProperty bury = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);
  public final AnimationProperty glow = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

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
    if (this.world.isClient()) {
      this.clientTick();
    } else {
      this.serverTick();
    }
    this.activatedTicks++;
  }

  protected void clientTick() {
    if (this.age % 20 == 0) {
      this.bury.setupTransitionTo(this.isHidden() ? 50.0F : 0.0F, 40.0F);
    }
    if (this.bury.getProgress() > 0.0F && this.bury.getProgress() < 1.0F) {
      this.spawnParticles();
    }
    this.glow.setupTransitionTo(this.activatedTicks < 100 ? 1.0F : 0.0F, 10.0F);
  }

  protected void serverTick() {
    if (this.age % 20 == 0) {
      this.setHidden(isEntityPresent());
    }
    if (this.activatedTicks == 40 && !this.isEntityPresent()) {
      this.spawnEntity();
    }
  }

  protected void spawnParticles() {
    ParticleUtils.addInBox(
      (ClientWorld) this.world,
      new BlockStateParticleEffect(ParticleTypes.BLOCK, MineCellsBlocks.PRISON_COBBLESTONE.getDefaultState()),
      Box.of(this.getPos(), 2.5D, 0.25D, 2.0D),
      50,
      new Vec3d(-1.0D, 1.0D, -1.0D)
    );
  }

  @Override
  public ActionResult interact(PlayerEntity player, Hand hand) {
    if (this.isHidden() || this.activatedTicks < 100 || this.isEntityPresent()) {
      return ActionResult.FAIL;
    }
    ItemStack stack = player.getStackInHand(hand);
    if (stack.isOf(this.getActivationItem())) {
      this.activatedTicks = 0;
      stack.setCount(stack.getCount() - 1);
      return ActionResult.SUCCESS;
    }
    player.sendMessage(Text.translatable("chat.minecells.obelisk_item_message", Text.translatable(this.getActivationItem().getTranslationKey())), true);
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
    for (Entity e : this.world.getOtherEntities(this, this.getBox())) {
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
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }
}
