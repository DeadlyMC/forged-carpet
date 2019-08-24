package carpet.forge;

import carpet.forge.commands.*;
import carpet.forge.helper.TickSpeed;
import carpet.forge.logging.LoggerRegistry;
import carpet.forge.utils.HUDController;
import carpet.forge.utils.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.MOD_VERSION,
        acceptedMinecraftVersions = Reference.ACCEPTED_MC_VERSIONS,
        certificateFingerprint = Reference.FINGERPRINT,
        updateJSON = Reference.UPDATE_URL
)
public class CarpetServer
{
    public static Logger logger;
    public static MinecraftServer minecraft_server;

    public CarpetServer() { }

    @Mod.Instance(Reference.MOD_ID)
    public static CarpetServer instance;

    public static void onGameStarted() {
        LoggerRegistry.initLoggers();
    }

    public static void onServerLoaded(MinecraftServer server) {
        CarpetServer.minecraft_server = server;
        CarpetSettings.applySettingsFromConf(server);
    }

    public static void tick(MinecraftServer server) {
        TickSpeed.tick(server);
        HUDController.update_hud(server);
        CarpetSettings.impendingFillSkipUpdates = false;
    }

    public static void playerConnected(EntityPlayerMP player) {
        LoggerRegistry.playerConnected(player);
    }

    public static void playerDisconnected(EntityPlayerMP player) {
        LoggerRegistry.playerDisconnected(player);
    }

    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandCarpet());
        event.registerServerCommand(new CommandGMC());
        event.registerServerCommand(new CommandGMS());
        event.registerServerCommand(new CommandPing());
        event.registerServerCommand(new CommandBlockInfo());
        event.registerServerCommand(new CommandSpawn());
        event.registerServerCommand(new CommandEntityInfo());
        event.registerServerCommand(new CommandAutoSave());
        event.registerServerCommand(new CommandCounter());
        event.registerServerCommand(new CommandFillBiome());
        event.registerServerCommand(new CommandLog());
        event.registerServerCommand(new CommandPerimeter());
        event.registerServerCommand(new CommandTick());
        event.registerServerCommand(new CommandTickHealth());
        event.registerServerCommand(new CommandDistance());
        event.registerServerCommand(new CommandUnload());
        event.registerServerCommand(new CommandUnload13());
    }

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        if (!event.isDirectory()) {
            logger.warn("*******************************************************************************************************");
            logger.warn("                                         WARNING!                                                      ");
            logger.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with.");
            logger.warn("                     This version will NOT be supported by the author!                                 ");
            logger.warn("*******************************************************************************************************");
        }
    }

}
