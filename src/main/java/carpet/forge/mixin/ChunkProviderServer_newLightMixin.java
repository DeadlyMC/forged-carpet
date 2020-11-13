package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.fakes.IWorld;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderServer.class)
public abstract class ChunkProviderServer_newLightMixin
{
    @Shadow
    @Final
    private WorldServer world;
    
    @Inject(method = "saveChunks", at = @At("HEAD"))
    private void onSaveChunks(boolean all, CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.newLight)
            ((IWorld) this.world).getLightingEngine().procLightUpdates();
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", remap = false))
    private void onTick(CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.newLight)
            ((IWorld) this.world).getLightingEngine().procLightUpdates();
    }
}
