package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.LightingHooks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

// CREDITS : Nessie
@Mixin(Chunk.class)
public abstract class ChunkClient_newLightMixin
{
    @Shadow
    @Final
    private World world;
    @Shadow
    @Final
    private int[] heightMap;
    
    @Shadow
    protected abstract void generateHeightMap();
    
    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;generateHeightMap()V"))
    private void weAlsoCallThisElsewhere(Chunk chunk)
    {
        if (!CarpetSettings.newLight)
            this.generateHeightMap();
    }
    
    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;generateHeightMap()V", shift = At.Shift.AFTER))
    private void preGenerateHeightMap(PacketBuffer buf, int availableSections, boolean groundUpContinuous, CallbackInfo ci)
    {
        if (CarpetSettings.newLight)
        {
            final int[] oldHeightMap = groundUpContinuous ? null : Arrays.copyOf(heightMap, heightMap.length);
            this.generateHeightMap();
            LightingHooks.relightSkylightColumns(world, (Chunk) (Object) this, oldHeightMap);
        }
    }
}
