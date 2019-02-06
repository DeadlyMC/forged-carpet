package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
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

    public String cm_name() { return "Piston"; }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", shift = At.Shift.BEFORE))
    private void notifyBlockUpdate(CallbackInfo ci) {
        if (CarpetSettings.pistonGhostBlocksFix == 1 )
        {
            IBlockState blockstate = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(pos.offset(blockstate.getValue(BlockPistonExtension.FACING).getOpposite()), blockstate, blockstate, 0);
        }
    }
}
