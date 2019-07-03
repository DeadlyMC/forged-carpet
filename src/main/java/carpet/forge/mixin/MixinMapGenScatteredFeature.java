package carpet.forge.mixin;

import carpet.forge.utils.mixininterfaces.IMapGenScatteredFeature;
import com.google.common.collect.Lists;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MapGenScatteredFeature.class)
public abstract class MixinMapGenScatteredFeature extends MapGenStructure implements IMapGenScatteredFeature
{
    @Final
    @Mutable
    private ArrayList<Biome.SpawnListEntry> huskSpawnList;
    
    @Inject(method = "<init>()V", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci)
    {
        this.huskSpawnList = Lists.<Biome.SpawnListEntry>newArrayList();
        this.huskSpawnList.add(new Biome.SpawnListEntry(EntityHusk.class, 1, 1, 1));
    }
    
    public List<Biome.SpawnListEntry> getHuskSpawnList()
    {
        return this.huskSpawnList;
    }
    
    public boolean isTemple(BlockPos pos)
    {
        StructureStart structurestart = this.getStructureAt(pos);
        if (structurestart != null && structurestart instanceof MapGenScatteredFeature.Start && !structurestart.components.isEmpty())
        {
            StructureComponent structurecomponent = structurestart.components.get(0);
            return structurecomponent instanceof ComponentScatteredFeaturePieces.DesertPyramid;
        }
        else
        {
            return false;
        }
    }
}
