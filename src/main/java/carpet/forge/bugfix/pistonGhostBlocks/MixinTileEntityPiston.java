package carpet.forge.bugfix.pistonGhostBlocks;

import carpet.forge.CarpetMain;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityPiston.class)
public abstract class MixinTileEntityPiston extends TileEntity {

    /* TODO: Carpet mixin
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
    public void notifyBlockUpdate(CallbackInfo ci) {
        if (CarpetMain.config.pistonGhostBlocks.enabled) {
            IBlockState blockState = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(pos.offset(blockState.getValue(BlockPistonExtension.FACING).getOpposite()), blockState, blockState, 0);
        }
    }
    */
}
