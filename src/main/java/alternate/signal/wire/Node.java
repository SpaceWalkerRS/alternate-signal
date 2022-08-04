package alternate.signal.wire;

import java.util.Arrays;

import alternate.signal.wire.WireHandler.Directions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import signal.api.IBlockState;
import signal.api.signal.SignalType;
import signal.api.signal.wire.WireType;

/**
 * A Node represents a block in the world. It also holds a few other pieces of
 * information that speed up the calculations in the WireHandler class.
 * 
 * @author Space Walker
 */
public class Node {

	final ServerLevel level;
	final Node[] neighbors;

	BlockPos pos;
	BlockState state;
	IBlockState istate;
	boolean invalid;

	/** The previous node in the priority queue. */
	Node prev_node;
	/** The next node in the priority queue. */
	Node next_node;
	/** The priority with which this node was queued. */
	int priority;
	/** The wire that queued this node for an update. */
	WireNode neighborWire;

	Node(ServerLevel level) {
		this.level = level;
		this.neighbors = new Node[Directions.ALL.length];
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Node)) {
			return false;
		}

		Node node = (Node)obj;

		return level == node.level && pos.equals(node.pos);
	}

	@Override
	public int hashCode() {
		return pos.hashCode();
	}

	Node set(BlockPos pos, BlockState state, boolean clearNeighbors) {
		IBlockState istate = (IBlockState)state;

		if (istate.isWire()) {
			throw new IllegalStateException("Cannot update a regular Node to a WireNode!");
		}

		if (clearNeighbors) {
			Arrays.fill(neighbors, null);
		}

		this.pos = pos.immutable();
		this.state = state;
		this.istate = istate;
		this.invalid = false;

		return this;
	}

	/**
	 * Determine the priority with which this node should be queued.
	 */
	int priority() {
		return neighborWire.priority;
	}

	/**
	 * Determine the offset with which this node should be queued.
	 */
	int offset() {
		return neighborWire.offset();
	}

	public boolean isWire() {
		return false;
	}

	public boolean isWire(WireType type) {
		return false;
	}

	public boolean isConductor(SignalType type) {
		return istate.isSignalConductor(level, pos, type);
	}

	public boolean isSignalSource(SignalType type) {
		return istate.isSignalSource(type);
	}

	public WireNode asWire() {
		throw new UnsupportedOperationException("Not a WireNode!");
	}
}
