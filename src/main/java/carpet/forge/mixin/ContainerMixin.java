package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.CtrlQCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Container.class)
public abstract class ContainerMixin
{
    @Shadow
    public List<Slot> inventorySlots;
    
    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
    private void onSlotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir)
    {
        if (clickTypeIn == ClickType.THROW && CarpetSettings.ctrlQCraftingFix && player.inventory.getItemStack().isEmpty() && slotId >= 0)
        {
            ItemStack itemStack = ItemStack.EMPTY;
            Slot slot = inventorySlots.get(slotId);
            if (slot != null && slot.canTakeStack(player))
            {
                if (slotId == 0 && dragType == 1)
                {
                    ItemStack itemStackDropAll = CtrlQCrafting.dropAllCrafting(player, slotId, inventorySlots);
                    while (!itemStackDropAll.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemStackDropAll))
                    {
                        itemStack = itemStackDropAll.copy();
                        itemStackDropAll = CtrlQCrafting.dropAllCrafting(player, slotId, inventorySlots);
                    }
                    cir.setReturnValue(itemStack);
                    cir.cancel();
                }
            }
        }
    }
    
    /*
    // [FCM] CtrlQCrafting(Recipe book support) - Check if item stack is stackable
    @Redirect(
            method = "slotClick",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "Lnet/minecraft/item/ItemStack;areItemStackTagsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z")
    )
    private boolean isAreItemStackTagsEqual(ItemStack stackA, ItemStack stackB)
    {
        return ItemStack.areItemStackTagsEqual(stackA, stackB) && stackB.isStackable();
    }
    */
}
