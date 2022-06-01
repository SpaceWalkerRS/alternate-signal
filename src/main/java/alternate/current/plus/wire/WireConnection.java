package alternate.current.plus.wire;

/**
 * This class represents a connection between some WireNode (the 'owner') and a
 * neighboring WireNode. Two wires are considered to be connected if power can
 * flow from one wire to the other (and/or vice versa).
 * 
 * @author Space Walker
 */
public class WireConnection {

	/** The connected wire. */
	final WireNode wire;
	/** A number encoding the relative position of the connected wire. */
	final int flow;
	/** The behavior of power flow between the owner of the connection and the connected wire. */
	final WireConnectionBehavior behavior;

	/** The next connection in the sequence. */
	WireConnection next;

	WireConnection(WireNode wire, int flow, WireConnectionBehavior behavior) {
		this.wire = wire;
		this.flow = flow;
		this.behavior = behavior;
	}
}
