package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.TickingArea;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer
{

    @Shadow
    @Final
    public WorldServer world;

    @Shadow
    @Final
    public Set<Long> droppedChunks;

    @Inject(method = "queueUnload", at = @At("HEAD"))
    private void tickAndDisableSpawnRule(Chunk chunkIn, CallbackInfo ci)
    {
        boolean canDrop = world.provider.canDropChunk(chunkIn.x, chunkIn.z);
        if (CarpetSettings.disableSpawnChunks)
            canDrop = true;
        if (CarpetSettings.tickingAreas)
            canDrop &= !TickingArea.isTickingChunk(world, chunkIn.x, chunkIn.z);
        if (canDrop)
        {
            this.droppedChunks.add(Long.valueOf(ChunkPos.asLong(chunkIn.x, chunkIn.z)));
            chunkIn.unloadQueued = true;
        }
    }

    @Redirect(method = "queueUnload", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;canDropChunk(II)Z"))
    private boolean cancelCanDropChunk(WorldProvider worldProvider, int x, int z)
    {
        return false;
    }
}
