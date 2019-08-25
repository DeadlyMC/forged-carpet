package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.interfaces.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class World_newLightMixin
{
    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void onCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.newLight)
        {
            ((IWorld) this).getLightingEngine().scheduleLightUpdate(lightType, pos);
            cir.setReturnValue(true);
        }
    }
}
