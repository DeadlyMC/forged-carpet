package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {

    @Shadow @Final public WorldServer world;

    @Inject(method = "saveChunks", at = @At(value = "HEAD"))
    private void procLightUpdatesNewLight1(boolean all, CallbackInfoReturnable<Boolean> cir){
        if (CarpetSettings.newLight)
            ((IWorld) this.world).getLightingEngine().procLightUpdates();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
    private void procLightUpdatesNewLight2(CallbackInfoReturnable<Boolean> cir){
        if (CarpetSettings.newLight)
            ((IWorld) this.world).getLightingEngine().procLightUpdates();
    }
}
