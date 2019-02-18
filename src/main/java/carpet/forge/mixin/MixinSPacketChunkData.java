package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SPacketChunkData.class)
public abstract class MixinSPacketChunkData
{
    @Inject(method = "<init>(Lnet/minecraft/world/chunk/Chunk;I)V", at = @At(value = "RETURN"))
    private void procLightUpdatesNewLight(Chunk chunkIn, int changedSectionFilter, CallbackInfo ci)
    {
        if (CarpetSettings.newLight)
            ((IWorld) chunkIn.getWorld()).getLightingEngine().procLightUpdates();
    }
}
