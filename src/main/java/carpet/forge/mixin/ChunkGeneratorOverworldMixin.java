package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.fakes.IMapGenScatteredFeature;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
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
    
    @Inject(
            method = "getPossibleCreatures",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/gen/ChunkGeneratorOverworld;mapFeaturesEnabled:Z",
                    shift = At.Shift.AFTER),
            cancellable = true
    )
    private void onGetPossibleCreatures(EnumCreatureType creatureType, BlockPos pos, CallbackInfoReturnable<List<Biome.SpawnListEntry>> cir)
    {
        if (CarpetSettings.huskSpawningInTemples && creatureType == EnumCreatureType.MONSTER && ((IMapGenScatteredFeature) this.scatteredFeatureGenerator).isTemple(pos))
        {
            cir.setReturnValue(((IMapGenScatteredFeature) this.scatteredFeatureGenerator).getHuskSpawnList());
        }
    }
}
