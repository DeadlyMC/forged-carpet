package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.ITileEntityPiston;
import carpet.forge.utils.mixininterfaces.IWorldServer;
import net.minecraft.block.*;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase extends BlockDirectional{

    protected MixinBlockPistonBase(Material materialIn) {
        super(materialIn);
    }

    @Shadow protected abstract boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending);

    @Shadow
    public static boolean canPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing p_185646_5_) {
        return false;
    }

    @Shadow @Final private boolean isSticky;

    @Shadow public abstract IBlockState getStateFromMeta(int meta);

    @Shadow @Final public static PropertyBool EXTENDED;

    @Shadow protected abstract boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing);

    /**
     * @author DeadlyMC
     * @reason Parameters for if statement
     */
    @Overwrite
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(BlockDirectional.FACING);

        if (!worldIn.isRemote)
        {
            boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

            if (flag && id == 1)
            {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!flag && id == 0)
            {
                return false;
            }
        }

        if (id == 0)
        {
            if (!this.doMove(worldIn, pos, enumfacing, true))
            {
                return false;
            }

            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 3);
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (id == 1)
        {
            TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(enumfacing));

            if (tileentity1 instanceof TileEntityPiston)
            {
                ((TileEntityPiston)tileentity1).clearPistonTileEntity();
            }

            worldIn.setBlockState(pos, Blocks.PISTON_EXTENSION.getDefaultState().withProperty(BlockPistonMoving.FACING, enumfacing).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            worldIn.setTileEntity(pos, BlockPistonMoving.createTilePiston(this.getStateFromMeta(param), enumfacing, false, true));

            if (this.isSticky)
            {
                BlockPos blockpos = pos.add(enumfacing.getXOffset() * 2, enumfacing.getYOffset() * 2, enumfacing.getZOffset() * 2);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                boolean flag1 = false;

                if (block == Blocks.PISTON_EXTENSION)
                {
                    TileEntity tileentity = worldIn.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityPiston)
                    {
                        TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;

                        if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending())
                        {
                            tileentitypiston.clearPistonTileEntity();
                            flag1 = true;
                        }
                    }
                }

                // [FCM] (param & 16) == 0 means the piston shouldn't retract the block, implemented for ghost block fix.
                if (!flag1 && (param & 16) == 0 && !iblockstate.getBlock().isAir(iblockstate, worldIn, blockpos) && canPush(iblockstate, worldIn, blockpos, enumfacing.getOpposite(), false, enumfacing) && (iblockstate.getPushReaction() == EnumPushReaction.NORMAL || block == Blocks.PISTON || block == Blocks.STICKY_PISTON))
                {
                    this.doMove(worldIn, pos, enumfacing, false);
                }
            }
            else
            {
                worldIn.setBlockToAir(pos.offset(enumfacing));
            }

            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    @Redirect(method = "checkForMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V", ordinal = 1))
    private void sendDropBlockFlag(World world, BlockPos pos, Block blockIn, int eventID, int eventParam, World worldIn, BlockPos callpos, IBlockState state)
    {
        int suppress_move = 0;

        if (CarpetSettings.pistonGhostBlocksFix == 2)
        {
            final EnumFacing enumfacing = state.getValue(FACING);

            final BlockPos blockpos = new BlockPos(callpos).offset(enumfacing, 2);
            final IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() == Blocks.PISTON_EXTENSION)
            {
                final TileEntity tileentity = worldIn.getTileEntity(blockpos);

                if (tileentity instanceof TileEntityPiston)
                {
                    final TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity;
                    if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending()
                            && (((ITileEntityPiston) tileentitypiston).getLastProgress() < 0.5F
                            || tileentitypiston.getWorld().getTotalWorldTime() == ((ITileEntityPiston) tileentitypiston).getLastTicked()
                            || !((IWorldServer) worldIn).haveBlockActionsProcessed()))
                    {
                        suppress_move = 16;
                    }
                }
            }
        }

        worldIn.addBlockEvent(pos, blockIn, eventID, eventParam | suppress_move);
    }

}
