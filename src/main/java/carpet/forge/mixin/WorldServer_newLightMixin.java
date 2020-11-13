package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.fakes.IWorld;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class WorldServer_newLightMixin extends World
{
    protected WorldServer_newLightMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn,
            Profiler profilerIn, boolean client)
    {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V"))
    private void onTick(CallbackInfo ci)
    {
        if (CarpetSettings.newLight)
        {
            this.profiler.endStartSection("lighting");
            ((IWorld) this).getLightingEngine().procLightUpdates();
        }
    }
}
