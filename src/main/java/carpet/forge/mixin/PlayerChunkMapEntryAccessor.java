package carpet.forge.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerChunkMapEntry.class)
public interface PlayerChunkMapEntryAccessor
{
    @Accessor("players")
    List<EntityPlayerMP> getPlayers();
}
