package carpet.forge.mixin;

import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityLlama.class)
public abstract class MixinEntityLlama {

    @Redirect(method = "handleEating", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/EntityLlama;isTame()Z"))
    public boolean isLlamaFix(EntityLlama self) {
        return self.isTame() && !self.isInLove();
    }

}
