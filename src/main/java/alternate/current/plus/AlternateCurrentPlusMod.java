package alternate.current.plus;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import alternate.current.plus.util.profiler.ACProfiler;
import alternate.current.plus.util.profiler.Profiler;
import alternate.current.plus.wire.WireTypes;
import alternate.current.plus.wire.WireTypes.WireConnectionBehaviorRegistry;
import alternate.current.plus.wire.WireTypes.WireTypeRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import net.minecraft.resources.ResourceLocation;

public class AlternateCurrentPlusMod implements ModInitializer, WireInitializer {

	public static final String MOD_ID = "alternate-current-plus";
	public static final String MOD_NAME = "Alternate Current Plus";
	public static final String MOD_VERSION = "1.3.0";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static final boolean DEBUG = false;

	public static boolean on = true;

	@Override
	public void onInitialize() {
		if (DEBUG) {
			LOGGER.warn(String.format("You are running a DEBUG version of %s!", MOD_NAME));
		}

		runWireInitializers();

		LOGGER.info(MOD_NAME + " has been initialized!");
	}

	@Override
	public void initializeWireTypes(WireTypeRegistry registry) {
		registry.register(new ResourceLocation("redstone_wire"), WireTypes.REDSTONE); // register vanilla redstone dust
	}

	@Override
	public void initializeWireConnectionBehaviors(WireConnectionBehaviorRegistry registry) {

	}

	private void runWireInitializers() {
		Collection<WireInitializer> initializers = new ArrayList<>();

		FabricLoaderImpl.INSTANCE.getEntrypointContainers(MOD_ID, WireInitializer.class).forEach(container -> {
			WireInitializer initializer = container.getEntrypoint();
			ModContainer mod = container.getProvider();

			LOGGER.info("Found wire initializer for mod " + mod.getMetadata().getId());

			initializers.add(initializer);
		});

		WireTypes.initializeTypes(initializers);
		WireTypes.initializeBehaviors(initializers);
	}

	public static Profiler createProfiler() {
		return DEBUG ? new ACProfiler() : Profiler.DUMMY;
	}
}
