package carpet.forge.permanantMixins;

import net.minecraft.entity.passive.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityVillager.class)
public interface IMixinEntityVillager {

    @Accessor
    int getWealth();
}
