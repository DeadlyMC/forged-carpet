package carpet.forge.config.gui;

import net.minecraftforge.fml.client.config.IConfigElement;
import carpet.forge.config.PatchDef;

public interface ICarpetConfigElement extends IConfigElement
{
    boolean isToggleable();
    String getCredits();
    String getSideEffects();
    PatchDef getPatchDef();
}
