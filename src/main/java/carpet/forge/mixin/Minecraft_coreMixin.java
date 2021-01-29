package carpet.forge.mixin;

import carpet.forge.CarpetServer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class Minecraft_coreMixin
{
    @Inject(method = "init", at = @At("RETURN"))
    private void carpetGameStarted(CallbackInfo ci)
    {
        CarpetServer.onGameStarted(Side.CLIENT);
    }
}
