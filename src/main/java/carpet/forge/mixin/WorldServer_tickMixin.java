package carpet.forge.mixin;

import carpet.forge.helper.TickSpeed;
import carpet.forge.utils.CarpetProfiler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(WorldServer.class)
public abstract class WorldServer_tickMixin extends World
{
    protected WorldServer_tickMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
    {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }
    
    @Shadow
    @Final
    public PlayerChunkMap playerChunkMap;
    
    @Shadow
    protected abstract BlockPos adjustPosToNearbyEntity(BlockPos pos);
    
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
    
    //TODO: Check if this works properly
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;tickUpdates(Z)Z"))
    private boolean onTickTickUpdates(WorldServer worldServer, boolean runAllPending)
    {
        if (TickSpeed.process_entities)
            return this.tickUpdates(runAllPending);
        return false;
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

    @Redirect(
            method = "updateBlocks",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "Ljava/util/Iterator;hasNext()Z"),
            slice = @Slice(
                      from = @At(value = "INVOKE",
                              target = "Lnet/minecraft/world/WorldServer;isThundering()Z"),
                      to = @At(value = "INVOKE",
                              target = "Lnet/minecraft/world/chunk/Chunk;enqueueRelightChecks()V"))
    )
    private boolean cancelForLoop(Iterator iterator)
    {
        return false;
    }

    @Inject(
            method = "updateBlocks",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void newForLoop(CallbackInfo ci, int i, boolean flag, boolean flag1)
    {
        for (Iterator<Chunk> iterator = getPersistentChunkIterable(this.playerChunkMap.getChunkIterator()); iterator.hasNext(); this.profiler.endSection())
        {
            this.profiler.startSection("getChunk");
            Chunk chunk = iterator.next();
            int j = chunk.x * 16;
            int k = chunk.z * 16;
            this.profiler.endStartSection("checkNextLight");
            chunk.enqueueRelightChecks();
            this.profiler.endStartSection("tickChunk");
            chunk.onTick(false);
            if (!TickSpeed.process_entities)
            { // skipping the rest of the block processing
                this.profiler.endSection();
                continue;
            }
            this.profiler.endStartSection("thunder");
        
            if (this.provider.canDoLightning(chunk) && flag && flag1 && this.rand.nextInt(100000) == 0)
            {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int l = this.updateLCG >> 2;
                BlockPos blockpos = this.adjustPosToNearbyEntity(new BlockPos(j + (l & 15), 0, k + (l >> 8 & 15)));
            
                if (this.isRainingAt(blockpos))
                {
                    DifficultyInstance difficultyinstance = this.getDifficultyForLocation(blockpos);
                
                    if (this.getGameRules().getBoolean("doMobSpawning") && this.rand.nextDouble() < (double)difficultyinstance.getAdditionalDifficulty() * 0.01D)
                    {
                        EntitySkeletonHorse entityskeletonhorse = new EntitySkeletonHorse(this);
                        entityskeletonhorse.setTrap(true);
                        entityskeletonhorse.setGrowingAge(0);
                        entityskeletonhorse.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                        this.spawnEntity(entityskeletonhorse);
                        this.addWeatherEffect(new EntityLightningBolt(this, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), true));
                    }
                    else
                    {
                        this.addWeatherEffect(new EntityLightningBolt(this, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), false));
                    }
                }
            }
        
            this.profiler.endStartSection("iceandsnow");
        
            if (this.provider.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0)
            {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int j2 = this.updateLCG >> 2;
                BlockPos blockpos1 = this.getPrecipitationHeight(new BlockPos(j + (j2 & 15), 0, k + (j2 >> 8 & 15)));
                BlockPos blockpos2 = blockpos1.down();
            
                if (this.isAreaLoaded(blockpos2, 1)) // Forge: check area to avoid loading neighbors in unloaded chunks
                    if (this.canBlockFreezeNoWater(blockpos2))
                    {
                        this.setBlockState(blockpos2, Blocks.ICE.getDefaultState());
                    }
            
                if (flag && this.canSnowAt(blockpos1, true))
                {
                    this.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState());
                }
            
                if (flag && this.getBiome(blockpos2).canRain())
                {
                    this.getBlockState(blockpos2).getBlock().fillWithRain(this, blockpos2);
                }
            }
        
            this.profiler.endStartSection("tickBlocks");
        
            if (i > 0)
            {
                for (ExtendedBlockStorage extendedblockstorage : chunk.getBlockStorageArray())
                {
                    if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE && extendedblockstorage.needsRandomTick())
                    {
                        for (int i1 = 0; i1 < i; ++i1)
                        {
                            this.updateLCG = this.updateLCG * 3 + 1013904223;
                            int j1 = this.updateLCG >> 2;
                            int k1 = j1 & 15;
                            int l1 = j1 >> 8 & 15;
                            int i2 = j1 >> 16 & 15;
                            IBlockState iblockstate = extendedblockstorage.get(k1, i2, l1);
                            Block block = iblockstate.getBlock();
                            this.profiler.startSection("randomTick");
                        
                            if (block.getTickRandomly())
                            {
                                block.randomTick(this, new BlockPos(k1 + j, i2 + extendedblockstorage.getYLocation(), l1 + k), iblockstate, this.rand);
                            }
                        
                            this.profiler.endSection();
                        }
                    }
                }
            }
        }
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
    
    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/WorldServer;tickUpdates(Z)Z"))
    private void startBlockProfiling(CallbackInfo ci)
    {
        if (TickSpeed.process_entities)
            CarpetProfiler.start_section(this.provider.getDimensionType().getName(), "blocks");
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/WorldServer;tickUpdates(Z)Z"))
    private void stopBlockProfilingAndStartBlockSectionProfiling(CallbackInfo ci)
    {
        if (TickSpeed.process_entities)
            CarpetProfiler.end_current_section();
        CarpetProfiler.start_section(this.provider.getDimensionType().getName(), "blocks");
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/WorldServer;updateBlocks()V"))
    private void stopBlockSectionProfiling(CallbackInfo ci)
    {
        CarpetProfiler.end_current_section();
    }
}
