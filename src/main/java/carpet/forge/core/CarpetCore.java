package carpet.forge.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-10000)
@IFMLLoadingPlugin.TransformerExclusions("carpet.forge.core.CarpetCore")
public class CarpetCore implements IFMLLoadingPlugin {

    public static final Logger log = LogManager.getLogger();

    public CarpetCore() {

        MixinBootstrap.init();

        Mixins.addConfiguration("mixins.forgedcarpet112.json");
        Mixins.addConfiguration("mixins.carpet.logging.json");

    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]
                {

                };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
