package carpet.forge.mixin;

import carpet.forge.helper.TickSpeed;
import carpet.forge.utils.CarpetProfiler;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class WorldServer_profilerMixin extends World
{
    protected WorldServer_profilerMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
    {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }
    
    @Inject(method = "tick", at = @At(value = "CONSTANT", args = "stringValue=mobSpawner"))
    private void startSpawningProfiling(CallbackInfo ci)
    {
        if (TickSpeed.process_entities)
            CarpetProfiler.start_section(this.provider.getDimensionType().getName(), "spawning");
    }
    
    @Inject(method = "tick", at = @At(value = "CONSTANT", args = "stringValue=chunkSource"))
    private void stopSpawningProfiling(CallbackInfo ci)
    {
        if (TickSpeed.process_entities) CarpetProfiler.end_current_section();
    }
    
    @Inject(method = "tick", at = @At(value = "CONSTANT", args = "stringValue=tickPending"))
    private void startBlockProfiling(CallbackInfo ci)
    {
        if (TickSpeed.process_entities)
            CarpetProfiler.start_section(this.provider.getDimensionType().getName(), "blocks");
    }
    
    @Inject(method = "tick", at = @At(value = "CONSTANT", args = "stringValue=tickBlocks"))
    private void stopBlockProfilingAndStartBlock2Profiling(CallbackInfo ci)
    {
        if (TickSpeed.process_entities) CarpetProfiler.end_current_section();
        String world_name = this.provider.getDimensionType().getName();
        CarpetProfiler.start_section(world_name, "blocks");
    }
    
    @Inject(method = "tick", at = @At(value = "CONSTANT", args = "stringValue=chunkMap"))
    private void stopBlock2Profiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }
}
