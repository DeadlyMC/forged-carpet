package carpet.forge.mixin;

import carpet.forge.CarpetServer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;makeEntityOutlineShader()V"))
    private void carpetGameStarted(CallbackInfo ci)
    {
        CarpetServer.onGameStarted();
    }
}
