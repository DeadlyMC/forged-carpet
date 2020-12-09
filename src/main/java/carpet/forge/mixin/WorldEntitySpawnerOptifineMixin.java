package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.fakes.IEntityLiving;
import carpet.forge.utils.SpawnReporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;

@Mixin(WorldEntitySpawner.class)
public abstract class WorldEntitySpawnerOptifineMixin
{
    @Shadow
    @Final
    private static int MOB_COUNT_DIV;
    
    @Dynamic
    @SuppressWarnings("ShadowTarget")
    @Shadow(remap = false)
    public int countChunkPos;
    
    @Unique private WorldServer world;
    @Unique private String level_suffix;
    @Unique private String type_code;
    @Unique private int did;
    @Unique private EnumCreatureType creatureType;
    @Unique private int chunksCount;
    @Unique private int totalMobcap;
    @Unique private int localSpawns;
    @Unique private BlockPos.MutableBlockPos mutablePos;
    
    @Inject(
            method = "findChunksForSpawning",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldServer;getSpawnPoint()Lnet/minecraft/util/math/BlockPos;"),
            cancellable = true
    )
    private void skipInvalidChunks(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> cir)
    {
        if (this.countChunkPos == 0 && CarpetSettings.optimizedDespawnRange) // worlds without valid chunks are skipped.
        {
            cir.setReturnValue(0);
        }
        
        if (this.world == null)
        {
            this.world = worldServerIn;
            this.chunksCount = this.countChunkPos;
            this.did = worldServerIn.provider.getDimensionType().getId();
            this.level_suffix = (did==0)?"":((did<0?" (N)":" (E)"));
        }
    }
    
    @Inject(
            method = "findChunksForSpawning",
            at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/entity/EnumCreatureType;getPeacefulCreature()Z"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void spawnTicks(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate,
            CallbackInfoReturnable<Integer> cir, boolean updateEligibleChunks, EntityPlayer player, int j4, BlockPos blockpos1,
            @Coerce BlockPos blockPosM /*Optifine class*/, BlockPos.MutableBlockPos blockpos$mutableblockpos, EnumCreatureType var11[], int var12,
            int var13, EnumCreatureType enumcreaturetype)
    {
        this.creatureType = enumcreaturetype;
        this.type_code = String.format("%s", enumcreaturetype);
        String group_code = this.type_code + this.level_suffix;
        if (SpawnReporter.track_spawns > 0L)
        {
            SpawnReporter.overall_spawn_ticks.put(group_code, SpawnReporter.overall_spawn_ticks.get(group_code) + SpawnReporter.spawn_tries.get(this.type_code));
        }
    }
    
    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EnumCreatureType;getMaxNumberOfCreature()I"))
    private int changeMaxCreatures(EnumCreatureType type)
    {
        int max = (int) (Math.pow(2.0,(SpawnReporter.mobcap_exponent/4)) * type.getMaxNumberOfCreature());
        this.totalMobcap = max * chunksCount / MOB_COUNT_DIV;
        return max;
    }
    
    // This one is tricky to target, we want the number of existing mobs right where it's compared to the mobcap.
    // The resulting code looks like:
    //   int l4 = enumcreaturetype.func_75601_b() * this.countChunkPos / field_180268_a;
    //   k4 = this.localvar$zdk000$modifyExistingCount(k4);
    //   if (k4 <= l4) {
    @ModifyVariable(method = "findChunksForSpawning", at = @At(value = "LOAD", ordinal = 0), index = 15)
    private int modifyExistingCount(int existingCount)
    {
        String group_code = creatureType + level_suffix;
        if (SpawnReporter.mobcaps.get(this.did) != null)
        {
            SpawnReporter.mobcaps.get(this.did).put(this.creatureType, new Tuple<>(existingCount, this.totalMobcap));
            if (SpawnReporter.track_spawns > 0L)
            {
                int tries = SpawnReporter.spawn_tries.get(type_code);
                if (existingCount > totalMobcap)
                    SpawnReporter.spawn_ticks_full.put(group_code, SpawnReporter.spawn_ticks_full.get(group_code) + tries);
                
                SpawnReporter.spawn_attempts.put(group_code, SpawnReporter.spawn_attempts.get(group_code) + tries);
                SpawnReporter.spawn_cap_count.put(group_code, SpawnReporter.spawn_cap_count.get(group_code) + existingCount);
            }
            if (SpawnReporter.mock_spawns)
                return 0;
        }
        return existingCount;
    }
    
    // This creates a special iterator that respects the number of tries (SpawnReporter.spawn_tries)
    // which acts like a for (int i = 0; i < tries; i++) loop around the spawning code
    // the iterator executes the code below when it is finished
    // Thanks to @skyrising for this mixin.
    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;", remap = false))
    private Iterator<ChunkPos> getChunkIterator(Collection<ChunkPos> set)
    {
        return SpawnReporter.createChunkIterator(set, type_code, () -> {
            if (SpawnReporter.track_spawns <= 0L)
                return;
            String group_code = creatureType + level_suffix;
            if (localSpawns > 0)
            {
                SpawnReporter.spawn_ticks_succ.put(group_code, SpawnReporter.spawn_ticks_succ.get(group_code) + 1L);
                SpawnReporter.spawn_ticks_spawns.put(group_code, SpawnReporter.spawn_ticks_spawns.get(group_code) + localSpawns);
            }
            else
            {
                SpawnReporter.spawn_ticks_fail.put(group_code, SpawnReporter.spawn_ticks_fail.get(group_code) + 1L);
            }
        });
    }
    
    @Inject(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureMutablePos(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate,
            CallbackInfoReturnable<Integer> cir, boolean updateEligibleChunks, EntityPlayer player, int j4, BlockPos blockpos1,
            @Coerce BlockPos blockPosM /*Optifine class*/, BlockPos.MutableBlockPos blockpos$mutableblockpos)
    {
        this.mutablePos = blockpos$mutableblockpos;
    }
    
    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private boolean spawnEntity(WorldServer worldServer, Entity entity)
    {
        // Added optimized despawn mobs causing netlag by Luflosi
        if (CarpetSettings.optimizedDespawnRange && ((IEntityLiving) entity).willImmediatelyDespawn())
        {
            entity.setDead();
            return false;
        }
        this.localSpawns++;
        if (SpawnReporter.track_spawns > 0L)
        {
            String species = EntityList.getEntityString(entity);
            SpawnReporter.registerSpawn((EntityLiving) entity, type_code, species, this.mutablePos);
        }
        if (SpawnReporter.mock_spawns)
        {
            entity.setDead();
            return false;
        }
        return worldServer.spawnEntity(entity);
    }
}
