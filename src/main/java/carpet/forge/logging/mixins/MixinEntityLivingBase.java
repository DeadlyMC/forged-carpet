package carpet.forge.logging.mixins;

import carpet.forge.logging.logHelpers.DamageReporter;
import carpet.forge.utils.IMixinEntityLivingBase;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase implements IMixinEntityLivingBase {

    @Shadow protected float lastDamage;

    @Shadow public abstract Iterable<ItemStack> getArmorInventoryList();

    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    @Shadow public abstract int getTotalArmorValue();

    @Shadow protected abstract float applyArmorCalculations(DamageSource source, float damage);

    @Shadow public abstract float getAbsorptionAmount();

    @Shadow protected boolean isJumping;

    @Shadow @Nullable public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    @Override
    public boolean getJumping(){
        return this.isJumping;
    }

    public long birthTick;
    public String cm_name() { return "Other Living Entity"; }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void onEntityLivingBase(World worldIn, CallbackInfo ci){
        this.birthTick = worldIn.getTotalWorldTime();
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getHealth()F", shift = At.Shift.BEFORE))
    private void registerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        DamageReporter.register_damage((EntityLivingBase)(Object) this, source, amount);
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "RETURN", ordinal = 3,shift = At.Shift.BEFORE))
    private void modifyDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        DamageReporter.modify_damage((EntityLivingBase)(Object) this, source, amount, 0.0f, "Already dead and can't take more damage");
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "RETURN", ordinal = 4,shift = At.Shift.BEFORE))
    private void modifyDamageFire(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        DamageReporter.modify_damage((EntityLivingBase)(Object) this, source, amount, 0.0f, "Resistance to fire");
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;", ordinal = 1, shift = At.Shift.AFTER))
    private void modifyDamageHelmet(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        DamageReporter.modify_damage((EntityLivingBase)(Object) this, source, amount, amount*0.75f, "wearing a helmet");
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;damageShield(F)V", shift = At.Shift.AFTER))
    private void modifyDamageShield(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        DamageReporter.modify_damage((EntityLivingBase)(Object) this, source, amount, 0.0f, "using a shield");
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "RETURN", ordinal = 5))
    private void modifyDamageRecentlyHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        DamageReporter.modify_damage((EntityLivingBase)(Object)this, source, amount, 0.0f, "Recently hit");
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;damageEntity(Lnet/minecraft/util/DamageSource;F)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void modifyDamageRecentHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        DamageReporter.modify_damage((EntityLivingBase)(Object) this, source, amount, amount - this.lastDamage, "Recently hit");
    }

    @Inject(method = "applyPotionDamageCalculations", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getActivePotionEffect(Lnet/minecraft/potion/Potion;)Lnet/minecraft/potion/PotionEffect;", shift = At.Shift.AFTER))
    private void modifyDamageResistance(DamageSource source, float damage, CallbackInfoReturnable<Float> cir){
        int i = (this.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
        int j = 25 - i;
        float f = damage * (float)j;
        DamageReporter.modify_damage((EntityLivingBase)(Object)this, source, damage, f / 25.0F, "Resistance status effect");
        damage = f / 25.0F;
    }

    @Inject(method = "applyPotionDamageCalculations", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CombatRules;getDamageAfterMagicAbsorb(FF)F", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void modifyDamageEnchantments(DamageSource source, float damage, CallbackInfoReturnable<Float> cir, int  k){
        float previous_damage = damage;
        DamageReporter.modify_damage((EntityLivingBase)(Object) this, source, previous_damage, damage,
                String.format("enchantments (%.1f total points)", (float)k));
    }

    @Inject(method = "damageEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;applyArmorCalculations(Lnet/minecraft/util/DamageSource;F)F", shift = At.Shift.AFTER))
    private void modifyDamageArmorToughness(DamageSource damageSrc, float damageAmount, CallbackInfo ci){
        float previous_amount = damageAmount;
        DamageReporter.modify_damage((EntityLivingBase)(Object) this, damageSrc, previous_amount, damageAmount,
                String.format("Armour %.1f, Toughness %.1f", (float)this.getTotalArmorValue(), (float)this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
    }

    @Inject(method = "damageEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;setAbsorptionAmount(F)V", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void modifyDamageAbsorption(DamageSource damageSrc, float damageAmount, CallbackInfo ci, float f){
        DamageReporter.modify_damage((EntityLivingBase)(Object) this, damageSrc, damageAmount, f, "Absorbtion");
    }

    @Inject(method = "damageEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getHealth()F", shift = At.Shift.AFTER))
    private void registerFinalDamage(DamageSource damageSrc, float damageAmount, CallbackInfo ci){
        DamageReporter.register_final_damage((EntityLivingBase)(Object) this, damageSrc, damageAmount);
    }
}
