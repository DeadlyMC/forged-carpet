package carpet.forge.mixin;

import carpet.forge.CarpetServer;
import carpet.forge.CarpetSettings;
import carpet.forge.helper.TickSpeed;
import carpet.forge.utils.CarpetProfiler;
import carpet.forge.utils.TickingArea;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer
{

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    public WorldServer[] worlds;

    @Shadow
    protected long currentTime;

    @Shadow
    private boolean serverIsRunning;

    @Shadow
    private long timeOfLastWarning;

    @Shadow
    private boolean serverRunning;

    @Shadow
    public static long getCurrentTimeMillis()
    {
        return 0;
    }

    @Shadow
    public abstract boolean init() throws IOException;

    @Shadow
    protected abstract void outputPercentRemaining(String message, int percent);

    @Shadow
    protected abstract void clearCurrentTask();

    @Shadow
    public abstract boolean isServerRunning();

    @Shadow
    protected abstract void setUserMessage(String message);

    @Shadow
    public abstract void tick();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void onMinecraftServer(File anvilFileIn, Proxy proxyIn, DataFixer dataFixerIn, YggdrasilAuthenticationService authServiceIn, MinecraftSessionService sessionServiceIn, GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn, CallbackInfo ci)
    {
        CarpetServer.init((MinecraftServer) (Object) this);
    }

    @Redirect(method = "run", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;serverRunning:Z"))
    private boolean cancelServerRunningLoop(MinecraftServer server)
    {
        return false;
    }

    @Inject(method = "run", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/server/MinecraftServer;applyServerIconToResponse(Lnet/minecraft/network/ServerStatusResponse;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void newServerRunningLoop(CallbackInfo ci, long i) throws InterruptedException
    {
        while (this.serverRunning)
        {
            // [FCM] CommandTick
            //TODO Check if this check is necessary
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
            // End
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
            boolean falling_behind = false; // [FCM]

            if (this.worlds[0].areAllPlayersAsleep())
            {
                this.tick();
                i = 0L;
            }
            else
            {
                boolean keeping_up = false;
                while (i > TickSpeed.mspt)// 50L
                {
                    i -= TickSpeed.mspt;// 50L
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
                Thread.sleep(1L); // [FCM] 50L
            }
            else
            {
                Thread.sleep(Math.max(1L, TickSpeed.mspt - i)); // [FCM] 50L
            }
            this.serverIsRunning = true;
        }

    }

    @Inject(method = "tick", at = @At(value = "FIELD", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/MinecraftServer;tickCounter:I", ordinal = 0))
    private void carpetTickAndStartTickProfiling(CallbackInfo ci)
    {
        CarpetServer.tick((MinecraftServer) (Object) this);
        if (CarpetProfiler.tick_health_requested != 0L)
        {
            CarpetProfiler.start_tick_profiling();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/network/ServerStatusResponse;invalidateJson()V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;saveAllWorlds(Z)V")))
    private void startAutoSaveProfiling(CallbackInfo ci)
    {
        CarpetProfiler.start_section(null, "Autosave");
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/profiler/Profiler;endSection()V"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;saveAllWorlds(Z)V"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;tickTimeArray:[J")))
    private void stopAutoSaveProfiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void stopTickProfiling(CallbackInfo ci)
    {
        if (CarpetProfiler.tick_health_requested != 0L)
        {
            CarpetProfiler.end_tick_profiling((MinecraftServer) (Object) this);
        }
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraftforge/common/DimensionManager;unloadWorlds(Ljava/util/Hashtable;)V"))
    private void startNetworkProfiling(CallbackInfo ci)
    {
        CarpetProfiler.start_section(null, "Network");
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/server/management/PlayerList;onTick()V"))
    private void stopNetworkProfiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }

    // [FCM] TickingAreas
    @Inject(method = "loadAllWorlds", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/server/MinecraftServer;initialWorldChunkLoad()V"))
    private void onLoadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String generatorOptions, CallbackInfo ci)
    {
        CarpetServer.onLoadAllWorlds((MinecraftServer) (Object) this);
    }

    /**
     * @author DeadlyMC
     * @reason if statement arounds
     */
    @Overwrite
    public void initialWorldChunkLoad()
    {
        int i = 16;
        int j = 4;
        int k = 192;
        int l = 625;
        int i1 = 0;
        this.setUserMessage("menu.generatingTerrain");
        int j1 = 0;
        // [FCM] TickingAreas - Start
        if (CarpetSettings.tickingAreas)
        {
            TickingArea.initialChunkLoad((MinecraftServer) (Object) this, true);
        }
        // [FCM] TickingAreas - End

        // [FCM] DisableSpawnChunks - if statement around
        if (!CarpetSettings.disableSpawnChunks)
        {
            LOGGER.info("Preparing start region for level 0");
            WorldServer worldserver = net.minecraftforge.common.DimensionManager.getWorld(j1);
            BlockPos blockpos = worldserver.getSpawnPoint();
            long k1 = getCurrentTimeMillis();

            for (int l1 = -192; l1 <= 192 && this.isServerRunning(); l1 += 16)
            {
                for (int i2 = -192; i2 <= 192 && this.isServerRunning(); i2 += 16)
                {
                    long j2 = getCurrentTimeMillis();

                    if (j2 - k1 > 1000L)
                    {
                        this.outputPercentRemaining("Preparing spawn area", i1 * 100 / 625);
                        k1 = j2;
                    }

                    ++i1;
                    worldserver.getChunkProvider().provideChunk(blockpos.getX() + l1 >> 4, blockpos.getZ() + i2 >> 4);
                }
            }
        } // [FCM] End indent

        this.clearCurrentTask();
    }

    @Inject(method = "saveAllWorlds", at = @At("TAIL"))
    private void onSaveAllWorlds(boolean isSilent, CallbackInfo ci)
    {
        CarpetServer.onWorldsSaved((MinecraftServer) (Object) this);
    }

}
