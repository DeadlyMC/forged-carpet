package carpet.forge.mixin;

import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketPlayerListHeaderFooter.class)
public interface SPacketPlayerListHeaderFooterAccessor
{
    @Accessor("header")
    void setHeader(ITextComponent header);
    
    @Accessor("footer")
    void setFooter(ITextComponent footer);
}
