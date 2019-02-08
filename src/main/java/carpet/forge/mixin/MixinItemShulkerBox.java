package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.IItem;
import net.minecraft.item.ItemShulkerBox;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemShulkerBox.class)
public abstract class MixinItemShulkerBox implements IItem {

    /*
     * Stack empty shulkers on the ground CARPET-XCOM
     */
    @Override
    public boolean itemGroundStacking(boolean hasTag){
        return !hasTag && CarpetSettings.getBool("stackableEmptyShulkerBoxes");
    }

}
