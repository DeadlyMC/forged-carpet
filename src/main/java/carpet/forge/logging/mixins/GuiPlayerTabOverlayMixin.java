package carpet.forge.logging.mixins;

import carpet.forge.fakes.IGuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiPlayerTabOverlay.class)
public abstract class GuiPlayerTabOverlayMixin implements IGuiPlayerTabOverlay
{
    @Shadow private ITextComponent header;
    
    @Shadow private ITextComponent footer;
    
    @Override
    public boolean hasHeaderOrFooter()
    {
        return this.header != null || this.footer != null;
    }
}
