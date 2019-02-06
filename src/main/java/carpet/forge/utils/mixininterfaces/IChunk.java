package carpet.forge.utils.mixininterfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public interface IChunk {
    int getCachedLightFor(EnumSkyBlock type, BlockPos pos);
    short getPendingNeighborLightInits();
    void setPendingNeighborLightInits(short inits);
    short[] getNeighborLightChecks();
    void setNeighborLightChecks(short[] lightChecks);
    IBlockState setBlockState_carpet(BlockPos pos, IBlockState state, boolean skip_updates);
}
