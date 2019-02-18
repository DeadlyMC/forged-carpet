package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExtendedBlockStorage.class)
public abstract class MixinExtendedBlockStorage
{

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    private void ifCancelEmpty(CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.newLight)
            cir.setReturnValue(false);
    }

}
