package carpet.forge.mixin.newlight;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// CREDITS : Nessie
@Mixin(SPacketChunkData.class)
public abstract class SPacketChunkData_newLightMixin
{
    @Inject(method = "calculateChunkSize", at = @At("HEAD"))
    private void onInit(Chunk chunkIn, boolean flag, int changedSectionFilter, CallbackInfoReturnable<Integer> cir)
    {
        if (CarpetSettings.newLight)
        {
            ((IWorld) chunkIn.getWorld()).getLightingEngine().procLightUpdates();
        }
    }
}
