package carpet.forge.mixin;

import carpet.forge.utils.CarpetProfiler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServer_profilerMixin
{
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;tickCounter:I", ordinal = 0, shift = At.Shift.AFTER))
    private void startTickProfiling(CallbackInfo ci)
    {
        if (CarpetProfiler.tick_health_requested != 0L)
        {
            CarpetProfiler.start_tick_profiling();
        }
    }
    
    @Inject(method = "tick", at = @At(value = "CONSTANT", args = "stringValue=save"))
    private void startAutoSaveProfiling(CallbackInfo ci)
    {
        CarpetProfiler.start_section(null, "Autosave");
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;saveAllWorlds(Z)V", shift = At.Shift.AFTER))
    private void stopAutoSaveProfiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }
    
    @Inject(method = "tick", at = @At("RETURN"))
    private void stopTickProfiling(CallbackInfo ci)
    {
        if (CarpetProfiler.tick_health_requested != 0L)
        {
            CarpetProfiler.end_tick_profiling((MinecraftServer) (Object) this);
        }
    }
    
    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "CONSTANT", args = "stringValue=connection"))
    private void startNetworkProfiling(CallbackInfo ci)
    {
        CarpetProfiler.start_section(null, "Network");
    }
    
    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "CONSTANT", args = "stringValue=commandFunctions"))
    private void stopNetworkProfiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }
}
