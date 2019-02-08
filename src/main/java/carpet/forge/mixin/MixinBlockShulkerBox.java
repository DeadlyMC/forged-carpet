package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockShulkerBox.class)
public abstract class MixinBlockShulkerBox extends BlockContainer {

    protected MixinBlockShulkerBox(Material materialIn, MapColor color) {
        super(materialIn, color);
    }

    /**
     * @author DeadlyMC
     * @reason StackableShulkerBoxes
     */
    @Overwrite
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityShulkerBox)
        {
            TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox)tileentity;

            if (!tileentityshulkerbox.isCleared() && tileentityshulkerbox.shouldDrop())
            {
                ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound.setTag("BlockEntityTag", ((TileEntityShulkerBox)tileentity).saveToNbt(nbttagcompound1));
                // [FCM] Added if statement
                if(!CarpetSettings.getBool("stackableEmptyShulkerBoxes") || nbttagcompound1.getSize() > 0) itemstack.setTagCompound(nbttagcompound);

                if (tileentityshulkerbox.hasCustomName())
                {
                    itemstack.setStackDisplayName(tileentityshulkerbox.getName());
                    tileentityshulkerbox.setCustomName("");
                }

                spawnAsEntity(worldIn, pos, itemstack);
            }

            worldIn.updateComparatorOutputLevel(pos, state.getBlock());
        }

        super.breakBlock(worldIn, pos, state);
    }

}
