package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(CommandSetBlock.class)
public abstract class CommandSetBlockMixin
{
    @Redirect(
            method = "execute",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z")
    )
    private boolean onExecuteUpdate1(World world, BlockPos pos, IBlockState newState, int flags)
    {
        return world.setBlockState(pos, newState, flags | (CarpetSettings.fillUpdates?0:128));
    }
    
    @Redirect(
            method = "execute",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V")
    )
    private void onExecuteUpdate2(World world, BlockPos pos, Block blockType, boolean updateObservers)
    {
        if (CarpetSettings.fillUpdates)
            world.notifyNeighborsRespectDebug(pos, blockType, updateObservers);
    }
}
