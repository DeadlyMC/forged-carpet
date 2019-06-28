package carpet.forge.mixin;

import carpet.forge.CarpetServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList
{
    @Inject(method = "playerLoggedIn", at = @At(value = "RETURN"))
    private void onPlayerLoggedIn(EntityPlayerMP playerIn, CallbackInfo ci)
    {
        CarpetServer.playerConnected(playerIn);
    }

    @Inject(method = "playerLoggedOut", at = @At(value = "HEAD"))
    private void onPlayerLoggedOut(EntityPlayerMP playerIn, CallbackInfo ci)
    {
        CarpetServer.playerDisconnected(playerIn);
    }
}
