package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StructureStart.class)
public abstract class StructureStartMixin
{
    @Shadow
    protected abstract void updateBoundingBox();

    @Inject(method = "writeStructureComponentsToNBT", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", ordinal = 1))
    private void callBBUpdate(int chunkX, int chunkZ, CallbackInfoReturnable<NBTTagCompound> cir)
    {
        if (CarpetSettings.boundingBoxFix)
            updateBoundingBox();
    }
}
