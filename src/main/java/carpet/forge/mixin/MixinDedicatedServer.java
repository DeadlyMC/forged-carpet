package carpet.forge.mixin;

import carpet.forge.CarpetMain;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer
{
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerProfileCache;setOnlineMode(Z)V"))
    private void gameStartHook(CallbackInfoReturnable<Boolean> cir)
    {
        // [FCM] init - all stuff loaded from the server, just before worlds loading
        CarpetMain.onServerLoaded((DedicatedServer) (Object) this);
        // [FCM] start game hook
        CarpetMain.onGameStarted();
    }

}
