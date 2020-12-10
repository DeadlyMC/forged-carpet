package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityXPOrb.class)
public abstract class EntityXPOrbMixin
{
    @Inject(method = "onCollideWithPlayer", at = @At("HEAD"))
    private void onOnCollidedWithPlayer(EntityPlayer entityIn, CallbackInfo ci)
    {
        if (CarpetSettings.xpNoCooldown)
            entityIn.xpCooldown = 0;
    }
}
