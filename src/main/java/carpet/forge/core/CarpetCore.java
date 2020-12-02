package carpet.forge.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-10000)
@IFMLLoadingPlugin.TransformerExclusions("carpet.forge.core.CarpetCore")
public class CarpetCore implements IFMLLoadingPlugin
{
    private static File minecraftDir;
    
    public CarpetCore()
    {
        
        MixinBootstrap.init();
        
        Mixins.addConfiguration("mixins.forgedcarpet.json");
        Mixins.addConfiguration("mixins.carpet.logging.json");
        
        try
        {
            Class.forName("com.mumfrey.liteloader.LiteMod");
            Mixins.addConfiguration("mixins.carpet.liteloader.json");
        }
        catch (ClassNotFoundException e)
        {
            Mixins.addConfiguration("mixins.carpet.forge.json");
        }
        
    }
    
    public static File getMinecraftDir()
    {
        return minecraftDir;
    }
    
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{
        
        };
    }
    
    @Override
    public String getModContainerClass()
    {
        return null;
    }
    
    @Nullable
    @Override
    public String getSetupClass()
    {
        return null;
    }
    
    @Override
    public void injectData(Map<String, Object> data)
    {
        minecraftDir = (File) data.get("mcLocation");
    }
    
    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
