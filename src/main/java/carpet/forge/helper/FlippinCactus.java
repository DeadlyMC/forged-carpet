package carpet.forge.helper;

import carpet.forge.utils.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class FlippinCactus
{
    // Disabled cause of issues with hitVec being null
    @SubscribeEvent
    public static void flip(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(event.getPos());
        EntityPlayer player = event.getEntityPlayer();
        EnumHand hand = event.getHand();
        EnumFacing facing = event.getFace();
        Vec3d hitVec = event.getHitVec();
        float hitX = (float) hitVec.x;
        float hitY = (float) hitVec.y;
        float hitZ = (float) hitVec.z;
        
        Boolean flipped = BlockRotator.flipBlockWithCactus(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
        if (flipped)
        {
            event.setResult(Event.Result.ALLOW);
        }
    }
}
