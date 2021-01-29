package carpet.forge;

import carpet.forge.helper.TickSpeed;
import carpet.forge.logging.LoggerRegistry;
import carpet.forge.utils.HUDController;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

public class CarpetServer
{
    public static MinecraftServer minecraft_server;
    public static final Random rand = new Random((int) ((2 >> 16) * Math.random()));
    
    public static void init(MinecraftServer server) //aka constructor of this static singleton class
    {
        minecraft_server = server;
    }
    
    public static void onServerLoaded(MinecraftServer server)
    {
        CarpetSettings.applySettingsFromConf(server);
        LoggerRegistry.initLoggers(server);
        LoggerRegistry.readSaveFile(server);
    }

    public static void onGameStarted(Side side)
    {
    
    }

    public static void tick(MinecraftServer server)
    {
        TickSpeed.tick(server);
        HUDController.update_hud(server);
    }

    public static void playerConnected(EntityPlayerMP player)
    {
        LoggerRegistry.playerConnected(player);
    }

    public static void playerDisconnected(EntityPlayerMP player)
    {
        LoggerRegistry.playerDisconnected(player);
    }
}
