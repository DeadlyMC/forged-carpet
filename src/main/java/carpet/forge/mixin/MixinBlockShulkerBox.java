package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockShulkerBox.class)
public abstract class MixinBlockShulkerBox extends BlockContainer
{
    protected MixinBlockShulkerBox(Material materialIn, MapColor color)
    {
        super(materialIn, color);
    }

    // [FCM] Stackable empty shulker boxes - if statement around a single line of code
    @Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setTagCompound(Lnet/minecraft/nbt/NBTTagCompound;)V"))
    private void ifSetTagCompound(ItemStack itemStack, NBTTagCompound nbt)
    {
        if (!CarpetSettings.stackableEmptyShulkerBoxes || nbt.getCompoundTag("BlockEntityTag").getSize() > 0)
            itemStack.setTagCompound(nbt);
    }
}
