package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(CommandSetBlock.class)
public abstract class CommandSetBlockMixin
{
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 2))
    private int flags(int flags)
    {
        return flags | (CarpetSettings.fillUpdates ? 0 : 128);
    }
    
    @Redirect(method = "execute", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V"))
    private void notifyNeighbors(World world, BlockPos pos, Block blockType, boolean updateObservers)
    {
        if (CarpetSettings.fillUpdates)
            world.notifyNeighborsRespectDebug(pos, blockType, updateObservers);
    }
}
