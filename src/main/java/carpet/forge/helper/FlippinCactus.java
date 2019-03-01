package carpet.forge.helper;

import carpet.forge.CarpetSettings;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FlippinCactus
{
    @SubscribeEvent
    public static void flippinCactus(PlayerInteractEvent.RightClickBlock event)
    {
        float hitX = 0;
        float hitY = 0;
        float hitZ = 0;
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        
        EnumFacing facing = event.getFace();
        
        if (event.getEntityPlayer().getHeldItemMainhand().getItem() == (Item.getItemFromBlock(Blocks.CACTUS)))
        {
            if (CarpetSettings.getBool("flippinCactus"))
            {
                if (block instanceof BlockGlazedTerracotta || (block instanceof BlockRedstoneDiode) || (block instanceof BlockRailBase) || (block instanceof BlockTrapDoor) || (block instanceof BlockLever) || (block instanceof BlockFenceGate))
                {
                    event.getWorld().setBlockState(event.getPos(), block.withRotation(state, Rotation.CLOCKWISE_90));
                    
                    
                }
                else if ((block instanceof BlockObserver) || (block instanceof BlockEndRod))
                {
                    event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockDirectional.FACING, (EnumFacing) event.getWorld().getBlockState(event.getPos()).getValue(BlockDirectional.FACING).getOpposite()));
                    
                    
                }
                else if (block instanceof BlockDispenser)
                {
                    event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockDispenser.FACING, (EnumFacing) event.getWorld().getBlockState(event.getPos()).getValue(BlockDirectional.FACING).getOpposite()));
                    
                }
                else if (block instanceof BlockPistonBase)
                {
                    if (!(Boolean) state.getValue(BlockPistonBase.EXTENDED).booleanValue())
                    {
                        event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockDirectional.FACING, (EnumFacing) event.getWorld().getBlockState(event.getPos()).getValue(BlockDirectional.FACING).getOpposite()));
                        
                    }
                }
                else if (block instanceof BlockSlab)
                {
                    if (!((BlockSlab) block).isDouble())
                    {
                        if (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP)
                        {
                            event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM));
                            
                        }
                        else
                        {
                            event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP));
                            
                            
                        }
                        
                    }
                    
                }
                else if (block instanceof BlockHopper)
                {
                    if ((EnumFacing) state.getValue(BlockHopper.FACING) != EnumFacing.DOWN)
                    {
                        event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockHopper.FACING, (EnumFacing) state.getValue(BlockHopper.FACING).rotateY()));
                        
                    }
                    
                }
                else if (block instanceof BlockStairs)
                {
                    if ((facing == EnumFacing.UP && hitY == 1.0f) || (facing == EnumFacing.DOWN && hitY == 0.0f))
                    {
                        if (state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP)
                        {
                            event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM));
                        }
                        else
                        {
                            event.getWorld().setBlockState(event.getPos(), state.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP));
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
                            event.setCanceled(true);
                        }
                        if (turn_right)
                        {
                            event.getWorld().setBlockState(event.getPos(), block.withRotation(state, Rotation.COUNTERCLOCKWISE_90), 130);
                        }
                        else
                        {
                            event.getWorld().setBlockState(event.getPos(), block.withRotation(state, Rotation.CLOCKWISE_90), 130);
                        }
                    }
                }
                
            }
        }
    }
}
