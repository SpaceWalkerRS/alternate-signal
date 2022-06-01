package alternate.current.plus.mixin.block;

import org.spongepowered.asm.mixin.Mixin;

import alternate.current.plus.interfaces.mixin.IBlock;

import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public class BlockMixin implements IBlock {

}
