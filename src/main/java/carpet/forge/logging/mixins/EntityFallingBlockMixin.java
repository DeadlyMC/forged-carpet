package carpet.forge.logging.mixins;

import carpet.forge.logging.LoggerRegistry;
import carpet.forge.logging.logHelpers.TrajectoryLogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFallingBlock.class)
public abstract class EntityFallingBlockMixin extends Entity
{
    private TrajectoryLogHelper logHelper = null;

    public EntityFallingBlockMixin(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void setDead()
    {
        if (LoggerRegistry.__fallingBlocks && logHelper != null)
            logHelper.onFinish();
        super.setDead();
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/block/state/IBlockState;)V", at = @At(value = "RETURN"))
    private void onEntityFallingBlock(World worldIn, double x, double y, double z, IBlockState fallingBlockState, CallbackInfo ci)
    {
        if (LoggerRegistry.__fallingBlocks)
            logHelper = new TrajectoryLogHelper("fallingBlocks");
    }

    public String cm_name()
    {
        return "Falling Block";
    }

    @Inject(method = "onUpdate", at = @At(value = "HEAD"))
    private void onOnUpdate(CallbackInfo ci)
    {
        if (LoggerRegistry.__fallingBlocks && logHelper != null)
            logHelper.onTick(posX, posY, posZ, motionX, motionY, motionZ);
    }

}
