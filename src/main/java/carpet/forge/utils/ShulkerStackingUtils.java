package carpet.forge.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class ShulkerStackingUtils
{
    public static boolean cleanUpShulkerBoxNBT(ItemStack stack)
    {
        boolean changed = false;
        NBTTagCompound nbt = stack.getTagCompound();
        
        if (nbt != null)
        {
            if (nbt.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
            {
                NBTTagCompound tag = nbt.getCompoundTag("BlockEntityTag");
                
                if (tag.hasKey("Items", Constants.NBT.TAG_LIST) &&
                            tag.getTagList("Items", Constants.NBT.TAG_COMPOUND).tagCount() == 0)
                {
                    tag.removeTag("Items");
                    changed = true;
                }
                
                if (tag.isEmpty())
                {
                    nbt.removeTag("BlockEntityTag");
                }
            }
            
            if (nbt.isEmpty())
            {
                stack.setTagCompound(null);
                changed = true;
            }
        }
        
        return changed;
    }
    
    public static boolean shulkerBoxHasItems(ItemStack stackShulkerBox)
    {
        NBTTagCompound nbt = stackShulkerBox.getTagCompound();
        
        if (nbt != null && nbt.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound tag = nbt.getCompoundTag("BlockEntityTag");
            
            if (tag.hasKey("Items", Constants.NBT.TAG_LIST))
            {
                NBTTagList tagList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
                return tagList.tagCount() > 0;
            }
        }
        
        return false;
    }
}
