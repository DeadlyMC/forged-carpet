package carpet.forge.helper;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CtrlQCrafting
{
    public static ItemStack dropAllCrafting(EntityPlayer playerIn, int index, List<Slot> inventorySlotsParam)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlotsParam.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

            if (index == 0)
            {
                playerIn.dropItem(itemstack, true);

                itemstack1.setCount(0);

                slot.onSlotChange(itemstack1, itemstack);
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0)
            {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }
}
