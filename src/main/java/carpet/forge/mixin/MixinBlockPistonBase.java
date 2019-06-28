package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.ITileEntityPiston;
import carpet.forge.utils.mixininterfaces.IWorldServer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase extends BlockDirectional
{

    protected MixinBlockPistonBase(Material materialIn)
    {
        super(materialIn);
    }
    
    /*
    @Shadow
    public static boolean canPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing p_185646_5_)
    {
        return false;
    }

    @Shadow
    protected abstract boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending);

    @Shadow
    public abstract IBlockState getStateFromMeta(int meta);

    @Redirect(method = "eventReceived", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/block/BlockPistonBase;canPush(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;ZLnet/minecraft/util/EnumFacing;)Z"))
    private boolean cancelCanPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing p_185646_5_)
    {
        return false;
    }

    @Inject(method = "eventReceived", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/block/BlockPistonBase;canPush(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;ZLnet/minecraft/util/EnumFacing;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void newCanPush(IBlockState state, World worldIn, BlockPos pos, int id, int param, CallbackInfoReturnable<Boolean> cir, EnumFacing enumfacing, TileEntity tileentity1, BlockPos blockpos, IBlockState iblockstate, Block block, boolean flag1)
    {
        if (!flag1 && (param & 16) == 0 && !iblockstate.getBlock().isAir(iblockstate, worldIn, blockpos) && canPush(iblockstate, worldIn, blockpos, enumfacing.getOpposite(), false, enumfacing) && (iblockstate.getPushReaction() == EnumPushReaction.NORMAL || block == Blocks.PISTON || block == Blocks.STICKY_PISTON))
        {
            this.doMove(worldIn, pos, enumfacing, false);
        }
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
                    if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending() && (((ITileEntityPiston) tileentitypiston).getLastProgress() < 0.5F || tileentitypiston.getWorld().getTotalWorldTime() == ((ITileEntityPiston) tileentitypiston).getLastTicked() || !((IWorldServer) worldIn).haveBlockActionsProcessed()))
                    {
                        suppress_move = 16;
                    }
                }
            }
        }

        worldIn.addBlockEvent(pos, blockIn, eventID, eventParam | suppress_move);
    }
    
     */

}
