package carpet.forge;

import carpet.forge.commands.*;
import carpet.forge.network.CarpetPacketHandler;
import carpet.forge.utils.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.MOD_VERSION,
        acceptedMinecraftVersions = Reference.ACCEPTED_MC_VERSIONS,
        certificateFingerprint = Reference.FINGERPRINT,
        updateJSON = Reference.UPDATE_URL,
        acceptableRemoteVersions = "*"
)
public class ForgedCarpet
{
    public static Logger logger = LogManager.getLogger();
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CarpetPacketHandler.registerMessagesAndEvents();
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
        event.registerServerCommand(new CommandPlayer());
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
