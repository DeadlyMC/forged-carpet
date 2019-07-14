package carpet.forge.commands;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.TickSpeed;
import carpet.forge.utils.CarpetProfiler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandTick extends CommandCarpetBase
{
    @Override
    public String getName()
    {
        return "tick";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/tick rate <tickrate in tps> | warp <time in ticks to skip>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {

        if (!command_enabled("commandTick", sender)) return;
        if (args.length == 0)
        {
            throw new WrongUsageException(getUsage(sender));
        }
        if ("rate".equalsIgnoreCase(args[0]))
        {
            if (args.length == 2)
            {
                float tickrate = (float)parseDouble(args[1], 0.01D);
                TickSpeed.tickrate(tickrate);
            }
            // CarpetClientMessageHandler.sendTickRateChanges();
            notifyCommandListener(sender, this, String.format("tick rate is %.1f", TickSpeed.tickrate));
            return;
        }
        else if ("warp".equalsIgnoreCase(args[0]))
        {
            long advance = parseLong(args[1], 0, Long.MAX_VALUE);
            EntityPlayer player = null;
            if (sender instanceof EntityPlayer)
            {
                player = (EntityPlayer)sender;
            }

            String s = null;
            ICommandSender icommandsender = null;
            if (args.length > 3)
            {
                s = buildString(args, 2);
                icommandsender = sender;
            }

            String message = TickSpeed.tickrate_advance(player, advance, s, icommandsender);
            if ("".equals(message))
            {
                notifyCommandListener(sender, this, message);
            }
            return;
        }
        else if ("freeze".equalsIgnoreCase(args[0]))
        {
            TickSpeed.is_paused = !TickSpeed.is_paused;
            if (TickSpeed.is_paused)
            {
                notifyCommandListener(sender, this, "Game is paused");
            }
            else
            {
                notifyCommandListener(sender, this, "Game runs normally");
            }
            return;
        }
        else if ("step".equalsIgnoreCase(args[0]))
        {
            int advance = 1;
            if (args.length > 1)
            {
                advance = parseInt(args[1], 1, 72000);
            }
            TickSpeed.add_ticks_to_run_in_pause(advance);
            return;
        }
        else if ("superHot".equalsIgnoreCase(args[0]))
        {
            if (args.length > 1)
            {
                if ("stop".equalsIgnoreCase(args[1]) && !TickSpeed.is_superHot)
                {
                    return;
                }
                if ("start".equalsIgnoreCase(args[1]) && TickSpeed.is_superHot)
                {
                    return;
                }
            }
            TickSpeed.is_superHot = !TickSpeed.is_superHot;
            if (TickSpeed.is_superHot)
            {
                notifyCommandListener(sender, this, "Superhot enabled");
            }
            else
            {
                notifyCommandListener(sender, this, "Superhot disabled");
            }
            return;
        }
        else if ("health".equalsIgnoreCase(args[0]))
        {
            int step = 100;
            if (args.length > 1)
            {
                step = parseInt(args[1], 20, 72000);
            }
            CarpetProfiler.prepare_tick_report(step);
            return;
        }
        else if ("entities".equalsIgnoreCase(args[0]))
        {
            int step = 100;
            if (args.length > 1)
            {
                step = parseInt(args[1], 20, 72000);
            }
            CarpetProfiler.prepare_entity_report(step);
            return;
        }
        throw new WrongUsageException(getUsage(sender), new Object[0]);

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (!CarpetSettings.commandTick)
        {
            return Collections.<String>emptyList();
        }
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "rate","warp", "freeze", "step", "superHot", "health", "entities");
        }
        if (args.length == 2 && "superHot".equalsIgnoreCase(args[0]))
        {
            return getListOfStringsMatchingLastWord(args, "stop","start");
        }
        if (args.length == 2 && "rate".equalsIgnoreCase(args[0]))
        {
            return getListOfStringsMatchingLastWord(args, "20");
        }
        if (args.length == 2 && "warp".equalsIgnoreCase(args[0]))
        {
            return getListOfStringsMatchingLastWord(args, "1000","24000","72000");
        }
        if (args.length == 2 && "health".equalsIgnoreCase(args[0]))
        {
            return getListOfStringsMatchingLastWord(args, "100","200","1000");
        }
        if (args.length == 2 && "entities".equalsIgnoreCase(args[0]))
        {
            return getListOfStringsMatchingLastWord(args, "100","200","1000");
        }
        return Collections.<String>emptyList();
    }
}
