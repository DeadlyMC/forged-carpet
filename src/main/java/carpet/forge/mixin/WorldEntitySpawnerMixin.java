package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.fakes.IEntityLiving;
import carpet.forge.utils.SpawnReporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;

@Mixin(WorldEntitySpawner.class)
public abstract class WorldEntitySpawnerMixin
{
    @Shadow @Final private static int MOB_COUNT_DIV;
    
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
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void skipInvalidChunks(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, CallbackInfoReturnable<Integer> cir, int chunkCount)
    {
        this.chunksCount = chunkCount;
        if (chunkCount == 0 && CarpetSettings.optimizedDespawnRange) // worlds without valid chunks are skipped.
        {
            cir.setReturnValue(0);
            return;
        }
        
        if (this.world == null)
        {
            this.world = worldServerIn;
            this.did = worldServerIn.provider.getDimension();
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
            CallbackInfoReturnable<Integer> cir, int i, int j4, BlockPos blockpos1, EnumCreatureType var8[], int var9, int var10,
            EnumCreatureType enumcreaturetype)
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
    // The comparison looks like ILOAD 12, ILOAD 13, IF_ICMPGT -> index = 12
    // If @At would actually respect the slice here we could use that with ordinal=0
    // but apparently 12 is also used in the loop that counts chunks, so ordinal needs to be 4.
    // The resulting code looks like:
    //   int l4 = this.redirect$zej000$getMaxNumberOfCreature(enumcreaturetype) * i / MOB_COUNT_DIV;
    //   k4 = this.localvar$zej000$modifyExistingCount(k4);
    //   if (k4 <= l4) {
    // Thanks to @skyrising for this mixin.
    @ModifyVariable(method = "findChunksForSpawning", at = @At(value = "LOAD", ordinal = 4), index = 12)
    private int modifyExistingCount(int existingCount)
    {
        String group_code = creatureType + level_suffix;
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
        return existingCount;
    }
    
    // This creates a special iterator that respects the number of tries (SpawnReporter.spawn_tries)
    // which acts like a for (int i = 0; i < tries; i++) loop around the spawning code
    // the iterator executes the code below when it is finished
    // Thanks to @skyrising for this mixin.
    @Redirect(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;iterator()Ljava/util/Iterator;", remap = false))
    private Iterator<ChunkPos> getChunkIterator(ArrayList<ChunkPos> set)
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
    
    @Inject(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureMutablePos(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate,
            CallbackInfoReturnable<Integer> cir, int i, int j4, BlockPos blockpos1, EnumCreatureType var8[], int var9, int var10,
            EnumCreatureType enumcreaturetype, int k4, int l4, ArrayList shuffled, BlockPos.MutableBlockPos mutableBlockPos)
    {
        this.mutablePos = mutableBlockPos;
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
