package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.RedstoneWireTurbo;
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

import java.util.Set;

@Mixin(BlockRedstoneWire.class)
public abstract class MixinBlockRedstoneWire
{

    @Shadow
    @Final
    public static PropertyInteger POWER;

    @Shadow
    public boolean canProvidePower;

    @Shadow
    @Final
    private Set<BlockPos> blocksNeedingUpdate;

    private RedstoneWireTurbo turbo = new RedstoneWireTurbo((BlockRedstoneWire) (Object) this);

    @Shadow
    protected abstract int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength);

    @Shadow
    protected abstract IBlockState updateSurroundingRedstone(World worldIn, BlockPos pos, IBlockState state);

    @Inject(method = "updateSurroundingRedstone", at = @At("HEAD"))
    private void updateSurroundingRedstoneTurbo(World worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir)
    {
        if (CarpetSettings.fastRedstoneDust)
            cir.setReturnValue(turbo.updateSurroundingRedstone(worldIn, pos, state, null));
    }

    /**
     * @author DeadlyMC
     * @reason if statement arounds
     */
    @Overwrite
    public IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state)
    {
        IBlockState iblockstate = state;
        int i = ((Integer) state.getValue(POWER)).intValue();
        int j = 0;
        j = this.getMaxCurrentStrength(worldIn, pos2, j);
        this.canProvidePower = false;
        int k = worldIn.getRedstonePowerFromNeighbors(pos1);
        this.canProvidePower = true;

        if (!CarpetSettings.fastRedstoneDust)
        {
            // [FCM] FastRedstoneDust This code is totally redundant to if statements just below the loop.
            if (k > 0 && k > j - 1)
            {
                j = k;
            }
        }

        int l = 0;

        // The variable 'k' holds the maximum redstone power value of any adjacent blocks.
        // If 'k' has the highest level of all neighbors, then the power level of this
        // redstone wire will be set to 'k'.  If 'k' is already 15, then nothing inside the
        // following loop can affect the power level of the wire.  Therefore, the loop is
        // skipped if k is already 15.
        if (!CarpetSettings.fastRedstoneDust || k < 15)
        {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos blockpos = pos1.offset(enumfacing);
                boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();

                if (flag)
                {
                    l = this.getMaxCurrentStrength(worldIn, blockpos, l);
                }

                if (worldIn.getBlockState(blockpos).isNormalCube() && !worldIn.getBlockState(pos1.up()).isNormalCube())
                {
                    if (flag && pos1.getY() >= pos2.getY())
                    {
                        l = this.getMaxCurrentStrength(worldIn, blockpos.up(), l);
                    }
                }
                else if (!worldIn.getBlockState(blockpos).isNormalCube() && flag && pos1.getY() <= pos2.getY())
                {
                    l = this.getMaxCurrentStrength(worldIn, blockpos.down(), l);
                }
            }
        }

        if (!CarpetSettings.fastRedstoneDust)
        { // [FCM] FastRedstoneDust - The old code would decrement the wire value only by 1 at a time.
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
            // The new code sets this redstonewire block's power level to the highest neighbor
            // minus 1.  This usually results in wire power levels dropping by 2 at a time.
            // This optimization alone has no impact on opdate order, only the number of updates.
            j = l - 1;

            // If 'l' turns out to be zero, then j will be set to -1, but then since 'k' will
            // always be in the range of 0 to 15, the following if will correct that.
            if (k > j)
                j = k;
        }

        if (i != j)
        {
            state = state.withProperty(POWER, Integer.valueOf(j));

            if (worldIn.getBlockState(pos1) == iblockstate)
            {
                worldIn.setBlockState(pos1, state, 2);
            }

            if (!CarpetSettings.fastRedstoneDust)
            {
                // The new search algorithm keeps track of blocks needing updates in its own data structures,
                // so only add anything to blocksNeedingUpdate if we're using the vanilla update algorithm.
                this.blocksNeedingUpdate.add(pos1);

                for (EnumFacing enumfacing1 : EnumFacing.values())
                {
                    this.blocksNeedingUpdate.add(pos1.offset(enumfacing1));
                }
            }
        }

        return state;
    }

    @Redirect(method = "neighborChanged", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/block/BlockRedstoneWire;updateSurroundingRedstone(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState callUpdateSurroundingRedstoneTurbo(BlockRedstoneWire blockRedstoneWire, World worldIn,
                                                           BlockPos pos, IBlockState state, IBlockState methodState,
                                                           World methpdWorldIn, BlockPos methodPos, Block methodBlockIn,
                                                           BlockPos fromPos)
    {
        if (CarpetSettings.fastRedstoneDust)
            return turbo.updateSurroundingRedstone(worldIn, pos, state, fromPos);
        else
            return this.updateSurroundingRedstone(worldIn, pos, state);
    }
}
