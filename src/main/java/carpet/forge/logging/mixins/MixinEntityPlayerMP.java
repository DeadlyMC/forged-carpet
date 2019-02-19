package carpet.forge.logging.mixins;

import carpet.forge.logging.logHelpers.DamageReporter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP
{
    // Damage Reporter
    @Inject(method = "attackEntityFrom", at = @At(value = "RETURN", ordinal = 1))
    private void modifyDamageRespawnProtection(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        DamageReporter.modify_damage((EntityPlayerMP) (Object) this, source, amount, 0.0F, "respawn protection");
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "RETURN", ordinal = 2))
    private void modifyDamagePVPDisabled(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        DamageReporter.modify_damage((EntityPlayerMP) (Object) this, source, amount, 0.0F, "PVP disabled");
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "RETURN", ordinal = 3))
    private void modifyDamagePVPDisabled2(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        DamageReporter.modify_damage((EntityPlayerMP) (Object) this, source, amount, 0.0F, "PVP disabled");
    }

}
