package alternate.current.plus.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import alternate.current.plus.wire.WireBlock;
import alternate.current.plus.wire.WireType;
import alternate.current.plus.wire.WireTypes;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

	@Inject(
		method = "getSystemInformation",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/Registry;BLOCK_REGISTRY:Lnet/minecraft/resources/ResourceKey;"
		)
	)
	private void addWireTypeInfo(CallbackInfoReturnable<List<String>> cir, long maxMemory, long totalMemory, long freeMemory, long usedMemory, List<String> info, BlockPos pos, BlockState state) {
		Block block = state.getBlock();

		if (block instanceof WireBlock) {
			WireBlock wire = (WireBlock)block;
			WireType type = wire.getWireType();
			ResourceLocation id = WireTypes.getId(type);

			info.add("wire type: " + id);
		}
	}
}
