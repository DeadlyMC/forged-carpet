package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockRailPowered;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockRailPowered.class)
public abstract class BlockRailPoweredMixin
{
    @ModifyConstant(method = "findPoweredRailSignal", constant = @Constant(intValue = 8))
    private int powerLimit(int original)
    {
        return CarpetSettings.railPowerLimit - 1;
    }
}
