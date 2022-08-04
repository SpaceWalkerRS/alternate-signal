package alternate.signal.mixin;

import org.spongepowered.asm.mixin.Mixin;

import alternate.signal.interfaces.mixin.IServerLevel;
import alternate.signal.wire.WireHandler;

import net.minecraft.server.level.ServerLevel;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements IServerLevel {

	private final WireHandler wireHandler = new WireHandler((ServerLevel)(Object)this);

	@Override
	public WireHandler getWireHandler() {
		return wireHandler;
	}
}
