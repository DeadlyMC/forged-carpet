package carpet.forge.commands;

import carpet.forge.CarpetMain;
import carpet.forge.CarpetSettings;
import carpet.forge.utils.BlockInfo;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandBlockInfo extends CarpetCommandBase {
    @Override
    public String getName() {
        return "blockinfo";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/blockinfo <X> <Y> <Z>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (!command_enabled("commandBlockInfo", sender)) return;

        if (args.length != 3)
        {
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        BlockPos blockpos = parseBlockPos(sender, args, 0, false);
        World world = sender.getEntityWorld();
        msg(sender, BlockInfo.blockInfo(blockpos, world));

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

        if (!CarpetSettings.getBool("commandBlockInfo"))
        {
            return Collections.<String>emptyList();
        }
        if (args.length > 0 && args.length <= 3)
        {
            return getTabCompletionCoordinate(args, 0, targetPos);
        }
        return Collections.<String>emptyList();
    }
}
