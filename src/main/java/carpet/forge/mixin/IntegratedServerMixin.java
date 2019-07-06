package carpet.forge.mixin;

import carpet.forge.CarpetServer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin
{
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setKeyPair(Ljava/security/KeyPair;)V"))
    private void gameStartHook(CallbackInfoReturnable<Boolean> cir)
    {
        CarpetServer.onServerLoaded((IntegratedServer) (Object) this);
    }
}
