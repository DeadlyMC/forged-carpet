package carpet.forge.mixin;

import carpet.forge.interfaces.IChunkProviderServer;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ChunkProviderServer.class)
public abstract class ChunkProviderServerMixin implements IChunkProviderServer
{
    @Shadow @Final private Set<Long> droppedChunks;
    
    public Set<Long> getDroppedChunksC()
    {
        return this.droppedChunks;
    }
}
