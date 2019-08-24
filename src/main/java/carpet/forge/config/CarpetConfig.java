package carpet.forge.config;

import carpet.forge.core.CarpetCore;
import carpet.forge.utils.Reference;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

//@Config(modid = Reference.MOD_ID)
public class CarpetConfig
{
    
    @Config.Name("FastRedstoneDust")
    @Config.Comment("Lag optimizations for redstone Dust. By Theosib")
    @Config.RequiresMcRestart
    public static boolean fastRedstoneDust;
    
    public boolean getFastRedstoneDust()
    {
        preload();
        return fastRedstoneDust;
    }
    
    @Config.Name("NewLight")
    @Config.Comment("Uses alternative lighting engine by PhiPros. AKA NewLight mod")
    @Config.RequiresMcRestart
    public static boolean newLight;
    
    public boolean getNewLight()
    {
        preload();
        return newLight;
    }
    
    private static void preload() {
        if (Loader.instance().getLoaderState() == LoaderState.NOINIT) {
            load();
        }
    }
    
    private static void load()
    {
        Map<String, Multimap<Config.Type, ASMDataTable.ASMData>> asm_data = ReflectionHelper.getPrivateValue(ConfigManager.class, null, "asm_data");
        Multimap<Config.Type, ASMDataTable.ASMData> map = HashMultimap.create();
        map.put(Config.Type.INSTANCE, new ASMDataTable.ASMData(null, null, CarpetConfig.class.getName(), null, Collections.emptyMap()));
        asm_data.put(Reference.MOD_ID, map);
        File configDir;
        try
        {
            configDir = new File(CarpetCore.getMinecraftDir(), "config").getCanonicalFile();
        }
        catch (IOException e)
        {
            throw new LoaderException(e);
        }
        ReflectionHelper.setPrivateValue(Loader.class, Loader.instance(), configDir, "canonicalConfigDir");
        ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
    }
    
    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class configChanged
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (Reference.MOD_ID.equals(event.getModID()))
            {
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
