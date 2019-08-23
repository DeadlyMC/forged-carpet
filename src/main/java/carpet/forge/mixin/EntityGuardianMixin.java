package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityGuardian.class)
public abstract class EntityGuardianMixin extends EntityMob
{
    public EntityGuardianMixin(World worldIn)
    {
        super(worldIn);
    }
    
    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt)
    {
        if (!this.world.isRemote && !this.isDead && CarpetSettings.renewableSponges)
        {
            EntityElderGuardian entityguardian = new EntityElderGuardian(this.world);
            entityguardian.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            entityguardian.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityguardian)), (IEntityLivingData)null);
            entityguardian.setNoAI(this.isAIDisabled());
            
            if (this.hasCustomName())
            {
                entityguardian.setCustomNameTag(this.getCustomNameTag());
                entityguardian.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
            }
            
            this.world.spawnEntity(entityguardian);
            this.setDead();
        }
        else
        {
            super.onStruckByLightning(lightningBolt);
        }
    }
}
