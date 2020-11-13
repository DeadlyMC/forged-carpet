package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.fakes.IWorld;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SPacketChunkData.class)
public abstract class SPacketChunkData_newLightMixin
{
    @Redirect(
            method = "<init>(Lnet/minecraft/world/chunk/Chunk;I)V",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/World;provider:Lnet/minecraft/world/WorldProvider;")
    )
    private WorldProvider processLight(World world)
    {
        if (CarpetSettings.newLight) ((IWorld) world).getLightingEngine().procLightUpdates();
        return world.provider;
    }
}
