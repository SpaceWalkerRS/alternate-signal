package alternate.current.plus.wire;

import alternate.current.plus.interfaces.mixin.IServerLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface should be implemented by any wire block type. While it is the
 * WireType class that represents different wire types and how they behave, this
 * interface is used by the WireHandler to distinguish between wire blocks and
 * non wire blocks.
 * 
 * @author Space Walker
 */
public interface WireBlock {

	public WireType getWireType();

	default boolean isConnectedWire(BlockState state) {
		Block block = state.getBlock();
		return block instanceof WireBlock && shouldConnect((WireBlock)block);
	}

	/**
	 * Non-static version of RedStoneWireBlock#shouldConnectTo - required to handle
	 * the connection properties for different wire types.
	 */
	default boolean shouldConnect(BlockState state) {
		return shouldConnect(state, null);
	}

	/**
	 * Non-static version of RedStoneWireBlock#shouldConnectTo - required to handle
	 * the connection properties for different wire types.
	 */
	default boolean shouldConnect(BlockState state, Direction dir) {
		Block block = state.getBlock();

		if (block instanceof WireBlock) {
			return shouldConnect((WireBlock)block);
		}
		if (block == Blocks.REPEATER) {
			Direction facing = state.getValue(RepeaterBlock.FACING);
			return facing == dir || facing.getOpposite() == dir;
		}
		if (block == Blocks.OBSERVER) {
			return state.getValue(ObserverBlock.FACING) == dir;
		}

		return state.isSignalSource() && dir != null;
	}

	default boolean shouldConnect(WireBlock wire) {
		WireType a = getWireType();
		WireType b = wire.getWireType();

		return WireTypes.getConnectionBehavior(a, b) != WireConnectionBehavior.NONE;
	}

	default void onWireAdded(Level level, BlockPos pos) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getWireHandler().onWireAdded(pos, getWireType());
		}
	}

	default void onWireRemoved(Level level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getWireHandler().onWireRemoved(pos, state, getWireType());
		}
	}

	default void onWireUpdated(Level level, BlockPos pos) {
		if (!level.isClientSide()) {
			((IServerLevel)level).getWireHandler().onWireUpdated(pos, getWireType());
		}
	}
}
