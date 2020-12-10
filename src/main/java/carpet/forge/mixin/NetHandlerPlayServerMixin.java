package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.TickSpeed;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class NetHandlerPlayServerMixin
{
	@Inject(method = "processInput", at = @At("RETURN"))
	private void onProcessInput(CPacketInput packetIn, CallbackInfo ci)
	{
		// [FCM] Checking if player is moving, for commandTick
		if (packetIn.getStrafeSpeed() != 0.0F || packetIn.getForwardSpeed() != 0.0F || packetIn.isJumping() || packetIn.isSneaking())
		{
			TickSpeed.reset_player_active_timeout();
		}
	}
	
	@Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;isInvulnerableDimensionChange()Z", ordinal = 0))
	private boolean onProcessPlayer(EntityPlayerMP playerMP)
	{
		return playerMP.isInvulnerableDimensionChange() && CarpetSettings.antiCheatSpeed;
	}
}
