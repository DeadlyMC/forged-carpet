package carpet.forge.mixin;

import carpet.forge.helper.BlockRotator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockHopper.class)
public abstract class MixinBlockHopper extends BlockContainer
{
    protected MixinBlockHopper(Material materialIn, MapColor color)
    {
        super(materialIn, color);
    }
    
    @Redirect(
            method = "getStateForPlacement",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/EnumFacing;getOpposite()Lnet/minecraft/util/EnumFacing;")
    )
    private EnumFacing onGetStateForPlacement(
            EnumFacing enumFacing,
            World worldIn,
            BlockPos pos,
            EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ,
            int meta,
            EntityLivingBase placer
    )
    {
        if (BlockRotator.flippinEligibility(placer))
        {
            return enumFacing.getOpposite().getOpposite();
        }
        return enumFacing.getOpposite();
    }
    
}
