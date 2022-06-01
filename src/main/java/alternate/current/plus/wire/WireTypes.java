package alternate.current.plus.wire;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import alternate.current.plus.AlternateCurrentPlusMod;
import alternate.current.plus.WireInitializer;
import alternate.current.plus.wire.redstone.RedstoneWireType;

import net.minecraft.resources.ResourceLocation;

public class WireTypes {

	public static final WireType REDSTONE = new RedstoneWireType(1, WireConnectionBehavior.BOTH);

	private static final Map<ResourceLocation, WireType> ID_TO_TYPE = new HashMap<>();
	private static final Map<WireType, ResourceLocation> TYPE_TO_ID = new HashMap<>();

	private static int TYPE_COUNT;
	private static WireConnectionBehavior[] BEHAVIORS;

	private static final Logger LOGGER = AlternateCurrentPlusMod.LOGGER;

	private static boolean initializedTypes;
	private static boolean initializedBehaviors;

	public static void initializeTypes(Collection<WireInitializer> initializers) {
		if (initializedTypes) {
			return;
		}

		initializers.forEach(initializer -> {
			initializer.initializeWireTypes(WireTypes::registerType);
		});

		initializedTypes = true;
	}

	public static void initializeBehaviors(Collection<WireInitializer> initializers) {
		if (!initializedTypes) {
			throw new IllegalStateException("tried to initialize wire connection behaviors before wire types!");
		}
		if (initializedBehaviors) {
			return;
		}

		TYPE_COUNT = WireType.idCounter;
		BEHAVIORS = new WireConnectionBehavior[TYPE_COUNT * TYPE_COUNT];

		initializers.forEach(initializer -> {
			initializer.initializeWireConnectionBehaviors(WireTypes::registerBehavior);
		});

		initializedBehaviors = true;
	}

	private static void registerType(ResourceLocation id, WireType type) {
		if (ID_TO_TYPE.containsKey(id)) {
			LOGGER.warn("Registering duplicate wire type id " + id);
		}
		if (TYPE_TO_ID.containsKey(type)) {
			LOGGER.warn("Registering duplicate wire type value " + type);
		}

		ID_TO_TYPE.put(id, type);
		TYPE_TO_ID.put(type, id);
	}

	private static void registerBehavior(WireType a, WireType b, WireConnectionBehavior behavior) {
		ResourceLocation aId = getId(a);
		ResourceLocation bId = getId(b);

		if (aId == null) {
			LOGGER.warn("Cannot register special wire connection behavior for wire type " + a + " as it does not appear in the registry!");
			return;
		}
		if (bId == null) {
			LOGGER.warn("Cannot register special wire connection behavior for wire type " + b + " as it does not appear in the registry!");
			return;
		}
		if (a == b) {
			LOGGER.warn("Cannot register special wire connection behavior between wire type " + aId + " and itself!");
			return;
		}

		WireConnectionBehavior r = getSpecialBehavior(a, b);

		if (r != null && r != behavior) {
			LOGGER.warn("Changing special wire connection behavior between wire type " + aId + " and wire type " + bId + " from " + r + " to " + behavior);
		}

		setSpecialBehavior(a, b, behavior);
	}

	private static int index(WireType a, WireType b) {
		return index(a.id, b.id);
	}

	private static int index(int a, int b) {
		return a * TYPE_COUNT + b;
	}

	private static void setSpecialBehavior(WireType a, WireType b, WireConnectionBehavior behavior) {
		int i = index(a, b);
		int j = index(b, a);

		BEHAVIORS[i] = behavior;
		BEHAVIORS[j] = behavior.inverse();
	}

	private static WireConnectionBehavior getSpecialBehavior(WireType a, WireType b) {
		return BEHAVIORS[index(a, b)];
	}

	public static WireType getType(ResourceLocation id) {
		return ID_TO_TYPE.get(id);
	}

	public static ResourceLocation getId(WireType type) {
		return TYPE_TO_ID.get(type);
	}

	public static WireConnectionBehavior getConnectionBehavior(WireType a, WireType b) {
		if (a == b) {
			return WireConnectionBehavior.BOTH;
		}

		WireConnectionBehavior behavior = getSpecialBehavior(a, b);

		if (behavior == null) {
			return b.defaultBehavior.inverse().and(a.defaultBehavior);
		}

		return behavior;
	}

	@FunctionalInterface
	public static interface WireTypeRegistry {

		public void register(ResourceLocation id, WireType type);

	}

	@FunctionalInterface
	public static interface WireConnectionBehaviorRegistry {

		public void register(WireType a, WireType b, WireConnectionBehavior behavior);

		default void register(WireType a, ResourceLocation bId, WireConnectionBehavior behavior) {
			WireType b = getType(bId);

			if (b == null) {
				LOGGER.warn("Cannot register special wire connection behavior for wire type " + bId + " as it does not appear in the registry!");
			} else {
				register(a, b, behavior);
			}
		}

		default void register(ResourceLocation aId, WireType b, WireConnectionBehavior behavior) {
			WireType a = getType(aId);

			if (a == null) {
				LOGGER.warn("Cannot register special wire connection behavior for wire type " + aId + " as it does not appear in the registry!");
			} else {
				register(a, b, behavior);
			}
		}

		default void register(ResourceLocation aId, ResourceLocation bId, WireConnectionBehavior behavior) {
			WireType a = getType(aId);
			WireType b = getType(bId);

			if (a == null) {
				LOGGER.warn("Cannot register special wire connection behavior for wire type " + aId + " as it does not appear in the registry!");
			} else
			if (b == null) {
				LOGGER.warn("Cannot register special wire connection behavior for wire type " + bId + " as it does not appear in the registry!");
			} else {
				register(a, b, behavior);
			}
		}
	}
}
