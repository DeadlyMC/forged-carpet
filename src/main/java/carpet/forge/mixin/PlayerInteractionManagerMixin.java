package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.CarefulBreakHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin
{
    @Shadow
    public World world;
    
    @Shadow
    public EntityPlayerMP player;
    
    @Shadow private BlockPos destroyPos;
    
    @Inject(method = "onBlockClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendBlockBreakProgress(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void miningGhostBlockFix(BlockPos pos, EnumFacing side, CallbackInfo ci)
    {
        if (CarpetSettings.miningGhostBlocksFix)
        {
            this.player.connection.sendPacket(new SPacketBlockChange(world, destroyPos));
        }
    }
    
    @Redirect(method = "tryHarvestBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/Block;harvestBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void harvestBlock(Block block, World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack)
    {
        try {
            CarefulBreakHelper.miningPlayer = this.player;
            block.harvestBlock(worldIn, player, pos, state, te, stack);
        } finally {
            CarefulBreakHelper.miningPlayer = null;
        }
    }
}
