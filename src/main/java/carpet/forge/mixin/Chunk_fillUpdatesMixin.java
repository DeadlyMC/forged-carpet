package carpet.forge.mixin;

import carpet.forge.fakes.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Chunk.class)
public abstract class Chunk_fillUpdatesMixin
{
    @Redirect(method = "setBlockState", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z"))
    private boolean skipUpdatesAndIsRemote(World world)
    {
        return ((IWorld) world).shouldSkipUpdates() && world.isRemote;
    }
}
