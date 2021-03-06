package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.ShulkerStackingUtils;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//CREDITS : Masa (Tweakeroo)
@Mixin(EntityItem.class)
public abstract class EntityItemMixin
{
    @Shadow private int age;
    
    @Shadow private int pickupDelay;
    
    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
    private void removeEmptyShulkerBoxTags(World worldIn, double x, double y, double z, ItemStack stack, CallbackInfo ci)
    {
        if (CarpetSettings.stackableEmptyShulkerBoxes)
        {
            if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockShulkerBox)
            {
                if (ShulkerStackingUtils.cleanUpShulkerBoxNBT(stack))
                {
                    ((EntityItem) (Object) this).setItem(stack);
                }
            }
        }
    }
    
    @Inject(method = "combineItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0), cancellable = true)
    private void tryStackShulkerBoxes(EntityItem other, CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.stackableEmptyShulkerBoxes)
        {
            EntityItem self = (EntityItem) (Object) this;
            ItemStack stackSelf = self.getItem();
            ItemStack stackOther = other.getItem();
            
            if (stackSelf.getItem() instanceof ItemBlock && ((ItemBlock) stackSelf.getItem()).getBlock() instanceof BlockShulkerBox &&
                        stackSelf.getItem() == stackOther.getItem() &&
                        ShulkerStackingUtils.shulkerBoxHasItems(stackSelf) == false &&
                        // Only stack up to 64, and don't steal from other stacks that are larger
                        stackSelf.getCount() < 64 && stackSelf.getCount() >= stackOther.getCount() &&
                        ItemStack.areItemStackTagsEqual(stackSelf, stackOther))
            {
                int amount = Math.min(stackOther.getCount(), 64 - stackSelf.getCount());
                stackSelf.grow(amount);
                self.setItem(stackSelf);
                this.pickupDelay = Math.max(((EntityItemAccessor) other).getPickupDelayFC(), this.pickupDelay);
                this.age = Math.min(other.getAge(), this.age);
                
                if (amount >= stackOther.getCount())
                {
                    other.setDead();
                }
                else
                {
                    stackOther.shrink(amount);
                    other.setItem(stackOther);
                }
                
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
