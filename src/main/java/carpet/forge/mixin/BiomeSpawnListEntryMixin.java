package carpet.forge.mixin;

import carpet.forge.utils.mixininterfaces.IMixinBiome;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

// Helper class for SpawnReporter
// Adds a new method required for SpawnReporter
@Mixin(Biome.SpawnListEntry.class)
public abstract class BiomeSpawnListEntryMixin extends WeightedRandom.Item implements IMixinBiome
{
    public BiomeSpawnListEntryMixin(int itemWeightIn)
    {
        super(itemWeightIn);
    }

    @Override
    public int getWeight()
    {
        return this.itemWeight;
    }

}
