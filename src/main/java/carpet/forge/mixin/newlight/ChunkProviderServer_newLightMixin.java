package carpet.forge.mixin.newlight;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

// CREDITS : Nessie
@Mixin(ChunkProviderServer.class)
public abstract class ChunkProviderServer_newLightMixin
{
    @Shadow
    @Final
    private WorldServer world;
    
    @Shadow @Final public Set<Long> droppedChunks;
    
    @Inject(method = "saveChunks", at = @At("HEAD"))
    private void onSaveChunks(boolean all, CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.newLight)
        {
            ((IWorld) this.world).getLightingEngine().procLightUpdates();
        }
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z", remap = false))
    private boolean foo(Set set)
    {
        if (CarpetSettings.newLight)
        {
            final boolean isEmpty = set.isEmpty();
            if (!isEmpty)
            {
                ((IWorld) this.world).getLightingEngine().procLightUpdates();
            }
            return isEmpty;
        }
        else
        {
            return !this.droppedChunks.isEmpty();
        }
    }
}
