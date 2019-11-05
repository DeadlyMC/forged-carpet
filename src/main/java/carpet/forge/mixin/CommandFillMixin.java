package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.command.CommandFill;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandFill.class)
public abstract class CommandFillMixin
{
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 32768))
    private int onExecuteLimit(int original)
    {
        return CarpetSettings.fillLimit;
    }
    
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 2, ordinal = 2))
    private int onExecuteUpdate(int original)
    {
        return original | (CarpetSettings.fillUpdates?0:128);
    }
    
    @Redirect(
            method = "execute",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V")
    )
    private void onExecuteUpdate(World world, BlockPos pos, Block blockType, boolean updateObservers)
    {
        if (CarpetSettings.fillUpdates)
            world.notifyNeighborsRespectDebug(pos, blockType, updateObservers);
    }
}
