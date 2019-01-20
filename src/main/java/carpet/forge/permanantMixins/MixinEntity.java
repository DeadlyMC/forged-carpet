package carpet.forge.permanantMixins;

import carpet.forge.utils.IMixinEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {

    @Shadow private int fire;

    public int getFire(){ return this.fire; }

    public String cm_name() { return "Other Entity"; }

}
