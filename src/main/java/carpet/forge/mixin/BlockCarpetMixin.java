package carpet.forge.mixin;

import carpet.forge.utils.WoolTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockCarpet.class)
public abstract class BlockCarpetMixin extends Block
{
    @Shadow @Final public static PropertyEnum<EnumDyeColor> COLOR;
    
    public BlockCarpetMixin(Material blockMaterialIn, MapColor blockMapColorIn)
    {
        super(blockMaterialIn, blockMapColorIn);
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState state = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        if (placer instanceof EntityPlayer && !worldIn.isRemote)
        {
            WoolTool.carpetPlacedAction(((EnumDyeColor)state.getValue(COLOR)), (EntityPlayer)placer, pos, worldIn);
        }
        return state;
    }
}
