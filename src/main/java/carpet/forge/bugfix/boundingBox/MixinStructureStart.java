package carpet.forge.bugfix.boundingBox;

import carpet.forge.CarpetMain;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StructureStart.class)
public abstract class MixinStructureStart {

    @Shadow
    protected abstract void updateBoundingBox();

    @Inject(method = "writeStructureComponentsToNBT", at = @At(value = "CONSTANT", args = "stringValue= Children", ordinal = 0))
    public void callBBUpdate(int chunkX, int chunkZ, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (CarpetMain.config.boundingBoxFix.enabled) {
            this.updateBoundingBox();
        }
    }
}
