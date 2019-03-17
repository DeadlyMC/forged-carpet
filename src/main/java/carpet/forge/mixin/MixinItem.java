package carpet.forge.mixin;

import carpet.forge.utils.mixininterfaces.IItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class MixinItem implements IItem {

    /*
     * [FCM] Fix for stack changes when doing NBT checks on shulkers.
     */
    @Override
    public boolean itemGroundStacking(boolean hasTagCompound) { return false; }

}
