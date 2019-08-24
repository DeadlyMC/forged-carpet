package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(World.class)
public abstract class World_fillUpdatesMixin
{
    @ModifyConstant(method = "markAndNotifyBlock", constant = @Constant(intValue = 16), remap = false)
    private int onMarkAndNotifyBlock(int original)
    {
        if (CarpetSettings.impendingFillSkipUpdates)
            return 128;
        return original;
    }
}
