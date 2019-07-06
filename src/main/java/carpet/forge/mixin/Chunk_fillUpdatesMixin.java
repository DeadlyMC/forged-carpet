package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Chunk.class)
public abstract class Chunk_fillUpdatesMixin
{
    @Redirect(
            method = "setBlockState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;breakBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V")
    )
    private void onSetBlockStateRemoved(Block block, World worldIn, BlockPos pos, IBlockState state)
    {
        if (!CarpetSettings.impendingFillSkipUpdates)
            block.breakBlock(worldIn, pos, state);
    }
    
    @Redirect(
            method = "setBlockState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onBlockAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V")
    )
    private void onSetBlockStateAdded(Block block, World worldIn, BlockPos pos, IBlockState state)
    {
        if (!CarpetSettings.impendingFillSkipUpdates)
            block.onBlockAdded(worldIn, pos, state);
    }
}
