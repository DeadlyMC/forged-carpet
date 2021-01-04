package carpet.forge.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketHandshake implements IMessage
{
    private String carpetVersion;
    
    public CPacketHandshake()
    {
    
    }
    
    public CPacketHandshake(String version)
    {
        this.carpetVersion = version;
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.carpetVersion = ByteBufUtils.readUTF8String(buf);
    }
    
    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.carpetVersion);
    }
    
    public String getCarpetVersion()
    {
        return carpetVersion;
    }
    
    public static class Handler implements IMessageHandler<CPacketHandshake, SPacketCarpetRule>
    {
        @Override
        public SPacketCarpetRule onMessage(CPacketHandshake message, MessageContext ctx)
        {
            NetHandlerPlayServer handler = ctx.getServerHandler();
            WorldServer world = handler.player.getServerWorld();
            world.addScheduledTask(() -> CarpetPacketHandler.onHello(message, handler.player));
            return CarpetPacketHandler.sendCarpetData(handler.player);
        }
    }
}
