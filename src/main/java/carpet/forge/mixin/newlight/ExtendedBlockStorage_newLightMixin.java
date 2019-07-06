package carpet.forge.mixin.newlight;

import carpet.forge.CarpetSettings;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// CREDITS : Nessie
@Mixin(ExtendedBlockStorage.class)
public abstract class ExtendedBlockStorage_newLightMixin
{
    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    private void onIsEmpty(CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.newLight)
        {
            cir.setReturnValue(false);
        }
    }
}
