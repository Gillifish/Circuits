package org.gillinet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;

public class AndGateBlock extends Block {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public AndGateBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false)
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        Direction facing = state.get(FACING);
        boolean leftPowered = world.isEmittingRedstonePower(pos.offset(facing.rotateYCounterclockwise()), facing.rotateYCounterclockwise());
        boolean rightPowered = world.isEmittingRedstonePower(pos.offset(facing.rotateYClockwise()), facing.rotateYClockwise());

        boolean powered = leftPowered && rightPowered;
        if (powered != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, powered), 3);
            world.updateNeighborsAlways(pos.offset(facing), this);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.updateNeighborsAlways(pos, this);
        this.updateRedstone(world, pos, state);
    }

    private void updateRedstone(World world, BlockPos pos, BlockState state) {
        world.updateNeighborsAlways(pos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction == state.get(FACING)) {
            if (state.get(POWERED)) {
                return 15;
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }
}