package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.RedstoneWireTurbo;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(BlockRedstoneWire.class)
public abstract class MixinBlockRedstoneWire {

    @Shadow @Final public static PropertyInteger POWER;

    @Shadow protected abstract int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength);

    @Shadow public boolean canProvidePower;
    @Shadow @Final private Set<BlockPos> blocksNeedingUpdate;

    RedstoneWireTurbo turbo = new RedstoneWireTurbo((BlockRedstoneWire)(Object)this);

    @Inject(method = "updateSurroundingRedstone", at = @At("HEAD"), cancellable = true)
    private void updateSurroundingRedstoneNew(World worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir){
        cir.setReturnValue(this.updateSurroundingRedstoneTurbo(worldIn, pos, state, pos));
        cir.cancel();
    }

    private IBlockState updateSurroundingRedstoneTurbo(World worldIn, BlockPos pos, IBlockState state, BlockPos source)
    {
        if(CarpetSettings.fastRedstoneDust)
            return turbo.updateSurroundingRedstone(worldIn, pos, state, source);

        state = this.calculateCurrentChanges(worldIn, pos, pos, state);
        List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (BlockPos blockpos : list)
        {
            worldIn.notifyNeighborsOfStateChange(blockpos, (BlockRedstoneWire)(Object)this, false);
        }

        return state;
    }

    @Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockRedstoneWire;updateSurroundingRedstone(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState callUpdateSurroundingRedstoneTurbo(BlockRedstoneWire blockRedstoneWire, World worldIn, BlockPos pos, IBlockState state){

        return this.updateSurroundingRedstoneTurbo(worldIn, pos, state, null);
    }

    @Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockRedstoneWire;updateSurroundingRedstone(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState callUpdateSurroundingRedstoneTurbo2(BlockRedstoneWire blockRedstoneWire, World worldIn, BlockPos pos, IBlockState state){

        return this.updateSurroundingRedstoneTurbo(worldIn, pos, state, null);
    }

    @Redirect(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockRedstoneWire;updateSurroundingRedstone(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState callUpdateSurroundingRedstoneTurbo3(BlockRedstoneWire blockRedstoneWire, World worldIn, BlockPos pos, IBlockState state, IBlockState methodState, World methodWorldIn, BlockPos methodPos, Block methodBlockIn, BlockPos methodFromPos){

        return this.updateSurroundingRedstoneTurbo(worldIn, pos, state, methodFromPos);
    }

    /**
     * @author DeadlyMC
     * @reason if statement arounds
     */
    @Overwrite
    public IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state)
    {
        IBlockState iblockstate = state;
        int i = ((Integer)state.getValue(POWER)).intValue();
        int j = 0;
        j = this.getMaxCurrentStrength(worldIn, pos2, j);
        this.canProvidePower = false;
        int k = worldIn.getRedstonePowerFromNeighbors(pos1);
        this.canProvidePower = true;

        // [FCM] FastRedstoneDust -- skipping unnecessary check by if statement around
        if (!CarpetSettings.fastRedstoneDust)
        {
            if (k > 0 && k > j - 1) {
                j = k;
            }
        }
        // [FCM] End

        int l = 0;

        // [FCM] FastRedstoneDust -- if statement around
        if (!CarpetSettings.fastRedstoneDust || k < 15)
        {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                BlockPos blockpos = pos1.offset(enumfacing);
                boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();

                if (flag) {
                    l = this.getMaxCurrentStrength(worldIn, blockpos, l);
                }

                if (worldIn.getBlockState(blockpos).isNormalCube() && !worldIn.getBlockState(pos1.up()).isNormalCube()) {
                    if (flag && pos1.getY() >= pos2.getY()) {
                        l = this.getMaxCurrentStrength(worldIn, blockpos.up(), l);
                    }
                } else if (!worldIn.getBlockState(blockpos).isNormalCube() && flag && pos1.getY() <= pos2.getY()) {
                    l = this.getMaxCurrentStrength(worldIn, blockpos.down(), l);
                }
            }
        }
        // [FCM] End

        // [FCM] FastRedstoneDust -- if statement around
        if (!CarpetSettings.fastRedstoneDust)
        {
            if (l > j)
            {
                j = l - 1;
            }
            else if (j > 0)
            {
                --j;
            }
            else
            {
                j = 0;
            }

            if (k > j - 1)
            {
                j = k;
            }
        }
        else
        {
            j = l - 1;
            if (k > j) j = k;
        }
        // [FCM] End

        if (i != j)
        {
            state = state.withProperty(POWER, Integer.valueOf(j));

            if (worldIn.getBlockState(pos1) == iblockstate)
            {
                worldIn.setBlockState(pos1, state, 2);
            }

            // [FCM] FastRedstoneDust -- if statement around unnecessary code
            if (!CarpetSettings.fastRedstoneDust)
            {
                this.blocksNeedingUpdate.add(pos1);

                for (EnumFacing enumfacing1 : EnumFacing.values())
                {
                    this.blocksNeedingUpdate.add(pos1.offset(enumfacing1));
                }
            }
            // [FCM] End
        }

        return state;
    }


}
