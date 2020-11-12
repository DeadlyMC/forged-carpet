package carpet.forge.mixin;

import carpet.forge.helper.BlockRotator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInteractionManager.class)
public abstract class PlayerInteractionManager_cactusMixin
{
	@Redirect(
			method = "processRightClickBlock",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/block/Block;onBlockActivated(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/EnumFacing;FFF)Z")
	)
	private boolean onProcessRightClickBlock(Block block, World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		boolean flipped = BlockRotator.flipBlockWithCactus(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		if (flipped)
			return true;
		
		return state.getBlock().onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
}
