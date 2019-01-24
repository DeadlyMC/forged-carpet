package carpet.forge.logging.mixins;

import carpet.forge.logging.LoggerRegistry;
import carpet.forge.logging.logHelpers.DamageReporter;
import carpet.forge.logging.logHelpers.KillLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    int mobs_smashed = 1;

    @Inject(method = "damageEntity", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ISpecialArmor$ArmorProperties;applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", shift = At.Shift.AFTER))
    private void modifyDamageArmorToughness(DamageSource damageSrc, float damageAmount, CallbackInfo ci){
        float previous_amount = damageAmount;
        DamageReporter.modify_damage(this, damageSrc, previous_amount, damageAmount,
                String.format("armour %.1f and toughness %.1f", (float)this.getTotalArmorValue(), (float)this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
    }

    @Inject(method = "damageEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setAbsorptionAmount(F)V", shift = At.Shift.AFTER))
    private void modifyDamageAbsorption(DamageSource damageSrc, float damageAmount, CallbackInfo ci) {
        float h = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
        DamageReporter.modify_damage(this, damageSrc, damageAmount, h, "Absorbtion");
    }

    @Inject(method = "damageEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getHealth()F", ordinal = 0, shift = At.Shift.AFTER))
    private void registerFinalDamage(DamageSource damageSrc, float damageAmount, CallbackInfo ci){
        DamageReporter.register_final_damage(this, damageSrc, damageAmount);
    }

    @Inject(method = "damageEntity", at = @At("TAIL"))
    private void modifyDamage(DamageSource damageSrc, float damageAmount, CallbackInfo ci){
        if (this.isEntityInvulnerable(damageSrc)) {
            DamageReporter.modify_damage(this, damageSrc, damageAmount, 0.0f, "invulnerability to the damage source");
        }
    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void incrementMobsSmashed(Entity targetEntity, CallbackInfo ci){
        mobs_smashed++;
    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/EntityPlayer;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void loggerRegisteryKillsSweep(Entity targetEntity, CallbackInfo ci){
        if (LoggerRegistry.__kills)
            KillLogHelper.onSweep((EntityPlayer)(Object) this, mobs_smashed);
    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setLastAttackedEntity(Lnet/minecraft/entity/Entity;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void LoggerRegistryKillsNonSweep(Entity targetEntity, CallbackInfo ci, float  f, float  f1, float  f2, boolean  flag, boolean  flag1, int  i, boolean  flag2, CriticalHitEvent hitResult, boolean  flag3){
        if (!flag3)
        {
            if (LoggerRegistry.__kills)
                KillLogHelper.onNonSweepAttack((EntityPlayer)(Object)this);
        }
    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "RETURN", target = "Lnet/minecraft/entity/Entity;canBeAttackedWithItem()Z"))
    private void loggerRegistryKillsHit(Entity targetEntity, CallbackInfo ci){
        if (targetEntity.hitByEntity(this))
        {
            if (LoggerRegistry.__kills)
                KillLogHelper.onDudHit((EntityPlayer)(Object)this);
        }
    }

}
