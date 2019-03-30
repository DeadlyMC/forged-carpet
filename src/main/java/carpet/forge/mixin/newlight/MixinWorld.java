package carpet.forge.mixin.newlight;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// CREDITS : Nessie
@Mixin(World.class)
public abstract class MixinWorld implements IWorld
{
    
    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void onCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.newLight)
        {
            cir.cancel();
            this.getLightingEngine().scheduleLightUpdate(lightType, pos);
            cir.setReturnValue(true);
        }
    }
}
