package carpet.forge.network;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.TickSpeed;
import carpet.forge.patches.EntityPlayerMPFake;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CarpetPacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("forgedcarpet");
    
    private static Map<EntityPlayerMP, String> remoteCarpetPlayers = new HashMap<>();
    private static Set<EntityPlayerMP> validCarpetPlayers = new HashSet<>();
    
    /**
     * Send handshake (hi) packet when a player joins.
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        SPacketHandshake packet = new SPacketHandshake(CarpetSettings.carpetVersion);
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (!(player instanceof EntityPlayerMPFake || player instanceof FakePlayer))
            CarpetPacketHandler.INSTANCE.sendTo(packet, player);
    }
    
    /**
     * Called when the client receives handshake (hi) packet.
     * Stores the server carpet version.
     */
    public static void onHi(SPacketHandshake message)
    {
        CarpetClient.setCarpet();
        CarpetClient.serverCarpetVersion = message.getCarpetVersion();
        if (CarpetSettings.carpetVersion.equals(CarpetClient.serverCarpetVersion))
        {
            CarpetSettings.LOG.info("Joined carpet server with matching carpet version");
        }
        else
        {
            CarpetSettings.LOG.warn("Joined carpet server with another carpet version: " + CarpetClient.serverCarpetVersion);
        }
    }
    
    /**
     * Called when the server receives the response (hello) packet from client.
     * Stores the registered players and client mod versions.
     */
    public static void onHello(CPacketHandshake message, EntityPlayerMP player)
    {
        validCarpetPlayers.add(player);
        String clientVersion = message.getCarpetVersion();
        remoteCarpetPlayers.put(player, clientVersion);
        if (clientVersion.equals(CarpetSettings.carpetVersion))
            CarpetSettings.LOG.info("Player " + player.getName() + " joined with a matching carpet client");
        else
            CarpetSettings.LOG.warn("Player " + player.getName() + " joined with another carpet version: " + clientVersion);
    }
    
    /**
     * Send server carpet rules and current server tickrate to client.
     * Called when the client 'hello' packet is received.
     */
    public static SPacketCarpetRule sendCarpetData(EntityPlayerMP player)
    {
        SPacketCarpetRule message = new SPacketCarpetRule();
        for (String rule : CarpetSettings.findAll(null))
        {
            String value = CarpetSettings.get(rule);
            message.addRule(rule, value);
            CarpetSettings.LOG.debug("Adding \"" + rule + "\" to server settings sync packet with value: " + value);
        }
        CarpetPacketHandler.INSTANCE.sendTo(new SPacketTickRate(TickSpeed.tickrate), player);
        return message;
    }
    
    /**
     * Called on the client when it receives the server carpet rule packet.
     * Syncs the client and server carpet rules.
     */
    public static void syncCarpetRules(SPacketCarpetRule message)
    {
        for (Map.Entry<String, String> rules : message.getCarpetRules().entrySet())
        {
            String name = rules.getKey();
            String value = rules.getValue();
            CarpetSettings.set(name, value, false);
        }
    }
    
    /**
     * Send carpet rule update to client.
     * Called when a rule is changed on the server.
     */
    public static void updateRuleWithConnectedClients(String rule, String value)
    {
        SPacketCarpetRule message = new SPacketCarpetRule();
        message.addRule(rule, value);
        for (EntityPlayerMP player : remoteCarpetPlayers.keySet())
        {
            CarpetPacketHandler.INSTANCE.sendTo(message, player);
        }
    }
    
    /**
     * Send tickrate update to client.
     * Called when the tickrate is changed on the server.
     */
    public static void updateTickSpeedToConnectedPlayers()
    {
        SPacketTickRate message = new SPacketTickRate(TickSpeed.tickrate);
        for (EntityPlayerMP player : remoteCarpetPlayers.keySet())
        {
            CarpetPacketHandler.INSTANCE.sendTo(message, player);
        }
    }
    
    public static void registerMessagesAndEvents()
    {
        INSTANCE.registerMessage(SPacketHandshake.Handler.class, SPacketHandshake.class, CarpetClient.HI, Side.CLIENT);
        INSTANCE.registerMessage(CPacketHandshake.Handler.class, CPacketHandshake.class, CarpetClient.HELLO, Side.SERVER);
        INSTANCE.registerMessage(SPacketCarpetRule.Handler.class, SPacketCarpetRule.class, CarpetClient.RULES, Side.CLIENT);
        INSTANCE.registerMessage(SPacketTickRate.Handler.class, SPacketTickRate.class, CarpetClient.TICK_RATE, Side.CLIENT);
    
        MinecraftForge.EVENT_BUS.register(CarpetPacketHandler.class);
    }
}
