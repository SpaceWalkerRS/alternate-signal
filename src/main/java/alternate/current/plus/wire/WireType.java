package alternate.current.plus.wire;

import alternate.current.plus.wire.WireConnectionManager.ConnectionConsumer;
import alternate.current.plus.wire.WireHandler.NodeProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class holds all information that specifies how a wire of this type
 * should interact with other wires.
 * 
 * @author Space Walker
 */
public abstract class WireType {

	static int idCounter;

	final int id;

	public final int minPower;
	public final int maxPower;
	public final int powerStep;

	// default behavior when interacting with other wire types
	public final WireConnectionBehavior defaultBehavior;

	protected WireType(int minPower, int maxPower, int powerStep, WireConnectionBehavior defaultBehavior) {
		if (minPower > maxPower) {
			throw new IllegalArgumentException("minPower cannot be more than maxPower!");
		}
		if (powerStep < 0) {
			throw new IllegalArgumentException("powerStep must be at least 0!");
		}

		this.id = idCounter++;

		this.minPower = minPower;
		this.maxPower = maxPower;
		this.powerStep = powerStep;

		this.defaultBehavior = defaultBehavior;
	}

	@Override
	public final String toString() {
		return String.format("%s[id: %d, min: %d, max: %d, step: %d]", getClass().getCanonicalName(), id, minPower, maxPower, powerStep);
	}

	public final int clamp(int power) {
		return Mth.clamp(power, minPower, maxPower);
	}

	public abstract int getPower(LevelAccess level, BlockPos pos, BlockState state);

	public abstract BlockState setPower(LevelAccess level, BlockPos pos, BlockState state, int power);

	public abstract void findConnections(NodeProvider nodes, WireNode wire, ConnectionConsumer connections);

}
