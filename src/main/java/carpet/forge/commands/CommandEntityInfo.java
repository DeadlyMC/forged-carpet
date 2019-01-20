package carpet.forge.commands;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.annotation.Nullable;

import carpet.forge.CarpetMain;
import carpet.forge.utils.EntityInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CommandEntityInfo extends CarpetCommandBase {

    @Override
    public String getName()
    {
        return "entityinfo";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "Usage: entityinfo <entity_selector>";
    }

    public void print_multi_message(List<String> messages, ICommandSender sender, String grep)
    {
        List<String> actual = new ArrayList<String>();
        if (grep != null)
        {
            Pattern p = Pattern.compile(grep);
            actual.add(messages.get(0));
            boolean empty = true;
            for (int i = 1; i<messages.size(); i++)
            {
                String line = messages.get(i);
                Matcher m = p.matcher(line);
                if (m.find())
                {
                    empty = false;
                    actual.add(line);
                }
            }
            if (empty)
            {
                return;
            }
        }
        else
        {
            actual = messages;
        }
        notifyCommandListener(sender, this, "");
        for (String lline: actual)
        {
            notifyCommandListener(sender, this, lline);
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {

        if(CarpetMain.config.commandEntityInfo.enabled) {
            if (args.length == 0 || "grep".equalsIgnoreCase(args[0])) {
                String grep = null;
                if (args.length == 2) {
                    grep = args[1];
                }
                EntityPlayer entityplayer = getCommandSenderAsPlayer(sender);
                List<String> report = EntityInfo.entityInfo(entityplayer, sender.getEntityWorld());
                print_multi_message(report, sender, grep);
            } else {
                Entity entity = getEntity(server, sender, args[0]);
                //LOG.error("SENDER dimension "+ sender.getEntityWorld().provider.getDimensionType().getId());
                List<String> report = EntityInfo.entityInfo(entity, sender.getEntityWorld());
                String grep = null;
                if (args.length >= 3 && "grep".equalsIgnoreCase(args[1])) {
                    grep = args[2];
                }
                print_multi_message(report, sender, grep);
            }
        } else {
            notifyCommandListener(sender, this, "CommandEntityInfo is disabled");
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (!CarpetMain.config.commandEntityInfo.enabled)
        {
            notifyCommandListener(sender, this, "Command is disabled in ForgeCarpet mod options");
        }
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
    }
}


