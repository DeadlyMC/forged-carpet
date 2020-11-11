package carpet.forge.mixin;

import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ChunkProviderServer.class)
public interface ChunkProviderServerAccessor
{
    @Accessor("droppedChunks")
    Set<Long> getDroppedChunks();
}
