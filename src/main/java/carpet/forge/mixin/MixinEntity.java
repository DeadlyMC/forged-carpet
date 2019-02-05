package carpet.forge.mixin;

import carpet.forge.helper.BlockRotator;
import carpet.forge.utils.mixininterfaces.IMixinEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {

    @Shadow private int fire;

    @Shadow public float rotationYaw;

    public int getFire(){ return this.fire; }

    public String cm_name() { return "Other Entity"; }

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
