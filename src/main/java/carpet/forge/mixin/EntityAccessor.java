package carpet.forge.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor
{
    @Accessor("fire")
    int getFire();
    
    @Invoker
    void invokeSetRotation(float yaw, float pitch);
}
