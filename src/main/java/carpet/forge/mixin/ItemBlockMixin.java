package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.PlacementHandler;
import carpet.forge.helper.PlacementHandler.UseContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemBlock.class, priority = 999) // Apply the @ModifyArg before tweakeroo
public abstract class ItemBlockMixin
{
    @Unique private EnumHand hand;
    
    @ModifyArg(method = "onItemUse", index = 3, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/block/Block;getStateForPlacement(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;FFFILnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/block/state/IBlockState;"))
    private float modifyHitX(float hitX)
    {
        return hitX % 2.0F;
    }
    
    @Inject(method = "onItemUse", at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/item/ItemBlock;placeBlockAt(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;FFFLnet/minecraft/block/state/IBlockState;)Z"))
    private void captureHand(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX,
            float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir)
    {
        this.hand = hand;
    }
    
    @ModifyArg(method = "onItemUse", index = 8, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/item/ItemBlock;placeBlockAt(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;FFFLnet/minecraft/block/state/IBlockState;)Z"))
    private IBlockState accurateBlockPlacementState(ItemStack stack, EntityPlayer placer, World world, BlockPos pos,
            EnumFacing facing, float hitX, float hitY, float hitZ, IBlockState originalState)
    {
        if (CarpetSettings.accurateBlockPlacement)
        {
            UseContext context = UseContext.of(world, pos, facing, new Vec3d(hitX, hitY, hitZ), placer, this.hand);
            IBlockState newState = PlacementHandler.getStateForPlacement(originalState, context);
            if (newState != null)
                return newState;
        }
        return originalState;
    }
}
