package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityLlama.class)
public abstract class EntityLlamaMixin
{
    @Redirect(
            method = "handleEating",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/EntityLlama;isTame()Z", ordinal = 0)
    )
    private boolean onHandleEating(EntityLlama entityLlama)
    {
        return entityLlama.isTame() && !(CarpetSettings.llamaOverfeedingFix && entityLlama.isInLove());
    }

}
