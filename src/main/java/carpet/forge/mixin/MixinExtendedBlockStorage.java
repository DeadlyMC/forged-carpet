package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExtendedBlockStorage.class)
public abstract class MixinExtendedBlockStorage {

    @Shadow
    private int blockRefCount;

    /**
     * @author DeadlyMC
     * @reason NewLight
     */
    @Overwrite
    public boolean isEmpty() {
        if (CarpetSettings.newLight) {
            return false;
        } else {
            return this.blockRefCount == 0;
        }
    }

}
