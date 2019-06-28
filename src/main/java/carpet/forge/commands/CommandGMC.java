package carpet.forge.commands;

import carpet.forge.CarpetMain;
import carpet.forge.CarpetSettings;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandGMC extends CarpetCommandBase {
    @Override
    public String getName() {
        return "c";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: Change to spectator mode";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (!command_enabled("commandCameramode", sender)) return;
        if (args.length > 0)
        {
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else
        {
            if (!CarpetSettings.commandCameramode)
            {
                notifyCommandListener(sender, this, "Quick gamemode switching is disabled");
            }
            GameType gametype = GameType.parseGameTypeWithDefault("spectator", GameType.NOT_SET);
            EntityPlayer entityplayer = getCommandSenderAsPlayer(sender);
            entityplayer.setGameType(gametype);
            PotionEffect potioneffect = new PotionEffect(Potion.getPotionFromResourceLocation("night_vision"), 999999, 0, false, false);
            entityplayer.addPotionEffect(potioneffect);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

}
