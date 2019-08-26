package carpet.forge;

import carpet.forge.helper.TickSpeed;
import carpet.forge.logging.LoggerRegistry;
import carpet.forge.utils.HUDController;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CarpetServer
{
    public static MinecraftServer minecraft_server;

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
}
