package carpet.forge.network;

import carpet.forge.CarpetSettings;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketHandshake implements IMessage
{
    private String carpetVersion;
    
    public SPacketHandshake()
    {
    
    }
    
    public SPacketHandshake(String version)
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
        return this.carpetVersion;
    }
    
    public static class Handler implements IMessageHandler<SPacketHandshake, CPacketHandshake>
    {
        @Override
        public CPacketHandshake onMessage(SPacketHandshake message, MessageContext ctx)
        {
            Minecraft client = Minecraft.getMinecraft();
            client.addScheduledTask(() -> CarpetPacketHandler.onHi(message));
            return new CPacketHandshake(CarpetSettings.carpetVersion);
        }
    }
}
