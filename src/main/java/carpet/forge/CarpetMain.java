package carpet.forge;

import carpet.forge.commands.*;
import carpet.forge.config.CarpetConfig;
import carpet.forge.helper.FlippinCactus;
import carpet.forge.performance.FlyingMachineTransparent;
import carpet.forge.proxy.CommonProxy;
import carpet.forge.tweak.ObserversDoNonUpdate;
import carpet.forge.utils.Reference;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.VERSION,
        acceptedMinecraftVersions = Reference.ACCEPTED_MC_VERSIONS,
        guiFactory = Reference.GUI_FACTORY,
        clientSideOnly = true
)
public class CarpetMain {

    public static Logger logger;
    public final static CarpetConfig config = new CarpetConfig();
    public static MinecraftServer minecraft_server;

    public CarpetMain() {

    }

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.Instance(Reference.MOD_ID)
    public static CarpetMain instance;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new CarpetConfig());
        MinecraftForge.EVENT_BUS.register(new ObserversDoNonUpdate());
        MinecraftForge.EVENT_BUS.register(new FlyingMachineTransparent());
        MinecraftForge.EVENT_BUS.register(new FlippinCactus());
        FlyingMachineTransparent.setFlyingMachineTransparent();

        config.init(new File(Launch.minecraftHome, "config/fcarpet1122.cfg"));
        config.load();


    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void PostInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        if (config.commandCameraMode.loaded) {
            event.registerServerCommand(new CommandGMC());
            event.registerServerCommand(new CommandGMS());
        }
        if (config.commandPing.loaded)        event.registerServerCommand(new CommandPing());
        if (config.commandBlockInfo.loaded)   event.registerServerCommand(new CommandBlockInfo());
        if (config.commandSpawn.loaded)       event.registerServerCommand(new CommandSpawn());
        if (config.commandEntityInfo.loaded)  event.registerServerCommand(new CommandEntityInfo());
        if (config.commandAutoSave.loaded)    event.registerServerCommand(new CommandAutoSave());
        if (config.commandCounter.loaded)     event.registerServerCommand(new CommandCounter());
        if (config.commandFillBiome.loaded)   event.registerServerCommand(new CommandFillBiome());
    }

}
