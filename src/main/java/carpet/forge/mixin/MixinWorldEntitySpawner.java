package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.SpawnReporter;
import carpet.forge.utils.mixininterfaces.IMixinEntityLiving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(WorldEntitySpawner.class)
public abstract class MixinWorldEntitySpawner
{
    @Shadow
    @Final
    private static int MOB_COUNT_DIV;

    @Shadow
    @Final
    private Set<ChunkPos> eligibleChunksForSpawning;

    @Shadow
    protected static BlockPos getRandomChunkPosition(World worldIn, int x, int z)
    {
        return null;
    }

    @Shadow
    public static boolean canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos)
    {
        return false;
    }

    /**
     * @author DeadlyMc
     * @reason Had to overwrite this method as there were issues with variable
     * handling using mixins.
     */
    @Overwrite()
    public int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate)
    {
        if (!spawnHostileMobs && !spawnPeacefulMobs)
        {
            return 0;
        }
        else
        {
            this.eligibleChunksForSpawning.clear();
            int i = 0;

            for (EntityPlayer entityplayer : worldServerIn.playerEntities)
            {
                if (!entityplayer.isSpectator())
                {
                    int j = MathHelper.floor(entityplayer.posX / 16.0D);
                    int k = MathHelper.floor(entityplayer.posZ / 16.0D);
                    int l = 8;

                    for (int i1 = -8; i1 <= 8; ++i1)
                    {
                        for (int j1 = -8; j1 <= 8; ++j1)
                        {
                            boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                            ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);

                            if (!this.eligibleChunksForSpawning.contains(chunkpos))
                            {
                                ++i;

                                if (!flag && worldServerIn.getWorldBorder().contains(chunkpos))
                                {
                                    PlayerChunkMapEntry playerchunkmapentry = worldServerIn.getPlayerChunkMap().getEntry(chunkpos.x, chunkpos.z);

                                    if (playerchunkmapentry != null && playerchunkmapentry.isSentToPlayers())
                                    {
                                        this.eligibleChunksForSpawning.add(chunkpos);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // [FCM] Start
            boolean optimizedDespawnRange = CarpetSettings.optimizedDespawnRange;
            ;
            if (i == 0 && optimizedDespawnRange) // Worlds without valid chunks are skipped.
            {
                return 0;
            }
            //[FCM] End

            int j4 = 0;
            BlockPos blockpos1 = worldServerIn.getSpawnPoint();

            // [FCM] Start
            int did = worldServerIn.provider.getDimensionType().getId();
            String level_suffix = (did == 0) ? "" : ((did < 0 ? " (N)" : " (E)"));
            // [FCM] End

            for (EnumCreatureType enumcreaturetype : EnumCreatureType.values())
            {
                // [FCM] Start
                String type_code = String.format("%s", enumcreaturetype);
                String group_code = type_code + level_suffix;
                if (SpawnReporter.track_spawns > 0L)
                {
                    SpawnReporter.overall_spawn_ticks.put(group_code, SpawnReporter.overall_spawn_ticks.get(group_code) + SpawnReporter.spawn_tries.get(type_code));
                }
                // [FCM] End
                if ((!enumcreaturetype.getPeacefulCreature() || spawnPeacefulMobs) && (enumcreaturetype.getPeacefulCreature() || spawnHostileMobs) && (!enumcreaturetype.getAnimal() || spawnOnSetTickRate))
                {
                    int k4 = worldServerIn.countEntities(enumcreaturetype, true);
                    //[FCM] Replaced:
                    // int l4 = enumcreaturetype.getMaxNumberOfCreature() * i / MOB_COUNT_DIV;
                    int l4 = (int) (Math.pow(2.0, (SpawnReporter.mobcap_exponent / 4)) * enumcreaturetype.getMaxNumberOfCreature() * i / MOB_COUNT_DIV);
                    SpawnReporter.mobcaps.get(did).put(enumcreaturetype, new Tuple<>(k4, l4));
                    int tries = SpawnReporter.spawn_tries.get(type_code);
                    if (SpawnReporter.track_spawns > 0L)
                    {
                        SpawnReporter.spawn_attempts.put(group_code, SpawnReporter.spawn_attempts.get(group_code) + tries);
                        SpawnReporter.spawn_cap_count.put(group_code, SpawnReporter.spawn_cap_count.get(group_code) + k4);
                    }
                    if (SpawnReporter.mock_spawns)
                    {
                        k4 = 0;
                    } // No mobcaps
                    // [FCM] End

                    if (k4 <= l4)
                    {
                        java.util.ArrayList<ChunkPos> shuffled = com.google.common.collect.Lists.newArrayList(this.eligibleChunksForSpawning);
                        java.util.Collections.shuffle(shuffled);
                        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                        // [FCM] Extra indentation -- Start.
                        for (int trie = 0; trie < tries; trie++)
                        {
                            // [FCM] New variable long.
                            long local_spawns = 0;

                            label134:

                            for (ChunkPos chunkpos1 : shuffled)
                            {
                                BlockPos blockpos = getRandomChunkPosition(worldServerIn, chunkpos1.x, chunkpos1.z);
                                int k1 = blockpos.getX();
                                int l1 = blockpos.getY();
                                int i2 = blockpos.getZ();
                                IBlockState iblockstate = worldServerIn.getBlockState(blockpos);

                                if (!iblockstate.isNormalCube())
                                {
                                    int j2 = 0;

                                    for (int k2 = 0; k2 < 3; ++k2)
                                    {
                                        int l2 = k1;
                                        int i3 = l1;
                                        int j3 = i2;
                                        int k3 = 6;
                                        Biome.SpawnListEntry biome$spawnlistentry = null;
                                        IEntityLivingData ientitylivingdata = null;
                                        int l3 = MathHelper.ceil(Math.random() * 4.0D);

                                        for (int i4 = 0; i4 < l3; ++i4)
                                        {
                                            l2 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6);
                                            i3 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1);
                                            j3 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6);
                                            blockpos$mutableblockpos.setPos(l2, i3, j3);
                                            float f = (float) l2 + 0.5F;
                                            float f1 = (float) j3 + 0.5F;

                                            if (!worldServerIn.isAnyPlayerWithinRangeAt((double) f, (double) i3, (double) f1, 24.0D) && blockpos1.distanceSq((double) f, (double) i3, (double) f1) >= 576.0D)
                                            {
                                                if (biome$spawnlistentry == null)
                                                {
                                                    biome$spawnlistentry = worldServerIn.getSpawnListEntryForTypeAt(enumcreaturetype, blockpos$mutableblockpos);

                                                    if (biome$spawnlistentry == null)
                                                    {
                                                        break;
                                                    }
                                                }

                                                if (worldServerIn.canCreatureTypeSpawnHere(enumcreaturetype, biome$spawnlistentry, blockpos$mutableblockpos) && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(biome$spawnlistentry.entityClass), worldServerIn, blockpos$mutableblockpos))
                                                {
                                                    EntityLiving entityliving;

                                                    try
                                                    {
                                                        entityliving = biome$spawnlistentry.newInstance(worldServerIn);
                                                    }
                                                    catch (Exception exception)
                                                    {
                                                        exception.printStackTrace();
                                                        return j4;
                                                    }

                                                    entityliving.setLocationAndAngles((double) f, (double) i3, (double) f1, worldServerIn.rand.nextFloat() * 360.0F, 0.0F);

                                                    net.minecraftforge.fml.common.eventhandler.Event.Result canSpawn = net.minecraftforge.event.ForgeEventFactory.canEntitySpawn(entityliving, worldServerIn, f, i3, f1, false);
                                                    if (canSpawn == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW || (canSpawn == net.minecraftforge.fml.common.eventhandler.Event.Result.DEFAULT && (entityliving.getCanSpawnHere() && entityliving.isNotColliding())))
                                                    {
                                                        if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(entityliving, worldServerIn, f, i3, f1))
                                                            ientitylivingdata = entityliving.onInitialSpawn(worldServerIn.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata);

                                                        if (entityliving.isNotColliding())
                                                        {
                                                            // [FCM] Replacing //worldServerIn.spawnEntity(entityliving);
                                                            if (optimizedDespawnRange && ((IMixinEntityLiving) entityliving).willImmediatelyDespawn()) // Added optimized despawn mobs causing netlag by Luflosi
                                                            {
                                                                entityliving.setDead();
                                                            }
                                                            else
                                                            {
                                                                ++local_spawns;
                                                                if (SpawnReporter.track_spawns > 0L)
                                                                {
                                                                    String species = EntityList.getEntityString(entityliving);
                                                                    SpawnReporter.registerSpawn(entityliving, type_code, species, blockpos$mutableblockpos);
                                                                }
                                                                if (SpawnReporter.mock_spawns)
                                                                {
                                                                    entityliving.setDead();
                                                                }
                                                                else
                                                                {
                                                                    worldServerIn.spawnEntity(entityliving);
                                                                }
                                                                // [FCM] End
                                                            }
                                                        }
                                                        else
                                                        {
                                                            entityliving.setDead();
                                                        }

                                                        if (j2 >= net.minecraftforge.event.ForgeEventFactory.getMaxSpawnPackSize(entityliving))
                                                        {
                                                            continue label134;
                                                        }
                                                    }

                                                    j4 += j2;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // [FCM] SpawnReporter
                            if (SpawnReporter.track_spawns > 0L)
                            {
                                if (local_spawns > 0)
                                {
                                    SpawnReporter.spawn_ticks_succ.put(group_code, SpawnReporter.spawn_ticks_succ.get(group_code) + 1L);
                                    SpawnReporter.spawn_ticks_spawns.put(group_code, SpawnReporter.spawn_ticks_spawns.get(group_code) + local_spawns);
                                }
                                else
                                {
                                    SpawnReporter.spawn_ticks_fail.put(group_code, SpawnReporter.spawn_ticks_fail.get(group_code) + 1L);
                                }
                            }

                        } // [FCM] Extra indentation -- End.
                    }
                    else // [FCM] Full mobcap.
                    {
                        if (SpawnReporter.track_spawns > 0L)
                        {
                            SpawnReporter.spawn_ticks_full.put(group_code, SpawnReporter.spawn_ticks_full.get(group_code) + SpawnReporter.spawn_tries.get(type_code));
                        }
                    }
                    // [FCM] End
                }
            }

            return j4;
        }
    }

}
