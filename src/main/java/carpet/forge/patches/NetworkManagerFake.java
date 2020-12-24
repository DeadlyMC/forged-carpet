package carpet.forge.patches;

import io.netty.channel.Channel;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;

public class NetworkManagerFake extends NetworkManager
{
    public NetworkManagerFake(EnumPacketDirection p)
    {
        super(p);
    }

    @Override
    public void disableAutoRead()
    {
    }
    
    @Override
    public void handleDisconnection()
    {
    }
    
    @Override
    public Channel channel()
    {
        return new ChannelFake();
    }
}
