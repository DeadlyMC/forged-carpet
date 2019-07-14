package carpet.forge.mixin;

import carpet.forge.utils.mixininterfaces.IEntityLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends EntityLivingBase implements IEntityLiving
{
    public EntityLivingMixin(World worldIn)
    {
        super(worldIn);
    }
    
    @Shadow private boolean persistenceRequired;
    
    @Shadow protected abstract boolean canDespawn();
    
    public boolean willImmediatelyDespawn()
    {
        if (!this.canDespawn() || this.persistenceRequired)
        {
            return false;
        }
        boolean playerInDimension = false;
        for (int i = 0; i < this.world.playerEntities.size(); i++)
        {
            EntityPlayer entityplayer = this.world.playerEntities.get(i);
            if (!entityplayer.isSpectator())
            {
                playerInDimension = true;
                double distanceSq = entityplayer.getDistanceSq(this.posX, this.posY, this.posZ);
                if (distanceSq <= 16384.0D)
                {
                    return false;
                }
            }
        }
        return playerInDimension;
    }
}
