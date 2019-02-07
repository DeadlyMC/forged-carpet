package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.TickSpeed;
import carpet.forge.utils.CarpetProfiler;
import carpet.forge.utils.LightingEngine;
import carpet.forge.utils.mixininterfaces.IChunk;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Mixin(World.class)
public abstract class MixinWorld implements IWorld {

    @Shadow @Final public WorldProvider provider;

    @Shadow @Final public Profiler profiler;

    @Shadow @Final public List<Entity> weatherEffects;

    @Shadow public abstract void removeEntity(Entity entityIn);

    @Shadow @Final public List<Entity> loadedEntityList;

    @Shadow @Final protected List<Entity> unloadedEntityList;

    @Shadow protected abstract boolean isChunkLoaded(int x, int z, boolean allowEmpty);

    @Shadow public abstract Chunk getChunk(int chunkX, int chunkZ);

    @Shadow public abstract void onEntityRemoved(Entity entityIn);

    @Shadow protected abstract void tickPlayers();

    @Shadow public abstract void updateEntity(Entity ent);

    @Shadow private boolean processingLoadedTiles;

    @Shadow @Final private List<TileEntity> tileEntitiesToBeRemoved;

    @Shadow @Final public List<TileEntity> tickableTileEntities;

    @Shadow @Final public List<TileEntity> loadedTileEntityList;

    @Shadow public abstract boolean isBlockLoaded(BlockPos pos, boolean allowEmpty);

    @Shadow @Final private WorldBorder worldBorder;

    @Shadow public abstract void removeTileEntity(BlockPos pos);

    @Shadow public abstract Chunk getChunk(BlockPos pos);

    @Shadow public abstract boolean isBlockLoaded(BlockPos pos);

    @Shadow @Final private List<TileEntity> addedTileEntityList;

    @Shadow public abstract boolean addTileEntity(TileEntity tile);

    @Shadow public abstract void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags);

    @Shadow @Final public Random rand;
    @Final
    @Mutable
    public LightingEngine lightingEngine;

    @Override
    public LightingEngine getLightingEngine(){
        return this.lightingEngine;
    }

    @Inject(method = "<init>" ,at = @At(value = "RETURN"))
    private void initLightEngine(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client, CallbackInfo ci){
        this.lightingEngine = new LightingEngine((World)(Object)this);
    }

    // [FCM ]modified for fillUpdates = false
    @Redirect(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState setBlockStateCarpet(Chunk chunk, BlockPos pos, IBlockState state, BlockPos methodPos, IBlockState newState, int flags){
        // [FCM] Carpet added flag
        return ((IChunk) chunk).setBlockState_carpet(methodPos, newState, ((flags & 128) != 0) ?true:false);
    }
    /**
     * @author DeadlyMC
     * @reason For some reason injection causes a crash.Maybe because CarpetProfiler.end_current_section cannot
     *         find current section and the code included extra indents too.
     */
    @Overwrite
    public void updateEntities()
    {
        this.profiler.startSection("entities");
        this.profiler.startSection("global");

        for (int i = 0; i < this.weatherEffects.size(); ++i)
        {
            Entity entity = this.weatherEffects.get(i);

            try
            {
                if(entity.updateBlocked) continue;
                ++entity.ticksExisted;
                entity.onUpdate();
            }
            catch (Throwable throwable2)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable2, "Ticking entity");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");

                if (entity == null)
                {
                    crashreportcategory.addCrashSection("Entity", "~~NULL~~");
                }
                else
                {
                    entity.addEntityCrashInfo(crashreportcategory);
                }

                if (net.minecraftforge.common.ForgeModContainer.removeErroringEntities)
                {
                    net.minecraftforge.fml.common.FMLLog.log.fatal("{}", crashreport.getCompleteReport());
                    removeEntity(entity);
                }
                else
                    throw new ReportedException(crashreport);
            }

            if (entity.isDead)
            {
                this.weatherEffects.remove(i--);
            }
        }
        // [FCM] Start
        String world_name = this.provider.getDimensionType().getName();
        CarpetProfiler.start_section(world_name, "entities");
        // [FCM] End

        this.profiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int k = 0; k < this.unloadedEntityList.size(); ++k)
        {
            Entity entity1 = this.unloadedEntityList.get(k);
            int j = entity1.chunkCoordX;
            int k1 = entity1.chunkCoordZ;

            if (entity1.addedToChunk && this.isChunkLoaded(j, k1, true))
            {
                this.getChunk(j, k1).removeEntity(entity1);
            }
        }

        for (int l = 0; l < this.unloadedEntityList.size(); ++l)
        {
            this.onEntityRemoved(this.unloadedEntityList.get(l));
        }

        this.unloadedEntityList.clear();
        this.tickPlayers();
        this.profiler.endStartSection("regular");

        for (int i1 = 0; i1 < this.loadedEntityList.size(); ++i1)
        {
            Entity entity2 = this.loadedEntityList.get(i1);
            CarpetProfiler.start_entity_section(world_name, entity2); // [FCM] CarpetProfiler start
            Entity entity3 = entity2.getRidingEntity();

            if (entity3 != null)
            {
                if (!entity3.isDead && entity3.isPassenger(entity2))
                {
                    continue;
                }

                entity2.dismountRidingEntity();
            }

            this.profiler.startSection("tick");

            if (!entity2.isDead && !(entity2 instanceof EntityPlayerMP))
            {
                try
                {
                    if (TickSpeed.process_entities) // [FCM] if statement around
                    {
                        net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackStart(entity2);
                        this.updateEntity(entity2);
                        net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackEnd(entity2);
                    }
                }
                catch (Throwable throwable1)
                {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Entity being ticked");
                    entity2.addEntityCrashInfo(crashreportcategory1);
                    if (net.minecraftforge.common.ForgeModContainer.removeErroringEntities)
                    {
                        net.minecraftforge.fml.common.FMLLog.log.fatal("{}", crashreport1.getCompleteReport());
                        removeEntity(entity2);
                    }
                    else
                        throw new ReportedException(crashreport1);
                }
            }

            this.profiler.endSection();
            this.profiler.startSection("remove");

            if (entity2.isDead)
            {
                int l1 = entity2.chunkCoordX;
                int i2 = entity2.chunkCoordZ;

                if (entity2.addedToChunk && this.isChunkLoaded(l1, i2, true))
                {
                    this.getChunk(l1, i2).removeEntity(entity2);
                }

                this.loadedEntityList.remove(i1--);
                this.onEntityRemoved(entity2);
            }
            CarpetProfiler.end_current_entity_section(); // [FCM] CarpetProfiler end

            this.profiler.endSection();
        }
        // [FCM] Start
        CarpetProfiler.end_current_section();
        CarpetProfiler.start_section(world_name, "tileentities");
        // [FCM] End

        this.profiler.endStartSection("blockEntities");

        this.processingLoadedTiles = true; //FML Move above remove to prevent CMEs

        if (!this.tileEntitiesToBeRemoved.isEmpty())
        {
            for (Object tile : tileEntitiesToBeRemoved)
            {
                ((TileEntity)tile).onChunkUnload();
            }

            // forge: faster "contains" makes this removal much more efficient
            java.util.Set<TileEntity> remove = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
            remove.addAll(tileEntitiesToBeRemoved);
            this.tickableTileEntities.removeAll(remove);
            this.loadedTileEntityList.removeAll(remove);
            this.tileEntitiesToBeRemoved.clear();
        }

        Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();

        boolean profilingEnabled = this.profiler.profilingEnabled;
        while (iterator.hasNext())
        {
            TileEntity tileentity = iterator.next();
            CarpetProfiler.start_tileentity_section(world_name, tileentity);

            if (!tileentity.isInvalid() && tileentity.hasWorld())
            {
                BlockPos blockpos = tileentity.getPos();

                if (this.isBlockLoaded(blockpos, false) && this.worldBorder.contains(blockpos)) //Forge: Fix TE's getting an extra tick on the client side....
                {
                    try
                    {
                        if (TickSpeed.process_entities)
                        {
                            //[FCM] Added condition - same number of check than in 1.12.1
                            if (profilingEnabled)
                            {
                                this.profiler.startSection(tileentity.getClass().getSimpleName());
                                ((ITickable)tileentity).update();
                                this.profiler.endSection();
                            }
                            else
                            {
                                ((ITickable)tileentity).update();
                            }

                            // Forge stuff
                            this.profiler.func_194340_a(() ->
                            {
                                return String.valueOf((Object)TileEntity.getKey(tileentity.getClass()));
                            });
                            net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE.trackStart(tileentity);
                            net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE.trackEnd(tileentity);
                        }
                    }
                    catch (Throwable throwable)
                    {
                        CrashReport crashreport2 = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                        CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Block entity being ticked");
                        tileentity.addInfoToCrashReport(crashreportcategory2);
                        if (net.minecraftforge.common.ForgeModContainer.removeErroringTileEntities)
                        {
                            net.minecraftforge.fml.common.FMLLog.log.fatal("{}", crashreport2.getCompleteReport());
                            tileentity.invalidate();
                            this.removeTileEntity(tileentity.getPos());
                        }
                        else
                            throw new ReportedException(crashreport2);
                    }
                }
            }

            if (tileentity.isInvalid())
            {
                iterator.remove();
                this.loadedTileEntityList.remove(tileentity);

                if (this.isBlockLoaded(tileentity.getPos()))
                {
                    //Forge: Bugfix: If we set the tile entity it immediately sets it in the chunk, so we could be desyned
                    Chunk chunk = this.getChunk(tileentity.getPos());
                    if (chunk.getTileEntity(tileentity.getPos(), net.minecraft.world.chunk.Chunk.EnumCreateEntityType.CHECK) == tileentity)
                        chunk.removeTileEntity(tileentity.getPos());
                }
            }
            CarpetProfiler.end_current_entity_section();
        }

        this.processingLoadedTiles = false;
        this.profiler.endStartSection("pendingBlockEntities");

        if (!this.addedTileEntityList.isEmpty())
        {
            for (int j1 = 0; j1 < this.addedTileEntityList.size(); ++j1)
            {
                TileEntity tileentity1 = this.addedTileEntityList.get(j1);

                if (!tileentity1.isInvalid())
                {
                    if (!this.loadedTileEntityList.contains(tileentity1))
                    {
                        this.addTileEntity(tileentity1);
                    }

                    if (this.isBlockLoaded(tileentity1.getPos()))
                    {
                        Chunk chunk = this.getChunk(tileentity1.getPos());
                        IBlockState iblockstate = chunk.getBlockState(tileentity1.getPos());
                        chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                        this.notifyBlockUpdate(tileentity1.getPos(), iblockstate, iblockstate, 3);
                    }
                }
            }

            this.addedTileEntityList.clear();
        }
        CarpetProfiler.end_current_section();

        this.profiler.endSection();
        this.profiler.endSection();
    }

    @Inject(method = "checkLightFor", at = @At(value = "HEAD"), cancellable = true)
    private void onCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        if (CarpetSettings.newLight){
            this.lightingEngine.scheduleLightUpdate(lightType, pos);
            cir.setReturnValue(true);
        }
    }

    // [FCM] CommandRNG stuff
    public long getRandSeed(){
        try
        {
            Field field = Random.class.getDeclaredField("seed");
            field.setAccessible(true);
            AtomicLong scrambledSeed = (AtomicLong) field.get(rand);   //this needs to be XOR'd with 0x5DEECE66DL
            return scrambledSeed.get();
            // Minecraft.getMinecraft().player.sendChatMessage(chunk.x + ", " + chunk.z + ", seed " + theSeed);
        } catch (Exception e) {}

        return 0;
    }
}
