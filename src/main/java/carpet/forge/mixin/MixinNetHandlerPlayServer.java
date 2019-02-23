package carpet.forge.mixin;

import carpet.forge.helper.TickSpeed;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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

	@Inject(method = "processPlayer", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/player/EntityPlayerMP;isPlayerSleeping()Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void resetPlayerActiveTimeout(CPacketPlayer packetIn, CallbackInfo ci, WorldServer worldserver, double d0,
			double d1, double d2, double d3, double d4, double d5, double d6, float f, float f1, double d7, double d8,
			double d9, double d10, double d11, CallbackInfo ci1) {
		if (d11 > 0.0001D) // for commandTick
		{
			TickSpeed.reset_player_active_timeout();
		}
	}
}
