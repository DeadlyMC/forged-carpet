package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandSetBlock.class)
public abstract class MixinCommandSetBlock extends CommandBase
{
    @Redirect(method = "execute", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
    private boolean replaceSetBlockState(World world, BlockPos blockpos, IBlockState iblockstate, int flags)
    {
        return !world.setBlockState(blockpos, iblockstate, 2 | (CarpetSettings.getBool("fillUpdates") ? 0 : 128));
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V"))
    private void replaceNotfiyNeighbors(World world, BlockPos blockpos, Block blockType, boolean updateObservers)
    {
        if (CarpetSettings.getBool("fillUpdates"))
        {
            world.notifyNeighborsRespectDebug(blockpos, blockType, false);
        }

    }
}
