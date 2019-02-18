package carpet.forge.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnumFacing.class)
public abstract class MixinEnumFacing
{

    @Shadow
    @Final
    public static EnumFacing DOWN;

    @Inject(method = "getDirectionFromEntityLiving", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private static void flippinEligibility(BlockPos pos, EntityLivingBase placer, CallbackInfoReturnable<EnumFacing> cir)
    {
        cir.setReturnValue(DOWN);
    }
}
