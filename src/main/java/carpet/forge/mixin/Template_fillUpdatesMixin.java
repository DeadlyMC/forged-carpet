package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Template.class)
public abstract class Template_fillUpdatesMixin
{
    @ModifyConstant(
            method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
            constant = @Constant(intValue = 4)
    )
    private int onAddBlocksToWorldUpdate1(int original)
    {
        return original | (CarpetSettings.fillUpdates?0:128);
    }
    
    @Redirect(method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
            at = @At(value = "INVOKE", ordinal = 1,
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z")
    )
    private boolean onAddBlocksToWorldUpdate2(World world, BlockPos pos, IBlockState newState, int flags)
    {
        return world.setBlockState(pos, newState, flags | (CarpetSettings.fillUpdates?0:128));
    }
    
    @Redirect(
            method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V")
    )
    private void onAddBlocksToWorld(World world, BlockPos pos, Block blockType, boolean updateObservers)
    {
        if (!CarpetSettings.impendingFillSkipUpdates)
            world.notifyNeighborsRespectDebug(pos, blockType, updateObservers);
    }
}
