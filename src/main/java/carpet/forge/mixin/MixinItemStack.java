package carpet.forge.mixin;

import carpet.forge.utils.mixininterfaces.IItem;
import carpet.forge.utils.mixininterfaces.IItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements IItemStack {

    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean hasTagCompound();

    // [FCM] Check for ground stacking
    @Override
    public boolean isGroundStackable() {
        return ((IItem) this.getItem()).itemGroundStacking(hasTagCompound());
    }
}
