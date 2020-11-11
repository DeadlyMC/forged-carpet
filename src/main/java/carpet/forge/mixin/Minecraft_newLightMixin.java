package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.fakes.IWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// CREDITS : Nessie
@Mixin(Minecraft.class)
public abstract class Minecraft_newLightMixin
{
    @Shadow
    public WorldClient world;
    
    @Shadow
    @Final
    public Profiler profiler;
    
    @Inject(method = "runTick", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
            args = "ldc=levelRenderer"))
    private void preEndStartSectionLevelRenderer(CallbackInfo ci)
    {
        if (CarpetSettings.newLight)
        {
            this.profiler.endStartSection("lighting");
            ((IWorld) this.world).getLightingEngine().procLightUpdates();
        }
    }
}
