package carpet.forge.helper;

import carpet.forge.CarpetSettings;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

// Helper for FlippinCactus, RotatorBlock and AccurateBlockPlacement
public class BlockRotator
{
    public static boolean flipBlockWithCactus(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.capabilities.allowEdit || !CarpetSettings.flippinCactus || !player_holds_cactus_mainhand(playerIn))
        {
            return false;
        }
        return flip_block(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    public static boolean flip_block(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        Block block = state.getBlock();
        if ( (block instanceof BlockGlazedTerracotta) || (block instanceof BlockRedstoneDiode) || (block instanceof BlockRailBase) ||
                (block instanceof BlockTrapDoor)         || (block instanceof BlockLever)         || (block instanceof BlockFenceGate))
        {
            worldIn.setBlockState(pos, block.withRotation(state, Rotation.CLOCKWISE_90), 130);
        }
        else if ((block instanceof BlockObserver) || (block instanceof BlockEndRod))
        {
            worldIn.setBlockState(pos, state.withProperty(BlockDirectional.FACING, (EnumFacing)state.getValue(BlockDirectional.FACING).getOpposite()), 130);
        }
        else if (block instanceof BlockDispenser)
        {
            worldIn.setBlockState(pos, state.withProperty(BlockDispenser.FACING, (EnumFacing)state.getValue(BlockDispenser.FACING).getOpposite()), 130);
        }
        else if (block instanceof BlockPistonBase)
        {
            if (!(((Boolean)state.getValue(BlockPistonBase.EXTENDED)).booleanValue()))
                worldIn.setBlockState(pos, state.withProperty(BlockDirectional.FACING, (EnumFacing)state.getValue(BlockDirectional.FACING).getOpposite()), 130);
        }
        else if (block instanceof BlockSlab)
        {
            if (!((BlockSlab) block).isDouble())
            {
                if (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP)
                {
                    worldIn.setBlockState(pos, state.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM), 130);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP), 130);
                }
            }
        }
        else if (block instanceof BlockHopper)
        {
            if ((EnumFacing)state.getValue(BlockHopper.FACING) != EnumFacing.DOWN)
            {
                worldIn.setBlockState(pos, state.withProperty(BlockHopper.FACING, (EnumFacing) state.getValue(BlockHopper.FACING).rotateY()), 130);
            }
        }
        else if (block instanceof BlockStairs)
        {
            //LOG.error(String.format("hit with facing: %s, at side %.1fX, X %.1fY, Y %.1fZ",facing, hitX, hitY, hitZ));
            if ((facing == EnumFacing.UP && hitY == 1.0f) || (facing == EnumFacing.DOWN && hitY == 0.0f))
            {
                if (state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP)
                {
                    worldIn.setBlockState(pos, state.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM), 130);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), 130);
                }
            }
            else
            {
                boolean turn_right = true;
                if (facing == EnumFacing.NORTH)
                {
                    turn_right = (hitX <= 0.5);
                }
                else if (facing == EnumFacing.SOUTH)
                {
                    turn_right = !(hitX <= 0.5);
                }
                else if (facing == EnumFacing.EAST)
                {
                    turn_right = (hitZ <= 0.5);
                }
                else if (facing == EnumFacing.WEST)
                {
                    turn_right = !(hitZ <= 0.5);
                }
                else
                {
                    return false;
                }
                if (turn_right)
                {
                    worldIn.setBlockState(pos, block.withRotation(state, Rotation.COUNTERCLOCKWISE_90), 130);
                }
                else
                {
                    worldIn.setBlockState(pos, block.withRotation(state, Rotation.CLOCKWISE_90), 130);
                }
            }
        }
        else
        {
            return false;
        }
        worldIn.markBlockRangeForRenderUpdate(pos, pos);
        return true;
    }
    private static boolean player_holds_cactus_mainhand(EntityPlayer playerIn)
    {
        return (!playerIn.getHeldItemMainhand().isEmpty()
                && playerIn.getHeldItemMainhand().getItem() instanceof ItemBlock &&
                ((ItemBlock) (playerIn.getHeldItemMainhand().getItem())).getBlock() == Blocks.CACTUS);
    }
    public static boolean flippinEligibility(Entity entity)
    {
        if (CarpetSettings.flippinCactus
                && (entity instanceof EntityPlayer))
        {
            EntityPlayer player = (EntityPlayer)entity;
            return (!player.getHeldItemOffhand().isEmpty()
                    && player.getHeldItemOffhand().getItem() instanceof ItemBlock &&
                    ((ItemBlock) (player.getHeldItemOffhand().getItem())).getBlock() == Blocks.CACTUS);
        }
        return false;
    }
}
