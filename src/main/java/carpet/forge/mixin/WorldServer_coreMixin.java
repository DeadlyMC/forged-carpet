package carpet.forge.mixin;

import carpet.forge.helper.TickSpeed;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(WorldServer.class)
public abstract class WorldServer_coreMixin extends World
{
    @Shadow protected abstract void sendQueuedBlockEvents();
    
    protected WorldServer_coreMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
    {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"))
    private void mobSpawnerProfiling(Profiler profiler, String name)
    {
        if (TickSpeed.process_entities)
            profiler.startSection(name);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z", ordinal = 1))
    private boolean doMobSpawning(GameRules gameRules, String name)
    {
        return gameRules.getBoolean(name) && TickSpeed.process_entities;
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;setWorldTotalTime(J)V"))
    private void setWorldTotalTime(WorldInfo worldInfo, long time)
    {
        if (TickSpeed.process_entities)
            worldInfo.setWorldTotalTime(time);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z", ordinal = 2))
    private boolean doDaylightCycle(GameRules gameRules, String name)
    {
        return gameRules.getBoolean(name) && TickSpeed.process_entities;
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0), slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=doDaylightCycle", ordinal = 1),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;tickUpdates(Z)Z")))
    private void tickPendingProfiling(Profiler profiler, String name)
    {
        if (TickSpeed.process_entities)
            profiler.endStartSection(name);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;tickUpdates(Z)Z"))
    private boolean tickUpdates(WorldServer worldServer, boolean runAllPending)
    {
        return TickSpeed.process_entities && worldServer.tickUpdates(runAllPending);
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerChunkMap;tick()V", shift = At.Shift.AFTER),
            cancellable = true)
    private void cancelIfNotProcessingEntities(CallbackInfo ci)
    {
        if (!TickSpeed.process_entities)
        {
            this.profiler.endSection();
            this.sendQueuedBlockEvents();
            ci.cancel();
        }
    }
    
    // Thanks to @skyrising for providing this mixin.
    @Redirect(
            method = "updateBlocks",
            at = @At(value = "INVOKE", ordinal = 1,
                    target = "Lnet/minecraft/server/management/PlayerChunkMap;getChunkIterator()Ljava/util/Iterator;")
    )
    private Iterator<Chunk> getChunkIterator(PlayerChunkMap map) {
        Iterator<Chunk> iterator = map.getChunkIterator();
        if (!TickSpeed.process_entities) {
            while (iterator.hasNext()) {
                this.profiler.startSection("getChunk");
                Chunk chunk = iterator.next();
                this.profiler.endStartSection("checkNextLight");
                chunk.enqueueRelightChecks();
                this.profiler.endStartSection("tickChunk");
                chunk.onTick(false);
                this.profiler.endSection();
            }
            // now the iterator is done and the vanilla loop won't run
            // this act's like a `continue` after chunk.onTick(false)
        }
        return iterator;
    }
}
