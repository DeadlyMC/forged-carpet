package carpet.forge.mixin;

import carpet.forge.fakes.IMapGenEndCity;
import com.google.common.collect.Lists;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.structure.MapGenEndCity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MapGenEndCity.class)
public abstract class MapGenEndCityMixin implements IMapGenEndCity
{
    @Final
    @Mutable
    private List<Biome.SpawnListEntry> spawnList;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCtor(ChunkGeneratorEnd endProviderIn, CallbackInfo ci)
    {
        this.spawnList = Lists.<Biome.SpawnListEntry>newArrayList();
        this.spawnList.add(new Biome.SpawnListEntry(EntityShulker.class, 10, 4, 4));
    }
    
    @Override
    public List<Biome.SpawnListEntry> getSpawnList()
    {
        return this.spawnList;
    }
}