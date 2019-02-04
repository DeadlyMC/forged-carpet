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

    public CarpetCore() {

        MixinBootstrap.init();

        Mixins.addConfiguration("mixins.carpet.permanant.json");
        Mixins.addConfiguration("mixins.carpet.logging.json");
        Mixins.addConfiguration("mixins.carpet.rsturbo.json");
        Mixins.addConfiguration("mixins.carpet.newlight.json");
        Mixins.addConfiguration("mixins.carpet.llamafix.json");
        Mixins.addConfiguration("mixins.carpet.miningghostblock.json");
        Mixins.addConfiguration("mixins.carpet.pistonghostblock.json");
        Mixins.addConfiguration("mixins.carpet.bbFix.json");
        Mixins.addConfiguration("mixins.carpet.tnt.json");
        Mixins.addConfiguration("mixins.carpet.despawn.json");
        Mixins.addConfiguration("mixins.carpet.observertweak.json");
        Mixins.addConfiguration("mixins.carpet.craftingfix.json");
        Mixins.addConfiguration("mixins.carpet.flipcacti.json");

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
