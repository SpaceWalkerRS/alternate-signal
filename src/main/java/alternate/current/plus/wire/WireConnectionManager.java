package alternate.current.plus.wire;

import java.util.Arrays;
import java.util.function.Consumer;

import alternate.current.plus.wire.WireHandler.NodeProvider;

import net.minecraft.core.BlockPos;

public class WireConnectionManager {

	private static final int SPAN = 3;

	/** The owner of these connections. */
	final WireNode owner;
	private final int x;
	private final int y;
	private final int z;

	private final WireConnection[] all;

	private WireConnection head;
	private WireConnection tail;

	/** The total number of connections. */
	int total;

	/**
	 * A 4 bit number that encodes which in direction(s) the owner has connections
	 * to other wires.
	 */
	private int flowTotal;
	/** The direction of flow based connections to other wires. */
	int iFlowDir;

	WireConnectionManager(WireNode owner) {
		this.owner = owner;
		this.x = this.owner.pos.getX();
		this.y = this.owner.pos.getY();
		this.z = this.owner.pos.getZ();

		this.all = new WireConnection[SPAN * SPAN * SPAN];

		this.total = 0;

		this.flowTotal = 0;
		this.iFlowDir = -1;
	}

	void set(NodeProvider nodes) {
		if (total > 0) {
			clear();
		}

		owner.type.findConnections(nodes, owner, this::add);

		if (total > 0) {
			iFlowDir = WireHandler.FLOW_IN_TO_FLOW_OUT[flowTotal];
		}
	}

	private void clear() {
		Arrays.fill(all, null);

		head = null;
		tail = null;

		total = 0;

		flowTotal = 0;
		iFlowDir = -1;
	}

	private int index(BlockPos pos) {
		return index(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
	}

	private int index(int dx, int dy, int dz) {
		int s = dx * dx + dy * dy + dz * dz;

		// Only direct neighbors or diagonal neighbors
		// of the owner can be connected to it.
		if (s < 1 || s > 2) {
			return -1;
		}

		return (dx + 1) + SPAN * ((dy + 1) + SPAN * (dz + 1));
	}

	private int flow(int dx, int dy, int dz) {
		// 1 >> d = { 1 for d in { 0 }, 0 for d in { -1, 1 } }
		// dx + 1 gives iDir for dx in { -1, 1 }
		// dz + 2 gives iDir for dz in { -1, 1 }
		// flow = 1 << iDir
		int flowX = (-(1 >> dx) + 1) << (dx + 1);
		int flowZ = (-(1 >> dz) + 1) << (dz + 2);

		return flowX | flowZ;
	}

	private void add(WireNode wire, WireConnectionBehavior behavior) {
		behavior = WireTypes.getConnectionBehavior(owner.type, wire.type).and(behavior);

		if (behavior == WireConnectionBehavior.NONE) {
			return;
		}

		BlockPos pos = wire.pos;
		int dx = pos.getX() - x;
		int dy = pos.getY() - y;
		int dz = pos.getZ() - z;

		int index = index(dx, dy, dz);
		int flow = flow(dx, dy, dz);

		if (index < 0) {
			throw new IllegalStateException("Cannot add connection to wire at illegal relative position (" + dx + ", " + dy + ", " + dz + ")");
		}
		if (all[index] != null) {
			throw new IllegalStateException("Cannot add duplicate connection to wire at relative position (" + dx + ", " + dy + ", " + dz + ")");
		}

		WireConnection connection = new WireConnection(wire, flow, behavior);

		if (head == null) {
			head = connection;
			tail = connection;
		} else {
			tail.next = connection;
			tail = connection;
		}

		all[index] = connection;

		total++;
		flowTotal |= connection.flow;
	}

	private WireConnection get(int index) {
		return index < 0 ? null : all[index];
	}

	WireConnection get(BlockPos pos) {
		return get(index(pos));
	}

	WireConnection get(int dx, int dy, int dz) {
		return get(index(dx, dy, dz));
	}

	/**
	 * Iterate over all connections. Use this method if the iteration order is not
	 * important.
	 */
	void forEach(Consumer<WireConnection> consumer) {
		for (WireConnection c = head; c != null; c = c.next) {
			consumer.accept(c);
		}
	}

	@FunctionalInterface
	public static interface ConnectionConsumer {

		public void accept(WireNode wire, WireConnectionBehavior behavior);

	}
}
