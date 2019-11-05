package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.command.CommandClone;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandClone.class)
public abstract class CommandCloneMixin
{
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 32768))
    private int onExecuteLimit(int original)
    {
        return CarpetSettings.fillLimit;
    }
    
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 2, ordinal = 1))
    private int onExecuteUpdate1(int original)
    {
        return original | (CarpetSettings.fillUpdates?0:128);
    }
    
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 3, ordinal = 1))
    private int onExecuteUpdate2(int original)
    {
        return (CarpetSettings.fillUpdates?3:131);
    }
    
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 2, ordinal = 2))
    private int onExecuteUpdate3(int original)
    {
        return original | (CarpetSettings.fillUpdates?0:128);
    }
    
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 2, ordinal = 3))
    private int onExecuteUpdate4(int original)
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
