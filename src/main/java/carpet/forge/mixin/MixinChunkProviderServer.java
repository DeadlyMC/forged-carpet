package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.TickingArea;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {

    @Shadow @Final public WorldServer world;

    @Shadow @Final public Set<Long> droppedChunks;

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

    /**
     * @author DeadlyMC
     * @reason TickingAreas and DisableSpawnChunks rule
     */
    @Overwrite
    public void queueUnload(Chunk chunkIn)
    {
        boolean canDrop = world.provider.canDropChunk(chunkIn.x, chunkIn.z);
        if (CarpetSettings.getBool("disableSpawnChunks"))
            canDrop = true;
        if (CarpetSettings.getBool("tickingAreas"))
            canDrop &= !TickingArea.isTickingChunk(world, chunkIn.x, chunkIn.z);
        if (canDrop)
        {
            this.droppedChunks.add(Long.valueOf(ChunkPos.asLong(chunkIn.x, chunkIn.z)));
            chunkIn.unloadQueued = true;
        }
    }
}
