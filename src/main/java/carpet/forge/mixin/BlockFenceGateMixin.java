package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFenceGate.class)
public abstract class BlockFenceGateMixin extends BlockHorizontal
{
    protected BlockFenceGateMixin(Material materialIn, MapColor colorIn)
    {
        super(materialIn, colorIn);
    }
    
    @Inject(method = "canPlaceBlockAt", at = @At("HEAD"), cancellable = true)
    private void canPlaceOnOver(World worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue((worldIn.getBlockState(pos.down()).getMaterial().isSolid() || CarpetSettings.relaxedBlockPlacement) && super.canPlaceBlockAt(worldIn, pos));
    }
}
