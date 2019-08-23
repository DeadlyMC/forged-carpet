package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockTorch;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockTorch.class)
public abstract class BlockTorchMixin
{
    @Inject(method = "canPlaceOn", at = @At("HEAD"), cancellable = true)
    private void canPlaceOnOver(World worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        Block block = worldIn.getBlockState(pos).getBlock();
        boolean flag = block == Blocks.END_GATEWAY || (block == Blocks.LIT_PUMPKIN && !CarpetSettings.relaxedBlockPlacement);
        
        if (worldIn.getBlockState(pos).isTopSolid())
        {
            cir.setReturnValue(!flag);
        }
        else
        {
            boolean flag1 = block instanceof BlockFence || block == Blocks.GLASS || block == Blocks.COBBLESTONE_WALL || block == Blocks.STAINED_GLASS;
            cir.setReturnValue(flag1 && !flag);
        }
    }
}
