package carpet.forge.mixin;

import carpet.forge.helper.TickSpeed;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {
	@Inject(method = "processInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;setEntityActionState(FFZZ)V"))
	private void isPlayerMoving(CPacketInput packetIn, CallbackInfo ci) {
		// [FCM] Checking if player is moving, for commandTick
		if (packetIn.getStrafeSpeed() != 0.0F || packetIn.getForwardSpeed() != 0.0F || packetIn.isJumping()
				|| packetIn.isSneaking()) {
			TickSpeed.reset_player_active_timeout();
		}
	}
}
