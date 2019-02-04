package carpet.forge.mixin;

import carpet.forge.utils.IMixinEntityLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving extends EntityLivingBase implements IMixinEntityLiving{

    public MixinEntityLiving(World worldIn) {
        super(worldIn);
    }

    @Shadow protected abstract boolean canDespawn();

    @Shadow private boolean persistenceRequired;

    // Added optimized despawn mobs causing netlag by Luflosi
    public boolean willImmediatelyDespawn(){
        if (!this.canDespawn() || this.persistenceRequired) {
            return false;
        }
        boolean playerInDimension = false;
        for (int i = 0; i < this.world.playerEntities.size(); i++) {
            EntityPlayer entityplayer = this.world.playerEntities.get(i);
            if (!entityplayer.isSpectator()) {
                playerInDimension = true;
                double distanceSq = entityplayer.getDistanceSq(this.posX, this.posY, this.posZ);
                if (distanceSq <= 16384.0D) {
                    return false;
                }
            }
        }
        return playerInDimension;
    }
}
