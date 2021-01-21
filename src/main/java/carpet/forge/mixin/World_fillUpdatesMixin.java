package carpet.forge.mixin;

import carpet.forge.fakes.IWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class World_fillUpdatesMixin implements IWorld
{
    @Unique private boolean skipUpdates = false;
    
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/block/state/IBlockState;getLightOpacity(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I", ordinal = 0))
    private void shouldSkipUpdates(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> cir)
    {
        this.skipUpdates = ((flags & 128) != 0);
    }
    
    @ModifyConstant(method = "markAndNotifyBlock", constant = @Constant(intValue = 16), remap = false)
    private int noUpdateFlag(int flags)
    {
        return flags | 128;
    }
    
    @Override
    public boolean shouldSkipUpdates()
    {
        return this.skipUpdates;
    }
}
