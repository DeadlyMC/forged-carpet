package carpet.forge.utils.mixininterfaces;

import net.minecraft.world.biome.Biome;

import java.util.List;

public interface IMapGenEndCity
{
    List<Biome.SpawnListEntry> getSpawnList();
}
