package carpet.forge.mixin;

import carpet.forge.utils.mixininterfaces.IItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity
{
    
    @Shadow
    private int pickupDelay;
    @Shadow
    private int age;
    
    public MixinEntityItem(World worldIn)
    {
        super(worldIn);
    }
    
    @Shadow
    public abstract ItemStack getItem();
    
    @Inject(method = "combineItems", at = @At(value = "RETURN", ordinal = 7, shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void stackShulkers(EntityItem other, CallbackInfoReturnable<Boolean> cir, ItemStack itemstack, ItemStack itemstack1)
    {
        if (((IItemStack) (Object) itemstack1).isGroundStackable() && ((IItemStack) (Object) itemstack).isGroundStackable())
        {
            itemstack1.grow(itemstack.getCount());
            other.pickupDelay = Math.max(other.pickupDelay, this.pickupDelay);
            other.age = Math.min(other.age, this.age);
            other.setItem(itemstack1);
            this.setDead();
            cir.setReturnValue(true);
        }
    }
    
    @Redirect(method = "combineItems", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/item/ItemStack;areCapsCompatible(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean modifyCapsCompat(ItemStack itemstack, ItemStack other)
    {
        return !itemstack.areCapsCompatible(other) || !other.isStackable() && !itemstack.isStackable();
    }
    
    
}
