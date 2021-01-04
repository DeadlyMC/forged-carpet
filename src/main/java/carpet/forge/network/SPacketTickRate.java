package carpet.forge.network;

import carpet.forge.ForgedCarpet;
import carpet.forge.helper.TickSpeed;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketTickRate implements IMessage
{
    private String tickrate;
    
    public SPacketTickRate()
    {
    
    }
    
    public SPacketTickRate(float tickrate)
    {
        this.tickrate = String.valueOf(tickrate);
        ForgedCarpet.logger.debug("Adding tickrate to server-client sync packet with value: " + tickrate);
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.tickrate = ByteBufUtils.readUTF8String(buf);
    }
    
    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.tickrate);
    }
    
    public float getTickRate()
    {
        return Float.parseFloat(this.tickrate);
    }
    
    public static class Handler implements IMessageHandler<SPacketTickRate, IMessage>
    {
        @Override
        public IMessage onMessage(SPacketTickRate message, MessageContext ctx)
        {
            Minecraft client = Minecraft.getMinecraft();
            client.addScheduledTask(() -> TickSpeed.tickrate(message.getTickRate(), false));
            return null;
        }
    }
}
