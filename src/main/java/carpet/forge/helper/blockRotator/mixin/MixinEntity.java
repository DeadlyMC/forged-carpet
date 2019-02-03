package carpet.forge.helper.blockRotator.mixin;

import carpet.forge.helper.blockRotator.BlockRotator;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public float rotationYaw;

    /**
     * @author DeadlyMC
     * @reason return type
     */
    @Overwrite
    public EnumFacing getHorizontalFacing()
    {
        // [FCM] Start
        if (BlockRotator.flippinEligibility((Entity)(Object)this))
            return EnumFacing.byHorizontalIndex(MathHelper.floor((double)(this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
        // [FCM] End
        return EnumFacing.byHorizontalIndex(MathHelper.floor((double)(this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
    }
}
