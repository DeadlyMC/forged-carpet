package carpet.forge.tweak;

import carpet.forge.CarpetMain;
import net.minecraft.block.BlockObserver;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ObserversDoNonUpdate extends BlockObserver {

    @SubscribeEvent
    public void observerPlaced(BlockEvent.PlaceEvent event) {
        if (CarpetMain.config.observertweak.enabled) {
            if (event.getPlacedBlock().getBlock() == Blocks.OBSERVER &&
                    event.getPlacedBlock().getValue(BlockObserver.POWERED) == false) {
                event.getWorld().setBlockState(event.getPos(), event.getPlacedBlock().withProperty(BlockObserver.POWERED, true));
            }
        } else {
            if (event.getPlacedBlock().getBlock() == Blocks.OBSERVER &&
                    event.getPlacedBlock().getValue(BlockObserver.POWERED) == true) {
                event.getWorld().setBlockState(event.getPos(), event.getPlacedBlock().withProperty(BlockObserver.POWERED, false));
            }

        }
    }
}
