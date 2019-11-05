package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockFarmland;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(BlockFarmland.class)
public abstract class BlockFarmlandMixin
{
    @Redirect(method = "turnToDirt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setPositionAndUpdate(DDD)V"))
    private static void cancelSetPosAndUpdate(Entity entity, double x, double y, double z)
    {
    
    }
    
    @Inject(
            method = "turnToDirt",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/entity/Entity;setPositionAndUpdate(DDD)V"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void onTurnToDirt(World world, BlockPos worldIn, CallbackInfo ci, AxisAlignedBB axisalignedbb, Iterator var3, Entity entity, double d0, Entity var7, double var8, double var10, double var12)
    {
        if (!CarpetSettings.farmlandBug)
        {
            entity.setPositionAndUpdate(entity.posX, entity.posY + d0 + 0.001D, entity.posZ);
        }
        else
        {
            entity.setPositionAndUpdate(entity.posX, axisalignedbb.maxY, entity.posZ);
        }
    }
}
