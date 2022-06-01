package alternate.current.plus;

import alternate.current.plus.wire.WireTypes.WireConnectionBehaviorRegistry;
import alternate.current.plus.wire.WireTypes.WireTypeRegistry;

/**
 * An entrypoint that initializes wire types and wire connection behaviors.
 * 
 * @author Space Walker
 */
public interface WireInitializer {

	public void initializeWireTypes(WireTypeRegistry registry);

	public void initializeWireConnectionBehaviors(WireConnectionBehaviorRegistry registry);

}
