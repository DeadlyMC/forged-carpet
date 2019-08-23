package carpet.forge.mixin;

import carpet.forge.interfaces.ITileEntity;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntity.class)
public abstract class TileEntityMixin implements ITileEntity
{
    public String cm_name() { return "Other Tile Entity"; }
}
