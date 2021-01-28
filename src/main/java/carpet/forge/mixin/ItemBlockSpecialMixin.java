package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.PlacementHandler;
import carpet.forge.helper.PlacementHandler.UseContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockSpecial.class)
public class ItemBlockSpecialMixin
{
    @Unique private World world;
    @Unique private EnumFacing facing;
    @Unique private float hitX;
    @Unique private float hitY;
    @Unique private float hitZ;
    @Unique private EntityPlayer placer;
    @Unique private EnumHand hand;
    
    @ModifyVariable(method = "onItemUse", argsOnly = true, at = @At(value = "LOAD", opcode = Opcodes.FLOAD), index = 6)
    private float modifyHitX(float hitX)
    {
        this.hitX = hitX;
        return hitX % 2.0F;
    }
    
    @ModifyVariable(method = "onItemUse", index = 6, at = @At(value = "INVOKE", shift = At.Shift.AFTER, remap = false,
            target = "Lnet/minecraft/block/Block;getStateForPlacement(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;FFFILnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/block/state/IBlockState;"))
    private float resetHitX(float hitX)
    {
        return this.hitX;
    }
    
    // I know this is pretty ugly but you can't capture method args with @ModifyArg's and using an @Redirect would cause a conflict and crash the game
    @Inject(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
    private void captureVars(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir)
    {
        this.world = worldIn;
        this.facing = facing;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
        this.placer = player;
        this.hand = hand;
    }
    
    @ModifyArg(method = "onItemUse", index = 1, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
    private IBlockState accurateBlockPlacementState(BlockPos pos, IBlockState originalState, int flags)
    {
        if (CarpetSettings.accurateBlockPlacement)
        {
            UseContext context = UseContext.of(this.world, pos, this.facing, new Vec3d(this.hitX, this.hitY, this.hitZ), this.placer, this.hand);
            IBlockState newState = PlacementHandler.getStateForPlacement(originalState, context);
            if (newState != null)
                return newState;
        }
        return originalState;
    }
}
