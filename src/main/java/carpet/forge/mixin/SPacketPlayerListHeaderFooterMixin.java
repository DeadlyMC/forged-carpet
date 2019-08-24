package carpet.forge.mixin;

import carpet.forge.interfaces.ISPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SPacketPlayerListHeaderFooter.class)
public abstract class SPacketPlayerListHeaderFooterMixin implements ISPacketPlayerListHeaderFooter
{
    @Shadow private ITextComponent header;
    
    @Shadow private ITextComponent footer;
    
    @Override
    public void setHeader(ITextComponent headerIn)
    {
        this.header = headerIn;
    }
    
    @Override
    public void setFooter(ITextComponent footerIn)
    {
        this.footer = footerIn;
    }
}
