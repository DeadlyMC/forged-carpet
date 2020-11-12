package carpet.forge.logging.mixins;

import carpet.forge.fakes.IGuiPlayerTabOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngameForge.class)
public abstract class GuiInGameForgeMixin extends GuiIngame
{
    public GuiInGameForgeMixin(Minecraft mcIn)
    {
        super(mcIn);
    }
    
    @Redirect(method = "renderPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isIntegratedServerRunning()Z"))
    private boolean onRenderPlayerList(Minecraft mc)
    {
        return mc.isIntegratedServerRunning() && !((IGuiPlayerTabOverlay) this.overlayPlayerList).hasHeaderOrFooter();
    }

}
