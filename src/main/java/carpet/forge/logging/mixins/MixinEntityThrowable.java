package carpet.forge.logging.mixins;

import carpet.forge.logging.LoggerRegistry;
import carpet.forge.logging.logHelpers.TrajectoryLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityThrowable.class)
public abstract class MixinEntityThrowable extends Entity {

    public MixinEntityThrowable(World worldIn) {
        super(worldIn);
    }

    private TrajectoryLogHelper logHelper = null;

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At(value = "RETURN"))
    private void onEntityThrowable(World worldIn, CallbackInfo ci){
        if (LoggerRegistry.__projectiles)
        {
            logHelper = new TrajectoryLogHelper("projectiles");
        }
    }

    @Inject(method = "onUpdate", at = @At(value = "RETURN"))
    private void onOnUpdate(CallbackInfo ci){
        if (LoggerRegistry.__projectiles && logHelper != null)
        {
            logHelper.onTick(posX, posY, posZ, motionX, motionY, motionZ);
        }
    }

    @Override
    public void setDead() {
        super.setDead();
        if (LoggerRegistry.__projectiles && logHelper != null)
            logHelper.onFinish();
    }

}
