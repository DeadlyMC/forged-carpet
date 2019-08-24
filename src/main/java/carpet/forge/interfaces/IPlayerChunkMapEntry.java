package carpet.forge.interfaces;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public interface IPlayerChunkMapEntry
{
    List<EntityPlayerMP> getPlayers();
}
