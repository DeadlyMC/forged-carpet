package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.HopperCounter;
import carpet.forge.utils.WoolTool;
import net.minecraft.block.BlockHopper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityHopper.class)
public abstract class TileEntityHopperMixin extends TileEntityLockableLoot
{
    @Shadow
    public abstract double getXPos();

    @Shadow
    public abstract double getYPos();

    @Shadow
    public abstract double getZPos();

    @Shadow
    public abstract int getSizeInventory();

    @Shadow
    public abstract void setInventorySlotContents(int index, ItemStack stack);

    private EnumDyeColor get_wool_pointing()
    {
        return WoolTool.getWoolColorAtPosition(getWorld(), new BlockPos(getXPos(), getYPos(), getZPos()).offset(BlockHopper.getFacing(this.getBlockMetadata())));

    }

    @Inject(method = "transferItemsOut", at = @At("HEAD"), cancellable = true)
    private void transferItemsOut(CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.hopperCounters)
        {
            EnumDyeColor wool_color = this.get_wool_pointing();
            if (wool_color != null)
            {
                for (int i = 0; i < this.getSizeInventory(); ++i)
                {
                    if (!this.getStackInSlot(i).isEmpty())
                    {
                        ItemStack itemstack = this.getStackInSlot(i);//.copy();
                        HopperCounter.COUNTERS.get(wool_color).add(this.getWorld().getMinecraftServer(), itemstack);
                        this.setInventorySlotContents(i, ItemStack.EMPTY);
                    }
                }
                cir.setReturnValue(true);
            }
        }
    }

}
