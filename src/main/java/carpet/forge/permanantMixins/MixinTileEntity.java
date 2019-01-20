package carpet.forge.permanantMixins;

import carpet.forge.utils.IMixinTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntity.class)
public abstract class MixinTileEntity implements IMixinTileEntity
{
    public String cm_name() { return "Other Tile Entity"; }
}
