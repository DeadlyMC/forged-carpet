package carpet.forge.logging.mixins;

import carpet.forge.logging.LoggerRegistry;
import carpet.forge.logging.logHelpers.TNTLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTNTPrimed.class)
public abstract class MixinEntityTNTPrimed extends Entity
{
    private TNTLogHelper logHelper = null;

    public MixinEntityTNTPrimed(World worldIn)
    {
        super(worldIn);
    }

    public String cm_name()
    {
        return "Primed TNT";
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/EntityLivingBase;)V", at = @At(value = "RETURN"))
    private void loggerRegistryTNT(World worldIn, double x, double y, double z, EntityLivingBase igniter, CallbackInfo ci)
    {
        float h = (float) (Math.random() * (Math.PI * 2D));
        if (LoggerRegistry.__tnt)
        {
            logHelper = new TNTLogHelper();
            logHelper.onPrimed(x, y, z, h);
        }
    }

    @Inject(method = "explode", at = @At(value = "HEAD"))
    private void loggerRegistryTNTOnExplode(CallbackInfo ci)
    {
        if (LoggerRegistry.__tnt && logHelper != null)
            logHelper.onExploded(posX, posY, posZ);
    }

}
