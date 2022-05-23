package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElevatorAssemblerBlock extends Block {
    public ElevatorAssemblerBlock(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean result = this.assemble(world, pos);
        return result ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    public boolean assemble(World world, BlockPos pos) {
        // Search for another elevator assembler
        int minY = Math.max(world.getBottomY(), pos.getY() - 64);
        int maxY = Math.min(world.getTopY(), pos.getY() + 64);
        int second = pos.getY();
        for (int y = minY; y <= maxY; y++) {
            if (y == pos.getY()) {
                continue;
            }
            if (world.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).getBlock() instanceof ElevatorAssemblerBlock) {
                second = y;
                break;
            }
        }
        // Minimum elevator shaft height is 5
        if (Math.abs(pos.getY() - second) < 5) {
            return false;
        }

        boolean goingUp = second < pos.getY();

        int elevatorMinY = Math.min(pos.getY(), second);
        int elevatorMaxY = Math.max(pos.getY(), second);

        Block north = world.getBlockState(pos.north()).getBlock();
        Block south = world.getBlockState(pos.south()).getBlock();

        boolean rotated = north instanceof ChainBlock && south instanceof ChainBlock;

        if (ElevatorEntity.validateShaft(world, pos.getX(), pos.getZ(), elevatorMinY, elevatorMaxY, rotated, false)) {
            ElevatorEntity.spawn(world, pos.getX(), pos.getZ(), elevatorMinY, elevatorMaxY, rotated, goingUp);
            world.breakBlock(pos, false);
            world.breakBlock(new BlockPos(pos.getX(), second, pos.getZ()), false);
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.assemble(world, pos);
    }
}
