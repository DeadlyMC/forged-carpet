package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.CtrlQCrafting;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Container.class)
public abstract class MixinContainer
{

    @Shadow
    public List<Slot> inventorySlots;

    // [FCM] Check if item stack is stackable
    @Redirect(method = "slotClick", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/item/ItemStack;areItemStackTagsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z",
              ordinal = 0))
    private boolean isAreItemStackTagsEqual(ItemStack stackA, ItemStack stackB)
    {
        return ItemStack.areItemStackTagsEqual(stackA, stackB) && stackB.isStackable();
    }

    @Redirect(method = "slotClick", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/inventory/Slot;decrStackSize(I)Lnet/minecraft/item/ItemStack;"),
              slice = @Slice(from = @At(value = "FIELD",
                      target = "Lnet/minecraft/inventory/ClickType;THROW:Lnet/minecraft/inventory/ClickType;"),
                      to = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;PICKUP_ALL:Lnet/minecraft/inventory/ClickType;")))
    private ItemStack cancelDecrStackSize(Slot slot, int amount)
    {
        return null;
    }

    @Redirect(method = "slotClick", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/inventory/Slot;onTake(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"),
              slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;THROW:Lnet/minecraft/inventory/ClickType;"),
                      to = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;PICKUP_ALL:Lnet/minecraft/inventory/ClickType;")))
    private ItemStack cancelOnTake(Slot slot, EntityPlayer thePlayer, ItemStack stack)
    {

        return null;
    }

    @Redirect(method = "slotClick", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/entity/player/EntityPlayer;dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/item/EntityItem;"),
              slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;THROW:Lnet/minecraft/inventory/ClickType;"),
                      to = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;PICKUP_ALL:Lnet/minecraft/inventory/ClickType;")))
    private EntityItem cancelDropItem(EntityPlayer player, ItemStack itemStackIn, boolean unused)
    {
        return null;
    }

    @Inject(method = "slotClick", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/inventory/Slot;decrStackSize(I)Lnet/minecraft/item/ItemStack;"),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;THROW:Lnet/minecraft/inventory/ClickType;"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/inventory/ClickType;PICKUP_ALL:Lnet/minecraft/inventory/ClickType;")),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void replaceCancelledStuff(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player,
                                       CallbackInfoReturnable<ItemStack> cir, ItemStack itemstack,
                                       InventoryPlayer inventoryplayer, Slot slot2)
    {
        // [FCM] CtrlQCrafting tweak
        if (CarpetSettings.getBool("ctrlQCraftingFix") && slotId == 0 && dragType == 1)
        {
            for (ItemStack itemstackDropAll = CtrlQCrafting.dropAllCrafting(player, slotId, inventorySlots); !itemstackDropAll.isEmpty() && ItemStack.areItemsEqual(slot2.getStack(), itemstackDropAll); itemstackDropAll = CtrlQCrafting.dropAllCrafting(player, slotId, inventorySlots))
            {
                itemstack = itemstackDropAll.copy();
            }
        }
        else
        {
            ItemStack itemstack4 = slot2.decrStackSize(dragType == 0 ? 1 : slot2.getStack().getCount());
            slot2.onTake(player, itemstack4);
            player.dropItem(itemstack4, true);
        }

    }

}
