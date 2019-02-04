package carpet.forge.commands;

import carpet.forge.CarpetMain;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandPing extends CarpetCommandBase {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: Get your Ping";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (!command_enabled("commandPing", sender))
            return;

        if (sender instanceof EntityPlayerMP)
        {
            int ping = ((EntityPlayerMP) sender).ping;
            sender.sendMessage(new TextComponentString("Your ping is: " + ping + " ms"));
        }
        else
        {
            throw new CommandException("Only a player can have a ping!");
        }
    }

}
