package carpet.forge.mixin;

import carpet.forge.interfaces.IPlayerChunkMapEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerChunkMapEntry.class)
public abstract class PlayerChunkMapEntryMixin implements IPlayerChunkMapEntry
{
    @Shadow @Final private List<EntityPlayerMP> players;
    
    @Override
    public List<EntityPlayerMP> getPlayers()
    {
        return this.players;
    }
}
