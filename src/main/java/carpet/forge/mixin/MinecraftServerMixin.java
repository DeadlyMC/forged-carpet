package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.TickSpeed;
import carpet.forge.utils.CarpetProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
    @Shadow private boolean serverRunning;
    
    @Shadow
    public static long getCurrentTimeMillis()
    {
        return 0;
    }
    
    @Shadow protected long currentTime;
    
    @Shadow private long timeOfLastWarning;
    
    @Shadow @Final private static Logger LOGGER;
    
    @Shadow public WorldServer[] worlds;
    
    @Shadow public abstract void tick();
    
    @Shadow private boolean serverIsRunning;
    
    @Redirect(method = "run", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;serverRunning:Z"))
    private boolean cancelWhileLoop(MinecraftServer server)
    {
        return false;
    }
    
    @Inject(
            method = "run",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/server/MinecraftServer;applyServerIconToResponse(Lnet/minecraft/network/ServerStatusResponse;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void newWhileLoop(CallbackInfo ci, long i) throws InterruptedException
    {
        while (this.serverRunning)
        {
            // [FCM] Command tick
            if (TickSpeed.time_warp_start_time != 0)
            {
                if (TickSpeed.continueWarp())
                {
                    this.tick();
                    this.currentTime = getCurrentTimeMillis();
                    this.serverIsRunning = true;
                }
                continue;
            }
            // [FCM] End
            long k = getCurrentTimeMillis();
            long j = k - this.currentTime;
        
            if (j > 2000L && this.currentTime - this.timeOfLastWarning >= 15000L)
            {
                LOGGER.warn("Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", Long.valueOf(j), Long.valueOf(j / 50L));
                j = 2000L;
                this.timeOfLastWarning = this.currentTime;
            }
        
            if (j < 0L)
            {
                LOGGER.warn("Time ran backwards! Did the system time change?");
                j = 0L;
            }
        
            i += j;
            this.currentTime = k;
            boolean falling_behind = false; // [FCM] Watch dog crash fix
        
            if (this.worlds[0].areAllPlayersAsleep())
            {
                this.tick();
                i = 0L;
            }
            else
            {
                boolean keeping_up = false; // [FCM] Watch dog crash fix
                while (i > TickSpeed.mspt)//50L)
                {
                    i -= TickSpeed.mspt;//50L;
                    if (CarpetSettings.watchdogFix && keeping_up)
                    {
                        this.currentTime = getCurrentTimeMillis();
                        this.serverIsRunning = true;
                        falling_behind = true;
                    }
                    this.tick();
                    keeping_up = true;
                }
            }
    
            if (falling_behind)
            {
                Thread.sleep(1L); /* carpet mod 50L */
            }
            else
            {
                Thread.sleep(Math.max(1L, TickSpeed.mspt - i));//50L - i));
            }
            this.serverIsRunning = true;
        }
    }
    
    @Inject(
            method = "tick",
            at = @At(value = "FIELD", ordinal = 0,
                    target = "Lnet/minecraft/server/MinecraftServer;tickCounter:I")
    )
    private void startTickProfiling(CallbackInfo ci)
    {
        if (CarpetProfiler.tick_health_requested != 0L)
        {
            CarpetProfiler.start_tick_profiling();
        }
    }
    
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/ServerStatusResponse;invalidateJson()V"))
    )
    private void startAutoSaveProfiling(CallbackInfo ci)
    {
        CarpetProfiler.start_section(null, "Autosave");
    }
    
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/profiler/Profiler;endSection()V"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/ServerStatusResponse;invalidateJson()V"))
    )
    private void stopAutoSaveProfiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void stopTickProfiling(CallbackInfo ci)
    {
        if (CarpetProfiler.tick_health_requested != 0L)
        {
            CarpetProfiler.end_tick_profiling((MinecraftServer)(Object)this);
        }
    }
    
    @Inject(
            method = "updateTimeLightAndEntities",
            at = @At(value = "INVOKE", ordinal = 1, shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityTracker;tick()V"))
    )
    private void startNetworkProfiling(CallbackInfo ci)
    {
        CarpetProfiler.start_section(null, "Network");
    }
    
    @Inject(
            method = "updateTimeLightAndEntities",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/server/management/PlayerList;onTick()V")
    )
    private void stopNetworkProfiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }
}
