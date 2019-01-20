package carpet.forge.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public abstract class CarpetCommandBase extends CommandBase
{
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public void msg(ICommandSender sender, List<ITextComponent> texts) { msg(sender, texts.toArray(new ITextComponent[0])); }
    public void msg(ICommandSender sender, ITextComponent ... texts)
    {
        if (sender instanceof EntityPlayer)
        {
            for (ITextComponent t: texts) sender.sendMessage(t);
        }
        else
        {
            for (ITextComponent t: texts) notifyCommandListener(sender, this, t.getUnformattedText());
        }
    }


}

