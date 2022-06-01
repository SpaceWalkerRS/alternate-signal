package alternate.current.plus.wire.redstone;

import alternate.current.plus.util.Redstone;
import alternate.current.plus.wire.LevelAccess;
import alternate.current.plus.wire.Node;
import alternate.current.plus.wire.WireConnectionBehavior;
import alternate.current.plus.wire.WireConnectionManager.ConnectionConsumer;
import alternate.current.plus.wire.WireHandler.Directions;
import alternate.current.plus.wire.WireHandler.NodeProvider;
import alternate.current.plus.wire.WireNode;
import alternate.current.plus.wire.WireType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RedstoneWireType extends WireType {

	public RedstoneWireType(int powerStep, WireConnectionBehavior defaultBehavior) {
		super(Redstone.SIGNAL_MIN, Redstone.SIGNAL_MAX, powerStep, defaultBehavior);
	}

	@Override
	public int getPower(LevelAccess level, BlockPos pos, BlockState state) {
		return state.getValue(BlockStateProperties.POWER);
	}

	@Override
	public BlockState setPower(LevelAccess level, BlockPos pos, BlockState state, int power) {
		return state.setValue(BlockStateProperties.POWER, power);
	}

	@Override
	public void findConnections(NodeProvider nodes, WireNode wire, ConnectionConsumer connections) {
		boolean belowIsConductor = nodes.getNeighbor(wire, Directions.DOWN).isConductor();
		boolean aboveIsConductor = nodes.getNeighbor(wire, Directions.UP).isConductor();

		for (int iDir = 0; iDir < Directions.HORIZONTAL.length; iDir++) {
			Node neighbor = nodes.getNeighbor(wire, iDir);

			if (neighbor.isWire()) {
				connections.accept(neighbor.asWire(), WireConnectionBehavior.BOTH);

				continue;
			}

			boolean sideIsConductor = neighbor.isConductor();

			if (!sideIsConductor) {
				Node node = nodes.getNeighbor(neighbor, Directions.DOWN);

				if (node.isWire()) {
					connections.accept(node.asWire(), WireConnectionBehavior.of(belowIsConductor, true));
				}
			}
			if (!aboveIsConductor) {
				Node node = nodes.getNeighbor(neighbor, Directions.UP);

				if (node.isWire()) {
					connections.accept(node.asWire(), WireConnectionBehavior.of(true, sideIsConductor));
				}
			}
		}
	}
}
