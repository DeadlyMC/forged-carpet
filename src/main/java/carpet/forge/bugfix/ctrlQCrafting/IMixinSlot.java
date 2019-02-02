package carpet.forge.bugfix.ctrlQCrafting;

import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Slot.class)
public interface IMixinSlot {

    @Invoker
    void invokeOnSwapCraft(int p_190900_1_);
}
