package carpet.forge.logging.mixins;

import carpet.forge.logging.logHelpers.DamageReporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySlime.class)
public abstract class MixinEntitySlime {

    @Shadow protected abstract int getAttackStrength();

    @Redirect(method = "dealDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntitySlime;canEntityBeSeen(Lnet/minecraft/entity/Entity;)Z"))
    private boolean registerDamageAttacker(EntitySlime entitySlime, Entity entityIn){
        return entitySlime.canEntityBeSeen(entityIn) &&
                DamageReporter.register_damage_attacker((EntityLivingBase)entityIn, ((EntitySlime)(Object) this), (float)this.getAttackStrength());
    }

}
