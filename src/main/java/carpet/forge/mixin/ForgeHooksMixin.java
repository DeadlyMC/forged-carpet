package carpet.forge.mixin;

import carpet.forge.patches.NetworkManagerFake;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ForgeHooks.class)
public abstract class ForgeHooksMixin
{
    @Redirect(method = "sendRecipeBook", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/handshake/NetworkDispatcher;get(Lnet/minecraft/network/NetworkManager;)Lnet/minecraftforge/fml/common/network/handshake/NetworkDispatcher;"), remap = false)
    private static NetworkDispatcher handleNPE(NetworkManager manager)
    {
        // Prevent NPE caused due to NetworkManager.channel()
        // Returning null directly doesnt cause any NPE's and forge handles the NPE in the next line, so all good.
        if (manager instanceof NetworkManagerFake)
            return null;
        return NetworkDispatcher.get(manager);
    }
}
