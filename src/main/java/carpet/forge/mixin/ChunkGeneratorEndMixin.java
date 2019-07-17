package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.IMapGenEndCity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.structure.MapGenEndCity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChunkGeneratorEnd.class)
public abstract class ChunkGeneratorEndMixin
{
    @Shadow private MapGenEndCity endCityGen;
    
    @Shadow @Final private World world;
    
    @Inject(method = "getPossibleCreatures", at = @At(value = "HEAD"), cancellable = true)
    private void onGetPossibleCreatures(EnumCreatureType creatureType, BlockPos pos, CallbackInfoReturnable<List<Biome.SpawnListEntry>> cir)
    {
        if (CarpetSettings.shulkerSpawningInEndCities && creatureType == EnumCreatureType.MONSTER)
        {
            if (this.endCityGen.isInsideStructure(pos))
            {
                cir.setReturnValue(((IMapGenEndCity)this.endCityGen).getSpawnList());
            }
        }
    }
    
    @Inject(method = "recreateStructures", at = @At("HEAD"))
    private void onRecreateStructures(Chunk chunkIn, int x, int z, CallbackInfo ci)
    {
        if (CarpetSettings.shulkerSpawningInEndCities)
            this.endCityGen.generate(this.world, x, z, (ChunkPrimer)null);
    }
}
