package carpet.forge.mixin;

import carpet.forge.CarpetMain;
import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockObserver.class)
public abstract class MixinBlockObserver extends BlockDirectional {

    protected MixinBlockObserver(Material materialIn) {
        super(materialIn);
    }

    @Shadow @Final public static PropertyBool POWERED;

    @Redirect(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockObserver;getDefaultState()Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState onGetStateForPlacement(BlockObserver blockObserver, World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer).getOpposite()).withProperty(POWERED, CarpetSettings.getBool("observersDoNonUpdate"));
    }
}
