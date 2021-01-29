package carpet.forge.mixin;

import carpet.forge.CarpetServer;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServer_coreMixin
{
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCtor(File anvilFileIn, Proxy proxyIn, DataFixer dataFixerIn, YggdrasilAuthenticationService authServiceIn,
            MinecraftSessionService sessionServiceIn, GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn,
            CallbackInfo ci)
    {
        CarpetServer.init((MinecraftServer) (Object) this);
    }
    
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;tickCounter:I", ordinal = 0, shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci)
    {
        CarpetServer.tick((MinecraftServer) (Object) this);
    }
}
