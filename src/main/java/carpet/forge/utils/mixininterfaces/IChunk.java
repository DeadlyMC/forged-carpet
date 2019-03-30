package carpet.forge.utils.mixininterfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public interface IChunk
{
    short[] getNeighborLightChecks();
    
    void setNeighborLightChecks(short[] in);
    
    short getPendingNeighborLightInits();
    
    void setPendingNeighborLightInits(short in);
    
    int getCachedLightFor(EnumSkyBlock type, BlockPos pos);
    
    IBlockState setBlockState_carpet(BlockPos pos, IBlockState state, boolean skip_updates);
}
