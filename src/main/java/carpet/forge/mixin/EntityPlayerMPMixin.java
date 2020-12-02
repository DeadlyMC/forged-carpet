package carpet.forge.mixin;

import carpet.forge.fakes.IEntityPlayerMP;
import carpet.forge.helper.EntityPlayerActionPack;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin implements IEntityPlayerMP
{
    private final EntityPlayerActionPack actionPack = new EntityPlayerActionPack((EntityPlayerMP) (Object) this);
    
    @Override
    public EntityPlayerActionPack getActionPack()
    {
        return actionPack;
    }
    
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci)
    {
        actionPack.onUpdate();
    }
}
