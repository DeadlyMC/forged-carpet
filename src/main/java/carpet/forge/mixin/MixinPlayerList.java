package carpet.forge.mixin;

import carpet.forge.CarpetMain;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method = "playerLoggedIn", at = @At(value = "RETURN"))
    public void onPlayerLoggedIn(EntityPlayerMP playerIn, CallbackInfo ci){
        CarpetMain.playerConnected(playerIn);
    }

    @Inject(method = "playerLoggedOut", at = @At(value = "HEAD"))
    public void onPlayerLoggedOut(EntityPlayerMP playerIn, CallbackInfo ci){
        CarpetMain.playerDisconnected(playerIn);
    }
}
