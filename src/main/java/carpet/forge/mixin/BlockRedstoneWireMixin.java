package carpet.forge.mixin;

import carpet.forge.fakes.IBlockRedstoneWire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockRedstoneWire.class)
public abstract class BlockRedstoneWireMixin extends Block implements IBlockRedstoneWire
{
    @Shadow protected abstract IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state);
    
    @Shadow private boolean canProvidePower;
    
    public BlockRedstoneWireMixin(Material blockMaterialIn, MapColor blockMapColorIn)
    {
        super(blockMaterialIn, blockMapColorIn);
    }
    
    @Override
    public IBlockState callCalculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state)
    {
        return this.calculateCurrentChanges(worldIn, pos1, pos2, state);
    }
    
    @Override
    public void setCanProvidePower(boolean canProvidePowerIn)
    {
        this.canProvidePower = canProvidePowerIn;
    }
}
