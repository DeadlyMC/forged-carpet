package carpet.forge.mixin;

import carpet.forge.helper.BlockRotator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnumFacing.class)
public abstract class MixinEnumFacing {

    @Shadow @Final public static EnumFacing UP;

    @Shadow @Final public static EnumFacing DOWN;

    /**
     * @author DeadlyMC
     * @reason return type
     * Helper for FlippinCactus and RotatorBlock
     */
    @Overwrite
    public static EnumFacing getDirectionFromEntityLiving(BlockPos pos, EntityLivingBase placer)
    {
        if (Math.abs(placer.posX - (double)((float)pos.getX() + 0.5F)) < 2.0D && Math.abs(placer.posZ - (double)((float)pos.getZ() + 0.5F)) < 2.0D)
        {
            double d0 = placer.posY + (double)placer.getEyeHeight();

            if (d0 - (double)pos.getY() > 2.0D)
            {
                // [FCM] Start
                if (BlockRotator.flippinEligibility(placer))
                    return DOWN;
                // [FCM] End
                return UP;
            }

            if ((double)pos.getY() - d0 > 0.0D)
            {
                // [FCM] Start
                if (BlockRotator.flippinEligibility(placer))
                    return UP;
                // [FCM] End
                return DOWN;
            }
        }

        return placer.getHorizontalFacing().getOpposite();
    }
}
