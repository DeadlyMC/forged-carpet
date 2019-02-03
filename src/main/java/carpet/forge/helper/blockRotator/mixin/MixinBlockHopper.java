package carpet.forge.helper.blockRotator.mixin;

import carpet.forge.helper.blockRotator.BlockRotator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockHopper.class)
public abstract class MixinBlockHopper extends BlockContainer {

    @Shadow @Final public static PropertyDirection FACING;

    @Shadow @Final public static PropertyBool ENABLED;

    protected MixinBlockHopper(Material materialIn, MapColor color) {
        super(materialIn, color);
    }

    /**
     * @author DeadlyMC
     */
    @Overwrite
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        EnumFacing enumfacing = facing.getOpposite();
        // [FCM] Start
        if (BlockRotator.flippinEligibility(placer))
            enumfacing = enumfacing.getOpposite();
        // [FCM] End

        if (enumfacing == EnumFacing.UP)
        {
            enumfacing = EnumFacing.DOWN;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ENABLED, Boolean.valueOf(true));
    }

}
