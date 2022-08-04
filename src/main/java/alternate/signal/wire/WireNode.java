package alternate.signal.wire;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import signal.api.IBlockState;
import signal.api.signal.SignalType;
import signal.api.signal.wire.ConnectionSide;
import signal.api.signal.wire.WireType;
import signal.api.signal.wire.block.Wire;

/**
 * A WireNode is a Node that represents a wire in the world. It stores all the
 * information about the wire that the WireHandler needs to calculate power
 * changes.
 * 
 * @author Space Walker
 */
public class WireNode extends Node {

	final WireConnectionManager connections;
	final Wire block;
	final WireType type;

	/** The power level this wire currently holds in the world. */
	int currentPower;
	/**
	 * While calculating power changes for a network, this field is used to keep
	 * track of the power level this wire should have.
	 */
	int virtualPower;
	/** The power level received from non-wire components. */
	int externalPower;
	/**
	 * A 4-bit number that keeps track of the power flow of the wires that give this
	 * wire its power level.
	 */
	int flowIn;
	/** The direction of power flow, based on the incoming flow. */
	int iFlowDir;
	boolean added;
	boolean removed;
	boolean shouldBreak;
	boolean root;
	boolean discovered;
	boolean searched;

	/** The next wire in the simple queue. */
	WireNode next_wire;

	WireNode(ServerLevel level, BlockPos pos, BlockState state) {
		super(level);

		this.pos = pos.immutable();
		this.state = state;

		this.connections = new WireConnectionManager(this);
		this.block = (Wire)state.getBlock();
		this.type = this.block.getWireType();

		this.virtualPower = this.currentPower = this.block.getSignal(this.level, this.pos, this.state);
		this.priority = priority();
	}

	@Override
	Node set(BlockPos pos, BlockState state, boolean clearNeighbors) {
		throw new UnsupportedOperationException("Cannot update a WireNode!");
	}

	@Override
	int priority() {
		return type.clamp(virtualPower);
	}

	@Override
	int offset() {
		return -type.min();
	}

	@Override
	public boolean isWire() {
		return true;
	}

	@Override
	public boolean isWire(WireType type) {
		return this.type == type;
	}

	@Override
	public boolean isConductor(SignalType type) {
		return false;
	}

	@Override
	public boolean isSignalSource(SignalType type) {
		return false;
	}

	@Override
	public WireNode asWire() {
		return this;
	}

	boolean offerPower(int power, ConnectionSide side) {
		if (removed || shouldBreak) {
			return false;
		}
		if (power == virtualPower) {
			flowIn |= WireHandler.CONNECTION_SIDE_TO_FLOW_IN[side.getIndex()];
			return false;
		}
		if (power > virtualPower) {
			virtualPower = power;
			flowIn = WireHandler.CONNECTION_SIDE_TO_FLOW_IN[side.getIndex()];

			return true;
		}

		return false;
	}

	boolean setPower() {
		if (removed) {
			return true;
		}

		state = level.getBlockState(pos);
		istate = (IBlockState)state;

		if (!istate.is(block)) {
			return false; // we should never get here
		}

		if (shouldBreak) {
			Block.dropResources(state, level, pos);
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);

			return true;
		}

		currentPower = type.clamp(virtualPower);
		state = block.setSignal(level, pos, state, currentPower);

		return LevelHelper.setWireState(level, pos, state, added);
	}
}
