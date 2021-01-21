package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.command.CommandClone;
import net.minecraft.command.CommandFill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = {CommandClone.class, CommandFill.class})
public abstract class CommandFillCloneMixin
{
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 32768))
    private int fillLimit(int limit)
    {
        return CarpetSettings.fillLimit;
    }
    
}
