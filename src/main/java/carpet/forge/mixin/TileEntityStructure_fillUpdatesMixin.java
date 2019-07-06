package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityStructure.class)
public abstract class TileEntityStructure_fillUpdatesMixin
{
    @Redirect(
            method = "load(Z)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/structure/template/Template;addBlocksToWorldChunk(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/PlacementSettings;)V")
    )
    private void onLoad(Template template, World worldIn, BlockPos pos, PlacementSettings placementIn)
    {
        if(!CarpetSettings.fillUpdates)
            CarpetSettings.impendingFillSkipUpdates = true;
        try
        {
            template.addBlocksToWorldChunk(worldIn, pos, placementIn);
        }
        finally
        {
            CarpetSettings.impendingFillSkipUpdates = false;
        }
    }
}
