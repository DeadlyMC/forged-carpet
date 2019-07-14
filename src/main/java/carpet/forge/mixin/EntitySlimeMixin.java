package carpet.forge.mixin;

import carpet.forge.utils.mixininterfaces.IEntitySlime;
import net.minecraft.entity.monster.EntitySlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin implements IEntitySlime
{
    @Shadow
    protected abstract int getAttackStrength();

    @Override
    public float getPublicAttackStrength()
    {
        return (float) this.getAttackStrength();
    }
}
