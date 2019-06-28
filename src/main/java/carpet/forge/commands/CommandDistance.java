package carpet.forge.commands;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.DistanceCalculator;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandDistance extends CarpetCommandBase {
    @Override
    public String getName() {
        return "distance";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/distance <X1> <Y1> <Z1> <X2> <Y2> <Z2>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (!command_enabled("commandDistance", sender)) return;
        if (args.length != 6)
        {
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        BlockPos blockpos = parseBlockPos(sender, args, 0, false);
        BlockPos blockpos2 = parseBlockPos(sender, args, 3, false);
        msg(sender, DistanceCalculator.print_distance_two_points(blockpos, blockpos2));

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (!CarpetSettings.commandDistance)
        {
            return Collections.<String>emptyList();
        }
        if (args.length > 0 && args.length <= 3)
        {
            return getTabCompletionCoordinate(args, 0, targetPos);
        }
        if (args.length > 3 && args.length <= 6)
        {
            return getTabCompletionCoordinate(args, 3, targetPos);
        }
        return Collections.<String>emptyList();
    }
}
