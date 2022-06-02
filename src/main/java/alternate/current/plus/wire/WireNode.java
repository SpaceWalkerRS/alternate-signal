package alternate.current.plus.wire;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A WireNode is a Node that represents a wire in the world. It stores all the
 * information about the wire that the WireHandler needs to calculate power
 * changes.
 * 
 * @author Space Walker
 */
public class WireNode extends Node {

	final WireType type;
	final WireConnectionManager connections;

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
	boolean prepared;
	boolean inNetwork;

	WireNode(WireBlock wireBlock, ServerLevel level, BlockPos pos, BlockState state) {
		this(wireBlock.getWireType(), level, pos, state);
	}

	WireNode(WireType type, ServerLevel level, BlockPos pos, BlockState state) {
		super(level);

		this.pos = pos.immutable();
		this.state = state;

		this.type = type;
		this.connections = new WireConnectionManager(this);

		this.virtualPower = this.currentPower = this.type.getPower(this.level, this.pos, this.state);
		this.priority = priority();
	}

	@Override
	Node update(BlockPos pos, BlockState state, boolean clearNeighbors) {
		throw new UnsupportedOperationException("Cannot update a WireNode!");
	}

	@Override
	int priority() {
		return type.clamp(virtualPower);
	}

	@Override
	int offset() {
		return -type.minPower;
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
	public WireNode asWire() {
		return this;
	}

	boolean offerPower(int power, int flow) {
		if (removed || shouldBreak) {
			return false;
		}
		if (power == virtualPower) {
			flowIn |= flow;
			return false;
		}
		if (power > virtualPower) {
			virtualPower = power;
			flowIn = flow;

			return true;
		}

		return false;
	}

	boolean setPower() {
		if (removed) {
			return true;
		}

		state = level.getBlockState(pos);

		if (shouldBreak) {
			BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
			Block.dropResources(state, level, pos, blockEntity);
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);

			return true;
		}

		currentPower = type.clamp(virtualPower);
		state = type.setPower(level, pos, state, currentPower);

		return LevelHelper.setWireState(level, pos, state, added);
	}
}
