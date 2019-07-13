package carpet.forge.commands;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.HopperCounter;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommandCounter extends CommandCarpetBase
{
    @Override
    public String getName()
    {
        return "counter";
    }
    
    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/counter <color> <reset/realtime>";
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (!command_enabled("hopperCounters", sender))
            return;
        if (args.length == 0)
        {
            msg(sender, HopperCounter.formatAll(server, false));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT))
        {
            case "realtime":
                msg(sender, HopperCounter.formatAll(server, true));
                return;
            case "reset":
                HopperCounter.resetAll(server);
                notifyCommandListener(sender, this, "All counters restarted.");
                return;
        }
        HopperCounter counter = HopperCounter.getCounter(args[0]);
        if (counter == null)
            throw new WrongUsageException("Invalid color");
        if (args.length == 1)
        {
            msg(sender, counter.format(server, false, false));
            return;
        }
        switch (args[1].toLowerCase(Locale.ROOT))
        {
            case "realtime":
                msg(sender, counter.format(server, true, false));
                return;
            case "reset":
                counter.reset(server);
                notifyCommandListener(sender, this, String.format("%s counters restarted.", args[0]));
                return;
        }
        throw new WrongUsageException(getUsage(sender));
        
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if (!CarpetSettings.hopperCounters)
        {
            return Collections.<String>emptyList();
        }
        if (args.length == 1)
        {
            List<String> lst = new ArrayList<String>();
            lst.add("reset");
            for (EnumDyeColor clr : EnumDyeColor.values())
            {
                lst.add(clr.name().toLowerCase(Locale.ROOT));
            }
            lst.add("realtime");
            String[] stockArr = new String[lst.size()];
            stockArr = lst.toArray(stockArr);
            return getListOfStringsMatchingLastWord(args, stockArr);
        }
        if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, "reset", "realtime");
        }
        return Collections.<String>emptyList();
    }
}
