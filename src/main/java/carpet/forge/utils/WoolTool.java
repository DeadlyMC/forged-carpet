package carpet.forge.utils;

import net.minecraft.block.BlockColored;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WoolTool {

    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.<EnumDyeColor>create("color", EnumDyeColor.class);

    /*
    public static void carpetPlacedAction(EnumDyeColor color, EntityPlayer placer, BlockPos pos, World worldIn) {
        if (!CarpetServer.config.carpets.enabled) {
            return;
        }
        switch (color)
        {
            case PINK:
                if (CarpetServer.config.commandSpawn.enabled)
                    Messenger.send(placer, SpawnReporter.report(pos, worldIn));

                break;
            case BLACK:
                if (CarpetServer.config.commandSpawn.enabled)
                    Messenger.send(placer, SpawnReporter.show_mobcaps(pos, worldIn));
                break;
            case BROWN:
                if (CarpetServer.config.commandDistance.enabled)
                {
                    DistanceCalculator.report_distance(placer, pos);
                }
                break;
            case GRAY:
                if (CarpetServer.config.commandBlockInfo.enabled)
                    Messenger.send(placer, BlockInfo.blockInfo(pos.down(), worldIn));
                break;
            case YELLOW:
                if (CarpetServer.config.commandEntityInfo.enabled)
                    EntityInfo.issue_entity_info(placer);
                break;
            case GREEN:
                if (CarpetServer.config.hopperCounters.enabled)
                {
                    EnumDyeColor under = getWoolColorAtPosition(worldIn, pos.down());
                    if (under == null) return;
                    Messenger.send(placer, HopperCounter.query_hopper_stats_for_color(worldIn.getMinecraftServer(), under.toString(), false, false));
                }
                break;
            case RED:
                if (CarpetServer.config.hopperCounters.enabled)
                {
                    EnumDyeColor under = getWoolColorAtPosition(worldIn, pos.down());
                    if (under == null) return;
                    HopperCounter.reset_hopper_counter(worldIn, under.toString());
                    Messenger.s(placer, String.format("%s counter reset",under.toString() ));
                }
                break;
        }
    }
    */
    public static EnumDyeColor getWoolColorAtPosition(World worldIn, BlockPos pos) {

        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() != Blocks.WOOL) return null;
        return state.getValue(BlockColored.COLOR);

    }

}
