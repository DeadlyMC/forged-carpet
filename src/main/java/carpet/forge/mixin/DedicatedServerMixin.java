package carpet.forge.mixin;

import carpet.forge.CarpetServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin
{
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerProfileCache;setOnlineMode(Z)V"))
    private void gameStartHook(CallbackInfoReturnable<Boolean> cir)
    {
        // [FCM] init - all stuff loaded from the server, just before worlds loading
        CarpetServer.onServerLoaded((DedicatedServer) (Object) this);
        // [FCM] start game hook
        CarpetServer.onGameStarted();
    }

}
