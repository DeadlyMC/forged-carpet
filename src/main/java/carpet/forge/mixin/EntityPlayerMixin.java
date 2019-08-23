package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.PortalHelper;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin
{
    @ModifyConstant(method = "getMaxInPortalTime", constant = @Constant(intValue = 1))
    private int onGetMaxInPortalTime(int original)
    {
        if (CarpetSettings.portalCreativeDelay)
            if (PortalHelper.player_holds_obsidian((EntityPlayer) (Object)this))
                return 72000;
            else
                return 80;
        return original;
    }
}
