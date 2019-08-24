package carpet.forge.interfaces;

import net.minecraft.util.text.ITextComponent;

public interface ISPacketPlayerListHeaderFooter
{
    void setHeader(ITextComponent headerIn);
    void setFooter(ITextComponent footerIn);
}
