package carpet.forge.permanantMixins;

import carpet.forge.utils.IMixinBiome;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

// Helper class for SpawnReporter
// Adds a new method required for SpawnReporter
@Mixin(value = Biome.SpawnListEntry.class)
public abstract class MixinBiomeSpawnListEntry extends WeightedRandom.Item implements IMixinBiome {

    public MixinBiomeSpawnListEntry(int itemWeightIn) {
        super(itemWeightIn);
    }

    @Override
    public int getWeight(){
        return this.itemWeight;
    }

}
