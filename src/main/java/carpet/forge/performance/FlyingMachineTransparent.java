package carpet.forge.performance;

import carpet.forge.CarpetMain;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FlyingMachineTransparent {

    public static void setFlyingMachineTransparent() {
        if (CarpetMain.config.isTransparent.enabled) {
            Blocks.OBSERVER.setLightOpacity(0);
            Blocks.REDSTONE_BLOCK.setLightOpacity(0);
            Blocks.TNT.setLightOpacity(0);
        } else {
            Blocks.OBSERVER.setLightOpacity(255);
            Blocks.REDSTONE_BLOCK.setLightOpacity(255);
            Blocks.TNT.setLightOpacity(255);
        }

    }

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event){
        FlyingMachineTransparent.setFlyingMachineTransparent();
    }
}
