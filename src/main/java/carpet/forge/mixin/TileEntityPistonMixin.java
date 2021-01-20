package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPiston.class)
public abstract class TileEntityPistonMixin extends TileEntity
{
    // You might ask why an injection point like this, the prev injection point fails with SevTech ages installed
    // Whereas this injection point seems to work flawlessly. It does have an extra check tho, but i
    // don't think it should be much of an issue here.
    @Inject(method = "update", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/tileentity/TileEntityPiston;invalidate()V"))
    private void onUpdate(CallbackInfo ci)
    {
        if (CarpetSettings.pistonGhostBlocksFix && this.world.getBlockState(this.pos).getBlock() == Blocks.PISTON_EXTENSION)
        {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(pos.offset(state.getValue(BlockPistonExtension.FACING).getOpposite()), state, state, 0);
        }
    }
}
