package carpet.forge.commands;

import carpet.forge.CarpetMain;
import carpet.forge.CarpetSettings;
import carpet.forge.utils.CarpetProfiler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandTickHealth extends CarpetCommandBase {

    @Override
    public String getName() {
        return "tickhealth";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tickheath <entities>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (!command_enabled("commandTick", sender)) return;
        if (args.length > 0 && "entities".equalsIgnoreCase(args[0]))
        {
            CarpetProfiler.prepare_entity_report(100);
        }
        else
        {
            CarpetProfiler.prepare_tick_report(100);
        }

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

        if (!CarpetSettings.commandTick)
        {
            return Collections.<String>emptyList();
        }
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "entities");
        }
        return Collections.<String>emptyList();
    }
}
