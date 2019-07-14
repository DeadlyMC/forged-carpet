package carpet.forge.utils.mixininterfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.List;

public interface IMapGenScatteredFeature
{
    List<Biome.SpawnListEntry> getHuskSpawnList();
    
    boolean isTemple(BlockPos pos);
}
