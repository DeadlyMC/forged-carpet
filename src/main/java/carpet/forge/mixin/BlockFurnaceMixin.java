package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFurnace.class)
public abstract class BlockFurnaceMixin
{
    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
    private void fixRotation(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci)
    {
        if (CarpetSettings.accurateBlockPlacement) ci.cancel();
    }
}
