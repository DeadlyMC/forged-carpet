package carpet.forge.logging.mixins;

import carpet.forge.logging.logHelpers.DamageReporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMob.class)
public abstract class EntityMobMixin extends EntityCreature
{

    float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    float ff = f;

    public EntityMobMixin(World worldIn)
    {
        super(worldIn);
    }

    public String cm_name()
    {
        return "Mob";
    }

    @Inject(method = "attackEntityAsMob", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/entity/monster/EntityMob;getEntityAttribute(Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;"))
    private void registerDamageAttacker(Entity entityIn, CallbackInfoReturnable<Boolean> cir)
    {
        DamageReporter.register_damage_attacker(entityIn, this, f);
    }

    @Inject(method = "attackEntityAsMob", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getKnockbackModifier(Lnet/minecraft/entity/EntityLivingBase;)I", shift = At.Shift.AFTER))
    private void modifyDamage(Entity entityIn, CallbackInfoReturnable<Boolean> cir)
    {
        DamageReporter.modify_damage((EntityLivingBase) entityIn, DamageSource.causeMobDamage(this), ff, f, "attacker enchants");
    }

}
