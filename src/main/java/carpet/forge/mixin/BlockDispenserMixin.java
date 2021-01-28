package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockDispenser.class)
public abstract class BlockDispenserMixin
{
    @Inject(method = "onBlockAdded", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockDispenser;setDefaultDirection(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V"))
    private void rotationFix(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci)
    {
        if (CarpetSettings.accurateBlockPlacement) ci.cancel();
    }
    
    @Redirect(method = "onBlockPlacedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
    private boolean setBlockStateWithCheck(World world, BlockPos pos, IBlockState newState, int flags)
    {
        if (CarpetSettings.accurateBlockPlacement) return false;
        return world.setBlockState(pos, newState, flags);
    }
}
