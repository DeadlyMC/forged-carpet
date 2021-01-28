package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockDispenser.class, priority = 999) // Apply the @ModifyArg before tweakeroo
public abstract class BlockDispenserMixin
{
    @Unique private IBlockState state;
    
    @Inject(method = "onBlockAdded", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockDispenser;setDefaultDirection(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V"))
    private void rotationFix(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci)
    {
        if (CarpetSettings.accurateBlockPlacement) ci.cancel();
    }
    
    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"))
    private void copyState(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, CallbackInfo ci)
    {
        this.state = state;
    }
    
    @ModifyArg(method = "onBlockPlacedBy", index = 1, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
    private IBlockState setBlockStateWithCheck(IBlockState originalState)
    {
        if (CarpetSettings.accurateBlockPlacement) return this.state;
        return originalState;
    }
}
