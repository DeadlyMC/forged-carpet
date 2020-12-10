package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.fakes.IMapGenScatteredFeature;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChunkGeneratorOverworld.class)
public abstract class ChunkGeneratorOverworldMixin
{
    @Shadow private MapGenScatteredFeature scatteredFeatureGenerator;
    
    @Shadow @Final private boolean mapFeaturesEnabled;
    
    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true)
    private void huskSpawning(EnumCreatureType creatureType, BlockPos pos, CallbackInfoReturnable<List<Biome.SpawnListEntry>> cir)
    {
        if (this.mapFeaturesEnabled)
        {
            if (CarpetSettings.huskSpawningInTemples && creatureType == EnumCreatureType.MONSTER && ((IMapGenScatteredFeature) this.scatteredFeatureGenerator).isTemple(pos))
            {
                cir.setReturnValue(((IMapGenScatteredFeature) this.scatteredFeatureGenerator).getHuskSpawnList());
            }
        }
    }
}
