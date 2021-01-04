package carpet.forge.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class SPacketCarpetRule implements IMessage
{
    private final HashMap<String, String> settings = new HashMap<>();
    
    public SPacketCarpetRule()
    {
    
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        int i = buf.readInt();
        this.settings.clear();
    
        for (int j = 0; j < i; ++j)
        {
            String rule = ByteBufUtils.readUTF8String(buf);
            String value = ByteBufUtils.readUTF8String(buf);
            this.settings.put(rule, value);
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.settings.size());
        for (Map.Entry<String, String> entry : this.settings.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            ByteBufUtils.writeUTF8String(buf, entry.getValue());
        }
    }
    
    public void addRule(String rule, String value)
    {
        this.settings.put(rule, value);
    }
    
    public HashMap<String, String> getCarpetRules()
    {
        return this.settings;
    }
    
    public static class Handler implements IMessageHandler<SPacketCarpetRule, IMessage>
    {
        @Override
        public IMessage onMessage(SPacketCarpetRule message, MessageContext ctx)
        {
            Minecraft client = Minecraft.getMinecraft();
            client.addScheduledTask(() -> CarpetPacketHandler.syncCarpetRules(message));
            return null;
        }
    }
}
