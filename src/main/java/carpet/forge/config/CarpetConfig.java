package carpet.forge.config;

import carpet.forge.CarpetMain;
import carpet.forge.utils.Reference;
import carpet.forge.performance.FlyingMachineTransparent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import javax.sound.midi.Patch;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CarpetConfig {

    public static Configuration config;

    private boolean serverLocked;

    public final PatchDef rsturbo = new PatchDef("rsturbo", PatchDef.Side.JOINED)
            .setDisplayName("RS Turbo")
            .setCategory("Performance")
            .setCredits("Theosib, MrGrim")
            .setDefaults(new boolean[]{false, false})
            .setToggleable(false)
            .setSideEffects("Does not have 100% vanilla behavior, but is very close.")
            .setComment(new String[]{"Lag optimizations for redstone Dust and sensible redstone behaviour."});

    public final PatchDef newlight = new PatchDef("newlight", PatchDef.Side.JOINED, PatchDef.ServerSyncHandlers.IGNORE)
            .setDisplayName("New Light")
            .setCategory("Performance")
            .setCredits("PhiPro, Mathe172, Nessie, MrGrim")
            .setDefaults(new boolean[]{false, false})
            .setToggleable(false)
            .setComment(new String[]{"Uses alternative lighting engine from NewLight mod."});

    public final PatchDef llamafix = new PatchDef("llamafix", PatchDef.Side.JOINED)
            .setDisplayName("Llama Over Feeding")
            .setCategory("Bug Fixes")
            .setCredits("Xcom, Gnembon")
            .setDefaults(new boolean[]{true, false})
            .setComment(new String[]{"Prevents llamas from taking player food while breeding."});

    public final PatchDef flipcacti = new PatchDef("flipcacti", PatchDef.Side.JOINED)
            .setDisplayName("Flippin Cactus")
            .setCategory("Helper")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Players can flip and rotate blocks when holding cactus."});

    public final PatchDef observertweak = new PatchDef("observertweak", PatchDef.Side.JOINED)
            .setDisplayName("ObserversDoNonUpdate")
            .setCategory("Tweaks")
            .setDefaults(new boolean[]{true, false})
            .setComment(new String[]{"Observers don't pulse when placed."});

    public final PatchDef isTransparent = new PatchDef("isTransparent", PatchDef.Side.JOINED)
            .setDisplayName("Flying Machine Transparent")
            .setCategory("Performance")
            .setDefaults(new boolean[]{true, false})
            .setSideEffects("May cause lighting artifacts.")
            .setComment(new String[]{"Transparent observers, TNT and redstone blocks."});

    public final PatchDef pistonGhostBlocks = new PatchDef("pistongGhostBlocks", PatchDef.Side.JOINED)
            .setDisplayName("Piston Ghost Blocks")
            .setCategory("Bug Fixes")
            .setCredits("Xcom, MrGrim")
            .setDefaults(new boolean[]{true, false})
            .setComment(new String[]{"Fix for piston ghost blocks"});

    public final PatchDef miningGhostBlocks = new PatchDef("miningGhostBlocks", PatchDef.Side.JOINED)
            .setDisplayName("Mining Ghost Blocks")
            .setCategory("Bug Fixes")
            .setCredits("Gnembon, Xcom")
            .setDefaults(new boolean[]{true, false})
            .setComment(new String[]{"Removes ghost blocks when mining too fast"});

    public final PatchDef boundingBoxFix = new PatchDef("boundingBoxFix", PatchDef.Side.JOINED)
            .setDisplayName("Bounding Box Fix")
            .setCategory("Bug Fixes")
            .setCredits("Xcom")
            .setDefaults(new boolean[]{true, false})
            .setComment(new String[]{"Structure bounding boxes (i.e. witch huts) will generate correctly"})
            .setSideEffects("Fixes spawning issues due to incorrect bounding boxes");

    public final PatchDef commandCameraMode = new PatchDef("commandCameraMode", PatchDef.Side.JOINED)
            .setDisplayName("Command CameraMode")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Enables /c and /s commands to quickly switch between camera and survival modes"});

    public final PatchDef tntDoNotUpdate = new PatchDef("tntDoNotUpdate", PatchDef.Side.JOINED)
            .setDisplayName("TNTDoNotUpdate")
            .setCategory("Helper")
            .setDefaults(new boolean[]{true, false})
            .setComment(new String[]{"TNT doesn't update when placed against a power source"});

    public final PatchDef commandPing = new PatchDef("commandPing", PatchDef.Side.JOINED)
            .setDisplayName("Command Ping")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Enables /ping for players to get their ping"});

    public final PatchDef commandBlockInfo = new PatchDef("commandBlockInfo", PatchDef.Side.JOINED)
            .setDisplayName("Command BlockInfo")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Enables /blockinfo command to get technical info about blocks"});

    public final PatchDef commandSpawn = new PatchDef("commandSpawn", PatchDef.Side.JOINED)
            .setDisplayName("Command Spawn")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Enables /spawn command for spawn tracking"});

    public final PatchDef commandEntityInfo = new PatchDef("commandEntityInfo", PatchDef.Side.JOINED)
            .setDisplayName("Command EntityInfo")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Enables /entityinfo command to get technical info about entities"});

    public final PatchDef hopperCounters = new PatchDef("hopperCounters", PatchDef.Side.JOINED)
            .setDisplayName("Hopper Counters")
            .setCategory("Helper")
            .setDefaults(new boolean[]{true, false})
            .setComment(new String[]{"Hoppers pointing to wool will count items passing through them\n Items counted are destroyed, count up to one stack per tick per hopper"});

    public final PatchDef commandAutoSave = new PatchDef("commandAutoSave", PatchDef.Side.JOINED)
            .setDisplayName("Command AutoSave")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Enables /autosave command to query information about the autosave and execute commands relative to the autosave"});

    public final PatchDef commandCounter = new PatchDef("commandCounter", PatchDef.Side.JOINED)
            .setDisplayName("Command Counter")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Enables /counter command \n Use /counter <color?> reset to reset the counter, and /counter <color?> to query\n Counters are global and shared between players, 16 channels available"});

    public final PatchDef commandFillBiome = new PatchDef("commandFillBiome", PatchDef.Side.JOINED)
            .setDisplayName("Command FillBiome")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{"Enables /fillbiome command to change the biome of an area"});

   /* public final PatchDef commandPlayer = new PatchDef("commandPlayer", PatchDef.Side.JOINED)
            .setDisplayName("Command Player")
            .setCategory("Commands")
            .setDefaults(new boolean[]{true, true})
            .setComment(new String[]{""});

   public final PatchDef stackableEmptyShulkerBox = new PatchDef("stackableEmptyShulkerBox", PatchDef.Side.JOINED)
           .setDisplayName("stackableEmptyShulkerBox")
           .setCategory("Helper")
           .setDefaults(new boolean[]{true, false})
           .setComment(new String[]{""});
           */

    public void init(File file) {
        if (config == null) {
            config = new Configuration(file);
            this.load();
        }
    }

    public void load() {
        config.load();
        this.sync();
        config.save();
    }

    public void sync() {
        for (Field field : this.getClass().getFields()) {
            Object fieldObj;

            try {
                fieldObj = field.get(this);
            } catch (Exception e) {
                CarpetMain.logger.error("Unknown field access reading configuration file.");
                continue;
            }

            if (fieldObj.getClass() == PatchDef.class) {
                boolean[] bugState;
                PatchDef patchDef = (PatchDef) fieldObj;

                if (!this.isServerLocked() || patchDef.isClientToggleable()) {
                    bugState = config.get(patchDef.getCategory(), field.getName(), patchDef.getDefaults(), String.join("\n", patchDef.getComment()), true, 2).getBooleanList();

                    if (bugState[0]) patchDef.setLoaded();
                    patchDef.setEnabled(bugState[1]);
                }
            }
        }
    }

    public void lock() {
        this.serverLocked = true;
    }

    public void unlock() {
        this.serverLocked = false;
    }

    public boolean isServerLocked() {
        return this.serverLocked;
    }

    @Nullable
    public PatchDef get(String propName) {
        try {
            return (PatchDef) (this.getClass().getField(propName).get(this));
        } catch (Exception e) {
            CarpetMain.logger.error("Unknown field access fetching property.");
            return null;
        }
    }

    public List<PatchDef> getAll() {
        List<PatchDef> bugs = new ArrayList<>();

        for (Field field : this.getClass().getFields()) {
            Object fieldObj;

            try {
                fieldObj = field.get(this);
            } catch (Exception e) {
                CarpetMain.logger.error("Unknown field access enumerating PatchDef's.");
                continue;
            }

            if (fieldObj.getClass() == PatchDef.class) {
                bugs.add((PatchDef) fieldObj);
            }
        }

        return bugs;
    }

    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MOD_ID)) {
            config.save();
            CarpetMain.config.sync();
            FlyingMachineTransparent.setFlyingMachineTransparent();

            // Right now this event is only ever called from GUI code by Forge itself, but just in case...
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                if (Minecraft.getMinecraft().getConnection() != null) {
                }
            }
        }
    }
}


