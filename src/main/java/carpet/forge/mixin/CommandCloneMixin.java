package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.command.CommandClone;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(CommandClone.class)
public abstract class CommandCloneMixin
{
    @ModifyConstant(method = "execute", constant = {@Constant(intValue = 2), @Constant(intValue = 3)})
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
    
    @Redirect(method = "execute", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getPendingBlockUpdates(Lnet/minecraft/world/gen/structure/StructureBoundingBox;Z)Ljava/util/List;"))
    private List<NextTickListEntry> pendingBlockUpdates(World world, StructureBoundingBox structureBB, boolean remove)
    {
        if (CarpetSettings.fillUpdates) return world.getPendingBlockUpdates(structureBB, remove);
        return null;
    }
}
