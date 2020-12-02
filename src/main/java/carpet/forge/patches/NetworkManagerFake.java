package carpet.forge.patches;

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
}
