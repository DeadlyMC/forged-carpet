package carpet.forge.mixin;

import carpet.forge.patches.EntityPlayerMPFake;
import carpet.forge.patches.NetHandlerPlayServerFake;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin
{
    @Shadow @Final private MinecraftServer server;
    
    @Inject(method = "initializeConnectionToPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerInteractionManager;setWorld(Lnet/minecraft/world/WorldServer;)V"))
    private void resetToSetPosition(NetworkManager netManager, EntityPlayerMP playerIn, NetHandlerPlayServer nethandlerplayserver, CallbackInfo ci)
    {
        if (playerIn instanceof EntityPlayerMPFake)
        {
            // Ignore position from NBT and use the one specified in the command
            ((EntityPlayerMPFake) playerIn).resetToSetPosition();
        }
    }
    
    @ModifyVariable(
            method = "initializeConnectionToPlayer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/management/PlayerList;setPlayerGameTypeBasedOnOther(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/world/World;)V"),
            index = 3
    )
    private NetHandlerPlayServer handleFakePlayer(NetHandlerPlayServer originalNetHandler, NetworkManager netManager, EntityPlayerMP playerIn, NetHandlerPlayServer nethandlerplayserver)
    {
        if (playerIn instanceof EntityPlayerMPFake)
            return new NetHandlerPlayServerFake(this.server, netManager, playerIn);
        return originalNetHandler;
    }
}
