package carpet.forge.logging.mixins;

import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer {
   // @Redirect(method = "tickUpdates", at = @At(value = "INVOKE", target = ""))
}
