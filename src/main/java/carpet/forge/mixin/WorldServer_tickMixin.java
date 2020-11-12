package carpet.forge.mixin;

import carpet.forge.helper.TickSpeed;
import carpet.forge.utils.CarpetProfiler;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(WorldServer.class)
public abstract class WorldServer_tickMixin extends World
{
    protected WorldServer_tickMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
    {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"))
    private void onTickMobSpawner(Profiler profiler, String name)
    {
        if (TickSpeed.process_entities)
            profiler.startSection(name);
    }
    
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"))
    )
    private boolean onTickGetBoolean1(GameRules gameRules, String name)
    {
        return gameRules.getBoolean(name) && TickSpeed.process_entities;
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;setWorldTotalTime(J)V"))
    private void onTickSetTotalTime(WorldInfo worldInfo, long time)
    {
        if (TickSpeed.process_entities)
            worldInfo.setWorldTotalTime(time);
    }
    
    @Redirect(method = "tick",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/storage/WorldInfo;setWorldTotalTime(J)V"))
    )
    private boolean onTickGetBoolean2(GameRules gameRules, String name)
    {
        return gameRules.getBoolean(name) && TickSpeed.process_entities;
    }
    
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/storage/WorldInfo;setWorldTotalTime(J)V"))
    )
    private void onTickTickPending(Profiler profiler, String name)
    {
        if (TickSpeed.process_entities)
            profiler.endStartSection(name);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;tickUpdates(Z)Z", ordinal = 0))
    private boolean startAndEndBlockProfiling(WorldServer thisIn, boolean runAllPending)
    {
        if (TickSpeed.process_entities)
            CarpetProfiler.start_section(this.provider.getDimensionType().getName(), "blocks");
        boolean ret = this.tickUpdates(runAllPending);
        if (TickSpeed.process_entities)
            CarpetProfiler.end_current_section();
        return ret;
    }
    
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/management/PlayerChunkMap;tick()V"))
    )
    private void onTickVillage(Profiler profiler, String name)
    {
        if (TickSpeed.process_entities)
            profiler.endStartSection(name);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/VillageCollection;tick()V"))
    private void onTickVillageCollection(VillageCollection villageCollection)
    {
        if (TickSpeed.process_entities)
            villageCollection.tick();
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/VillageSiege;tick()V"))
    private void onTickVillageSiege(VillageSiege villageSiege)
    {
        if (TickSpeed.process_entities)
            villageSiege.tick();
    }
    
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/village/VillageSiege;tick()V"))
    )
    private void onTickPortalForcer(Profiler profiler, String name)
    {
        if (TickSpeed.process_entities)
            profiler.endStartSection(name);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Teleporter;removeStalePortalLocations(J)V"))
    private void onTickStalePortalLocations(Teleporter teleporter, long worldTime)
    {
        if (TickSpeed.process_entities)
            teleporter.removeStalePortalLocations(worldTime);
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
    
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V")
    )
    private void startSpawningProfiling(CallbackInfo ci)
    {
        if (TickSpeed.process_entities)
            CarpetProfiler.start_section(this.provider.getDimensionType().getName(), "spawning");
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0))
    private void stopSpawningProfiling(CallbackInfo ci)
    {
        if (TickSpeed.process_entities)
            CarpetProfiler.end_current_section();
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/WorldServer;tickUpdates(Z)Z"))
    private void startBlockSectionProfiling(CallbackInfo ci)
    {
        CarpetProfiler.start_section(this.provider.getDimensionType().getName(), "blocks");
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/WorldServer;updateBlocks()V"))
    private void stopBlockSectionProfiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }
}
