package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin
{
    @Shadow
    public World world;
    
    @Shadow
    public EntityPlayerMP player;
    
    @Shadow
    private BlockPos destroyPos = BlockPos.ORIGIN;
    
    @Inject(
            method = "onBlockClicked",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;sendBlockBreakProgress(ILnet/minecraft/util/math/BlockPos;I)V",
            shift = At.Shift.BEFORE)
    )
    private void notifyUpdate(BlockPos pos, EnumFacing side, CallbackInfo ci)
    {
        if (CarpetSettings.miningGhostBlocksFix)
        {
            player.connection.sendPacket(new SPacketBlockChange(world, destroyPos));
        }
    }
}
