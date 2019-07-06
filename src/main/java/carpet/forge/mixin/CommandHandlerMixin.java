package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandHandler.class)
public abstract class CommandHandlerMixin
{
    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void onExecuteBegin(ICommandSender sender, String rawCommand, CallbackInfoReturnable<Integer> cir)
    {
        if (!CarpetSettings.fillUpdates)
            CarpetSettings.impendingFillSkipUpdates = true;
    }
    
    @Inject(method = "executeCommand", at = @At("TAIL"))
    private void onExecuteCommandEnd(ICommandSender sender, String rawCommand, CallbackInfoReturnable<Integer> cir)
    {
        CarpetSettings.impendingFillSkipUpdates = false;
    }
}
