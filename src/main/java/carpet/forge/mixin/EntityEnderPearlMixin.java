package carpet.forge.mixin;

import carpet.forge.patches.EntityPlayerMPFake;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityEnderPearl.class)
public abstract class EntityEnderPearlMixin
{
    @Unique
    private EntityPlayerMP copyOfPlayer;
    
    @Inject(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;isPlayerSleeping()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void copyPlayer(RayTraceResult result, CallbackInfo ci, EntityLivingBase entitylivingbase, EntityPlayerMP entityplayermp)
    {
        this.copyOfPlayer = entityplayermp;
    }
    
    @Redirect(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;isChannelOpen()Z"))
    private boolean handleFakePlayers(NetworkManager networkManager)
    {
        return networkManager.isChannelOpen() || this.copyOfPlayer instanceof EntityPlayerMPFake;
    }
}
