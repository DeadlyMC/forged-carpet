package carpet.forge.mixin;

import carpet.forge.interfaces.IWorldServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin implements IWorldServer
{
    @Shadow @Final private PlayerChunkMap playerChunkMap;
    
    @Override
    public PlayerChunkMap getPlayerChunkMap()
    {
        return this.playerChunkMap;
    }
}
