package carpet.forge.performance.newlight.mixin;

import carpet.forge.performance.newlight.IWorld;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SPacketChunkData.class)
public abstract class MixinSPacketChunkData {
    @Inject(method = "calculateChunkSize", at = @At("HEAD"))
    private void onInit(Chunk chunkIn, boolean flag, int changedSectionFilter, CallbackInfoReturnable<Integer> cir) {
        ((IWorld) chunkIn.getWorld()).getLightingEngine().procLightUpdates();
    }
}

