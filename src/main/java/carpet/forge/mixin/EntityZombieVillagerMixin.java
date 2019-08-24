package carpet.forge.mixin;

import carpet.forge.interfaces.IEntityZombieVillager;
import net.minecraft.entity.monster.EntityZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityZombieVillager.class)
public abstract class EntityZombieVillagerMixin implements IEntityZombieVillager
{
    @Shadow private int conversionTime;
    
    public int getConversionTime()
    {
        return this.conversionTime;
    }
}
