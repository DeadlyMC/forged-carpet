package carpet.forge.mixin;

import carpet.forge.CarpetServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServer_coreMixin
{
    @Inject(method = "loadAllWorlds", at = @At("HEAD"))
    private void onLoadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String generatorOptions, CallbackInfo ci)
    {
        CarpetServer.onServerLoaded((IntegratedServer) (Object) this);
    }
}
