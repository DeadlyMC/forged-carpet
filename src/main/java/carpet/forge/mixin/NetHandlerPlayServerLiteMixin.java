package carpet.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import carpet.forge.helper.TickSpeed;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.WorldServer;

@Mixin(NetHandlerPlayServer.class)
public class NetHandlerPlayServerLiteMixin
{
	@Inject(method = "processPlayer", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/player/EntityPlayerMP;isPlayerSleeping()Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void resetPlayerActiveTimeoutLite(CPacketPlayer packetIn, CallbackInfo ci, WorldServer worldserver, double d0,
			double d1, double d2, double d3, double d4, double d5, double d6, float f, float f1, double d7, double d8,
			double d9, double d10, double d11, CallbackInfo ci1) {
		if (d11 > 0.0001D) // for commandTick
		{
			TickSpeed.reset_player_active_timeout();
		}
	}
}
