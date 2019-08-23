package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerStatusResponse.class)
public abstract class ServerStatusResponse_motdMixin
{
    @Inject(method = "getServerDescription", at = @At("HEAD"), cancellable = true)
    private void getDescriptionAlternative(CallbackInfoReturnable<ITextComponent> cir)
    {
        if (!CarpetSettings.customMOTD.equals("_"))
        {
            cir.setReturnValue(new TextComponentString(CarpetSettings.customMOTD));
        }
    }
}
