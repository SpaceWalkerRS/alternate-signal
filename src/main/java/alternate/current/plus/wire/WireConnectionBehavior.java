package alternate.current.plus.wire;

/**
 * Describes the behavior of power flow along a connection between a wire of
 * type A and a wire of type B.
 * 
 * @author Space Walker
 */
public enum WireConnectionBehavior {

	NONE(0b00),
	A2B (0b01),
	B2A (0b10),
	BOTH(0b11);

	private static final WireConnectionBehavior[] ALL;

	static {

		WireConnectionBehavior[] values = values();
		ALL = new WireConnectionBehavior[values.length];

		for (WireConnectionBehavior behavior : values) {
			ALL[behavior.flags] = behavior;
		}
	}

	final int flags;

	private WireConnectionBehavior(int flags) {
		this.flags = flags;
	}

	public boolean a2b() {
		return (flags & 0b01) != 0;
	}

	public boolean b2a() {
		return (flags & 0b10) != 0;
	}

	public WireConnectionBehavior opposite() {
		return ALL[flags ^ 0b11];
	}

	public WireConnectionBehavior inverse() {
		return ALL[((flags << 1) | (flags >> 1)) & 0b11];
	}

	public WireConnectionBehavior and(WireConnectionBehavior behavior) {
		return ALL[flags & behavior.flags];
	}

	public WireConnectionBehavior or(WireConnectionBehavior behavior) {
		return ALL[flags | behavior.flags];
	}

	public static WireConnectionBehavior of(int flags) {
		return ALL[flags & 0b11];
	}

	public static WireConnectionBehavior of(boolean a2b, boolean b2a) {
		int f1 = a2b ? 0b01 : 0b00;
		int f2 = b2a ? 0b10 : 0b00;

		return ALL[f1 | f2];
	}
}
