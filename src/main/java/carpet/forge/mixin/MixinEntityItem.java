package carpet.forge.mixin;

import carpet.forge.utils.mixininterfaces.IItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity {

    @Shadow public abstract ItemStack getItem();

    @Shadow private int pickupDelay;

    @Shadow private int age;

    public MixinEntityItem(World worldIn) {
        super(worldIn);
    }

    /**
     * @author DeadlyMC
     * @reason StackableShulkerBoxes
     */
    @Overwrite
    public boolean combineItems(EntityItem other)
    {
        if (other == (EntityItem)(Object)this)
        {
            return false;
        }
        else if (other.isEntityAlive() && this.isEntityAlive())
        {
            ItemStack itemstack = this.getItem();
            ItemStack itemstack1 = other.getItem();

            if (this.pickupDelay != 32767 && other.pickupDelay != 32767)
            {
                if (this.age != -32768 && other.age != -32768)
                {
                    if (itemstack1.getItem() != itemstack.getItem())
                    {
                        return false;
                    }
                    else if (itemstack1.hasTagCompound() ^ itemstack.hasTagCompound())
                    {
                        return false;
                    }
                    else if (itemstack1.hasTagCompound() && !itemstack1.getTagCompound().equals(itemstack.getTagCompound()))
                    {
                        return false;
                    }
                    else if (itemstack1.getItem() == null)
                    {
                        return false;
                    }
                    else if (itemstack1.getItem().getHasSubtypes() && itemstack1.getMetadata() != itemstack.getMetadata())
                    {
                        return false;
                    }
                    else if (itemstack1.getCount() < itemstack.getCount())
                    {
                        return other.combineItems((EntityItem)(Object)this);
                    }
                    else if (itemstack1.getCount() + itemstack.getCount() > itemstack1.getMaxStackSize())
                    {
                        // [FCM] Add check for stacking shoulkers without NBT on the ground
                        if (((IItemStack)(Object) itemstack1).isGroundStackable() && ((IItemStack)(Object) itemstack).isGroundStackable())
                        {
                            itemstack1.grow(itemstack.getCount());
                            other.pickupDelay = Math.max(other.pickupDelay, this.pickupDelay);
                            other.age = Math.min(other.age, this.age);
                            other.setItem(itemstack1);
                            this.setDead();
                            return true;
                        }
                        return false;
                    }
                    // [FCM] Make sure stackable items are checked before combining them, always true in vanilla
                    else if (!itemstack1.isStackable() && !itemstack.isStackable())
                    {
                        return false;
                    }
                    else if (!itemstack.areCapsCompatible(itemstack1))
                    {
                        return false;
                    }
                    else
                    {
                        itemstack1.grow(itemstack.getCount());
                        other.pickupDelay = Math.max(other.pickupDelay, this.pickupDelay);
                        other.age = Math.min(other.age, this.age);
                        other.setItem(itemstack1);
                        this.setDead();
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }


}
