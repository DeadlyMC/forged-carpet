package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.world.gen.structure.MapGenEndCity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapGenEndCity.Start.class)
public abstract class MapGenEndCityStartMixin
{
    @Shadow private boolean isSizeable;
    
    @Redirect(method = "isSizeableStructure", at = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/structure/MapGenEndCity$Start;isSizeable:Z"))
    private boolean onIsSizeableStructure(MapGenEndCity.Start mapGenStart)
    {
        // [FCM] Shulker Spawning in End cities - Needs to return true for end cities to regenerate properly.
        if (CarpetSettings.shulkerSpawningInEndCities)
            return true;
        return this.isSizeable;
    }
}
