package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.BlockRotator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = PlayerInteractionManager.class)
public abstract class MixinPlayerInteractionManager
{

    @Shadow
    public World world;

    @Shadow
    public EntityPlayerMP player;

    @Shadow
    private BlockPos destroyPos;
    
    @Shadow public abstract boolean isCreative();
    
    @Shadow private GameType gameType;
    
    @Inject(method = "onBlockClicked", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;sendBlockBreakProgress(ILnet/minecraft/util/math/BlockPos;I)V", shift = At.Shift.BEFORE))
    private void notifyUpdate(BlockPos pos, EnumFacing side, CallbackInfo ci)
    {
        if (CarpetSettings.getBool("miningGhostBlocksFix"))
        {
            player.connection.sendPacket(new SPacketBlockChange(world, destroyPos));
        }
    }
    
    /**
     * @author DeadlyMC
     * @reason Doing an @Inject was causing crash with liteloader
     */
    @Overwrite
    public EnumActionResult processRightClickBlock(EntityPlayer player, World worldIn, ItemStack stack, EnumHand hand, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (this.gameType == GameType.SPECTATOR)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            
            if (tileentity instanceof ILockableContainer)
            {
                Block block1 = worldIn.getBlockState(pos).getBlock();
                ILockableContainer ilockablecontainer = (ILockableContainer)tileentity;
                
                if (ilockablecontainer instanceof TileEntityChest && block1 instanceof BlockChest)
                {
                    ilockablecontainer = ((BlockChest)block1).getLockableContainer(worldIn, pos);
                }
                
                if (ilockablecontainer != null)
                {
                    player.displayGUIChest(ilockablecontainer);
                    return EnumActionResult.SUCCESS;
                }
            }
            else if (tileentity instanceof IInventory)
            {
                player.displayGUIChest((IInventory)tileentity);
                return EnumActionResult.SUCCESS;
            }
            
            return EnumActionResult.PASS;
        }
        else
        {
            double reachDist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
            net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks
                                                                                                       .onRightClickBlock(player, hand, pos, facing, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(player, reachDist + 1));
            if (event.isCanceled()) return event.getCancellationResult();
            
            EnumActionResult result = EnumActionResult.PASS;
            if (event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
            {
                result = stack.onItemUseFirst(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                if (result != EnumActionResult.PASS) return result ;
            }
            
            boolean bypass = player.getHeldItemMainhand().doesSneakBypassUse(worldIn, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(worldIn, pos, player);
            
            if (!player.isSneaking() || bypass || event.getUseBlock() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW)
            {
                IBlockState iblockstate = worldIn.getBlockState(pos);
                if(event.getUseBlock() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                {
                    // [FCM] Flip method will check for flippinCactus setting
                    boolean flipped = BlockRotator.flipBlockWithCactus(worldIn, pos, iblockstate, player, hand, facing, hitX, hitY, hitZ);
                    if (flipped)
                    {
                        result = EnumActionResult.PASS;
                    }
                    if (iblockstate.getBlock().onBlockActivated(worldIn, pos, iblockstate, player, hand, facing, hitX, hitY, hitZ))
                    {
                        result = EnumActionResult.SUCCESS;
                    }
                }
            }
            
            if (stack.isEmpty())
            {
                return EnumActionResult.PASS;
            }
            else if (player.getCooldownTracker().hasCooldown(stack.getItem()))
            {
                return EnumActionResult.PASS;
            }
            else
            {
                if (stack.getItem() instanceof ItemBlock && !player.canUseCommandBlock())
                {
                    Block block = ((ItemBlock)stack.getItem()).getBlock();
                    
                    if (block instanceof BlockCommandBlock || block instanceof BlockStructure)
                    {
                        return EnumActionResult.FAIL;
                    }
                }
                
                if (this.isCreative())
                {
                    int j = stack.getMetadata();
                    int i = stack.getCount();
                    if (result != EnumActionResult.SUCCESS && event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY
                                || result == EnumActionResult.SUCCESS && event.getUseItem() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) {
                        EnumActionResult enumactionresult = stack.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                        stack.setItemDamage(j);
                        stack.setCount(i);
                        return enumactionresult;
                    } else return result;
                }
                else
                {
                    if (result != EnumActionResult.SUCCESS && event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY
                                || result == EnumActionResult.SUCCESS && event.getUseItem() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) {
                        ItemStack copyBeforeUse = stack.copy();
                        result = stack.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                        if (stack.isEmpty()) net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
                    } return result;
                }
            }
        }
    }
    
    
}
