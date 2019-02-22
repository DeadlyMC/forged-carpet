package carpet.forge;

import carpet.forge.commands.*;
import carpet.forge.helper.TickSpeed;
import carpet.forge.logging.LoggerRegistry;
import carpet.forge.proxy.CommonProxy;
import carpet.forge.utils.HUDController;
import carpet.forge.utils.Reference;
import carpet.forge.utils.TickingArea;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.VERSION,
        acceptedMinecraftVersions = Reference.ACCEPTED_MC_VERSIONS,
        certificateFingerprint = Reference.FINGERPRINT
)
public class CarpetMain {

    public static Logger logger;
    public static MinecraftServer minecraft_server;

    public CarpetMain() {
    }

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.Instance(Reference.MOD_ID)
    public static CarpetMain instance;

    public static void init(MinecraftServer server) {
        CarpetMain.minecraft_server = server;
    }

    public static void onGameStarted() {
        LoggerRegistry.initLoggers();
    }

    public static void onLoadAllWorlds(MinecraftServer server)
    {
        TickingArea.loadConfig(server);
    }
    public static void onWorldsSaved(MinecraftServer server)
    {
        TickingArea.saveConfig(server);
    }

    public static void onServerLoaded(MinecraftServer server)
    {
        CarpetSettings.apply_settings_from_conf(server);
    }

    public static void tick(MinecraftServer server) {
        TickSpeed.tick(server);
        HUDController.update_hud(server);
    }

    public static void playerConnected(EntityPlayerMP player) {
        LoggerRegistry.playerConnected(player);
    }

    public static void playerDisconnected(EntityPlayerMP player) {
        LoggerRegistry.playerDisconnected(player);
    }


    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {

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
        event.registerServerCommand(new CommandRNG());
        event.registerServerCommand(new CommandTickingArea());
        event.registerServerCommand(new CommandStructure());
        event.registerServerCommand(new CommandCarpetFill());
        event.registerServerCommand(new CommandCarpetClone());

        CarpetSettings.reload_all_statics();
    }

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {

        if (!event.isDirectory()) {
            logger.warn("*******************************************************************************************************");
            logger.warn("                                         WARNING!                                                      ");
            logger.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with.");
            logger.warn("                     This version will NOT be supported by the author!                                 ");
            logger.warn("*******************************************************************************************************");
        }
    }

}
