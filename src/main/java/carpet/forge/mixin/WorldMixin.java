package carpet.forge.mixin;

import carpet.forge.fakes.IWorld;
import carpet.forge.utils.LightingEngine;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld
{
    @Final
    @Mutable
    private LightingEngine lightingEngine;
    
    @Override
    public LightingEngine getLightingEngine()
    {
        return this.lightingEngine;
    }
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn,
            Profiler profilerIn, boolean client, CallbackInfo ci)
    {
        this.lightingEngine = new LightingEngine((World) (Object) this);
    }
}
