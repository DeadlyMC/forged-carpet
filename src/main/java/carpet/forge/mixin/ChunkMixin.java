package carpet.forge.mixin;

import carpet.forge.interfaces.IChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.world.chunk.Chunk.NULL_BLOCK_STORAGE;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements IChunk
{
    @Shadow @Final private ExtendedBlockStorage[] storageArrays;
    
    @Shadow public abstract boolean canSeeSky(BlockPos pos);
    
    @Shadow @Final private World world;
    private short[] neighborLightChecks = null;
    private short pendingNeighborLightInits;
    
    @Override
    public short[] getNeighborLightChecks()
    {
        return neighborLightChecks;
    }
    
    @Override
    public void setNeighborLightChecks(short[] in)
    {
        this.neighborLightChecks = in;
    }
    
    @Override
    public short getPendingNeighborLightInits()
    {
        return pendingNeighborLightInits;
    }
    
    @Override
    public void setPendingNeighborLightInits(short in)
    {
        this.pendingNeighborLightInits = in;
    }
    
    @Override
    public int getCachedLightFor(EnumSkyBlock type, BlockPos pos)
    {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
        
        if (extendedblockstorage == NULL_BLOCK_STORAGE)
        {
            return this.canSeeSky(pos) ? type.defaultLightValue : 0;
        }
        else if (type == EnumSkyBlock.SKY)
        {
            return !this.world.provider.hasSkyLight() ? 0 : extendedblockstorage.getSkyLight(i, j & 15, k);
        }
        else
        {
            return type == EnumSkyBlock.BLOCK ? extendedblockstorage.getBlockLight(i, j & 15, k) : type.defaultLightValue;
        }
    }
}
