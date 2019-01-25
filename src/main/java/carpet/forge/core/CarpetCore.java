package carpet.forge.core;

import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-10000)
@IFMLLoadingPlugin.TransformerExclusions("carpet.forge.core.CarpetCore")
public class CarpetCore implements IFMLLoadingPlugin {

    public static final Logger log = LogManager.getLogger();
    private static boolean initialized = false;

    public static CarpetSettings config;


    public CarpetCore() {
        initialize();

        MixinBootstrap.init();

        Mixins.addConfiguration("mixins.carpet.permanant.json");
        Mixins.addConfiguration("mixins.carpet.logging.json");

        if (config.rsturbo)              Mixins.addConfiguration("mixins.carpet.rsturbo.json");
        if (config.newlight)             Mixins.addConfiguration("mixins.carpet.newlight.json");
        if (config.llamafix)             Mixins.addConfiguration("mixins.carpet.llamafix.json");
        if (config.miningGhostBlocks)    Mixins.addConfiguration("mixins.carpet.miningghostblock.json");
        if (config.pistonGhostBlocks)    Mixins.addConfiguration("mixins.carpet.pistonghostblock.json");
        if (config.boundingBoxFix)       Mixins.addConfiguration("mixins.carpet.bbFix.json");
        if (config.tntDoNotUpdate)       Mixins.addConfiguration("mixins.carpet.tnt.json");
        if (config.optimizedDespawnRange)Mixins.addConfiguration("mixins.carpet.despawn.json");

    }

    public static void initialize() {
        if(initialized) return;
        initialized = true;

        config = new CarpetSettings();
        config.init(new File(((File)(FMLInjectionData.data()[6])), "config/fcarpet1122.cfg"));
    }


    @Override
    public String[] getASMTransformerClass()
    {
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
