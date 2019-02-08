package carpet.forge;

import carpet.forge.utils.TickingArea;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CarpetSettings {

    public static boolean locked = false;
    public static final String carpetVersion = "v18_12_20";

    public static final Logger LOG = LogManager.getLogger();
    private static final Map<String, CarpetSettingEntry> settings_store;
    public static final CarpetSettingEntry FalseEntry = CarpetSettingEntry.create("void", "all", "Error").choices("None", "");

    public static final String[] default_tags = {"tnt", "fix", "survival", "creative", "experimental", "optimizations", "feature", "commands"}; //tab completion only

    static {
        settings_store = new HashMap<>();
        set_defaults();
    }

    public static boolean hopperCounters  = true;
    public static boolean fastRedstoneDust = false;
    public static boolean newLight = false;
    public static int pistonGhostBlocksFix = 0;
    public static int tileTickLimit = 65536;

    public static long setSeed = 0;

    private static CarpetSettingEntry rule(String s1, String s2, String s3) {
        return CarpetSettingEntry.create(s1, s2, s3);
    }

    public static void set_defaults() {
        CarpetSettingEntry[] RuleList = new CarpetSettingEntry[]
        {
                rule("fastRedstoneDust", "experimental optimizations", "Lag optimizations for redstone Dust. By Theosib"),
                rule("newLight", "optimizations", "Uses alternative lighting engine by PhiPros. AKA NewLight mod"),
                rule("llamaOverfeedingFix", "fix", "Prevents llamas from taking player food while breeding"),
                rule("flippinCactus",         "creative survival", "Players can flip and rotate blocks when holding cactus")
                        .extraInfo("Doesn't cause block updates when rotated/flipped",
                        "Applies to pistons, observers, droppers, repeaters, stairs, glazed terracotta etc..."),
                rule("observersDoNonUpdate",  "creative", "Observers don't pulse when placed"),
                rule("flyingMachineTransparent", "creative", "Transparent observers, TNT and redstone blocks. May cause lighting artifacts"),
                rule("pistonGhostBlocksFix",  "fix", "Fix for piston ghost blocks")
                        .extraInfo("true(serverOnly) option works with all clients, including vanilla",
                                "clientAndServer option requires compatible carpet clients and messes up flying machines")
                        .choices("false","false true clientAndServer"),
                rule("miningGhostBlocksFix",  "fix", "Removes ghost blocks when mining too fast")
                        .extraInfo("Fixed in 1.13"),
                rule("boundingBoxFix",        "fix", "Structure bounding boxes (i.e. witch huts) will generate correctly")
                        .extraInfo("Fixes spawning issues due to incorrect bounding boxes"),
                rule("commandCameramode",     "commands", "Enables /c and /s commands to quickly switch between camera and survival modes").defaultTrue()
                        .extraInfo("/c and /s commands are available to all players regardless of their permission levels"),
                rule("TNTDoNotUpdate",        "tnt", "TNT doesn't update when placed against a power source"),
                rule("commandPing",           "commands", "Enables /ping for players to get their ping").defaultTrue(),
                rule("commandBlockInfo",      "commands", "Enables /blockinfo command").defaultTrue(),
                rule("commandSpawn",          "commands", "Enables /spawn command for spawn tracking").defaultTrue(),
                rule("commandEntityInfo",     "commands", "Enables /entityinfo command").defaultTrue()
                        .extraInfo("Also enables yellow carpet placement action if 'carpets' rule is turned on as well"),
                rule("hopperCounters",        "commands creative survival","hoppers pointing to wool will count items passing through them")
                        .extraInfo("Enables /counter command, and actions while placing red and green carpets on wool blocks",
                        "Use /counter <color?> reset to reset the counter, and /counter <color?> to query",
                        "In survival, place green carpet on same color wool to query, red to reset the counters",
                        "Counters are global and shared between players, 16 channels available",
                        "Items counted are destroyed, count up to one stack per tick per hopper"),
                rule("commandAutosave",       "commands", "Enables /autosave command to query information about the autosave and execute commands relative to the autosave").defaultTrue(),
                rule("commandFillBiome",      "commands", "Enabled /fillbiome command to change the biome of an area").defaultTrue(),
                rule("commandLog",            "commands", "Enables /log command to monitor events in the game via chat and overlays").defaultTrue(),
                rule("optimizedDespawnRange", "optimizations", "Spawned mobs that would otherwise despawn immediately, won't be placed in world"),
                rule("commandPerimeterInfo",  "commands", "Enables /perimeterinfo command that scans the area around the block for potential spawnable spots").defaultTrue(),
                rule("commandTick",           "commands", "Enables /tick command to control game speed").defaultTrue(),
                rule("watchdogFix",           "fix", "Fixes server crashing under heavy load and low tps")
                        .extraInfo("Won't prevent crashes if the server doesn't respond in max-tick-time ticks"),
                rule("ctrlQCraftingFix",      "fix survival", "Dropping entire stacks works also from on the crafting UI result slot"),
                rule("commandDistance",       "commands", "Enables /distance command to measure in game distance between points").defaultTrue()
                        .extraInfo("Also enables brown carpet placement action if 'carpets' rule is turned on as well"),
                rule("commandUnload",         "commands", "Enables /unload command to control game speed").defaultTrue(),
                rule("commandRNG",            "commands", "Enables /rng command to manipulate and query rng").defaultTrue(),
                rule("tickingAreas",          "creative", "Enable use of ticking areas.")
                        .extraInfo("As set by the /tickingarea comamnd.",
                        "Ticking areas work as if they are the spawn chunks."),
                rule("disableSpawnChunks",    "creative", "Removes the spawn chunks."),
                rule("commandStructure",      "commands", "Enables /structure to manage NBT structures used by structure blocks").defaultTrue(),
                rule("fillUpdates",           "creative", "fill/clone/setblock and structure blocks cause block updates").defaultTrue(),
                rule("fillLimit",             "creative","Customizable fill/clone volume limit")
                        .choices("32768","32768 250000 1000000").setNotStrict(),
                rule("tileTickLimit",         "survival", "Customizable tile tick limit")
                        .extraInfo("Negative for no limit")
                        .choices("65536","1000 65536 1000000").setNotStrict(),
        };

        for (CarpetSettingEntry rule: RuleList)
        {
            settings_store.put(rule.getName(), rule);
        }
    }

    public static void reload_all_statics()
    {
        for (String rule: settings_store.keySet())
        {
            reload_stat(rule);
        }
    }

    public static void reload_stat(String rule)
    {
        hopperCounters = CarpetSettings.getBool("hopperCounters");
        fastRedstoneDust = CarpetSettings.getBool("fastRedstoneDust");
        newLight = CarpetSettings.getBool("newLight");
        tileTickLimit = CarpetSettings.getInt("tileTickLimit");

        if ("pistonGhostBlocksFix".equalsIgnoreCase(rule))
        {
            pistonGhostBlocksFix = 0;
            if("true".equalsIgnoreCase(CarpetSettings.getString("pistonGhostBlocksFix")))
            {
                pistonGhostBlocksFix = 1;
            }
            if("clientAndServer".equalsIgnoreCase(CarpetSettings.getString("pistonGhostBlocksFix")))
            {
                pistonGhostBlocksFix = 2;
            }
        }
        else if ("flyingMachineTransparent".equalsIgnoreCase(rule))
        {
            if(CarpetSettings.getBool("flyingMachineTransparent"))
            {
                Blocks.OBSERVER.setLightOpacity(0);
                Blocks.REDSTONE_BLOCK.setLightOpacity(0);
                Blocks.TNT.setLightOpacity(0);
            }
            else
            {
                Blocks.OBSERVER.setLightOpacity(255);
                Blocks.REDSTONE_BLOCK.setLightOpacity(255);
                Blocks.TNT.setLightOpacity(255);
            }
        }
        else if ("tickingAreas".equalsIgnoreCase(rule))
        {
            if (CarpetSettings.getBool("tickingAreas") && CarpetMain.minecraft_server.worlds != null)
            {
                TickingArea.initialChunkLoad(CarpetMain.minecraft_server, false);
            }
        }
        else if ("disableSpawnChunks".equalsIgnoreCase(rule))
        {
            if (!CarpetSettings.getBool("disableSpawnChunks") && CarpetMain.minecraft_server.worlds != null)
            {
                World overworld = CarpetMain.minecraft_server.worlds[0];
                for (ChunkPos chunk : new TickingArea.SpawnChunks().listIncludedChunks(overworld))
                {
                    overworld.getChunkProvider().provideChunk(chunk.x, chunk.z);
                }
            }
        }
    }

    public static void apply_settings_from_conf(MinecraftServer server)
    {
        Map<String, String> conf = read_conf(server);
        boolean is_locked = locked;
        locked = false;
        if (is_locked)
        {
            LOG.info("[CM]: Carpet Mod is locked by the administrator");
        }
        for (String key: conf.keySet())
        {
            set(key, conf.get(key));
            LOG.info("[CM]: loaded setting "+key+" as "+conf.get(key)+" from forgedcarpet.conf");
        }
        locked = is_locked;
    }

    private static void disable_commands_by_detault()
    {
        for (CarpetSettingEntry entry: settings_store.values())
        {
            if (entry.getName().startsWith("command"))
            {
                entry.defaultFalse();
            }
        }
    }

    private static Map<String, String> read_conf(MinecraftServer server)
    {
        try
        {
            File settings_file = server.getActiveAnvilConverter().getFile(server.getFolderName(), "forgedcarpet.conf");
            BufferedReader b = new BufferedReader(new FileReader(settings_file));
            String line = "";
            Map<String,String> result = new HashMap<String, String>();
            while ((line = b.readLine()) != null)
            {
                line = line.replaceAll("\\r|\\n", "");
                if ("locked".equalsIgnoreCase(line))
                {
                    disable_commands_by_detault();
                    locked = true;
                }
                String[] fields = line.split("\\s+",2);
                if (fields.length > 1)
                {
                    if (get(fields[0])==FalseEntry)
                    {
                        LOG.error("[CM]: Setting " + fields[0] + " is not a valid - ignoring...");
                        continue;
                    }
                    if (!(Arrays.asList(get(fields[0]).getOptions()).contains(fields[1])) && get(fields[0]).isStrict())
                    {
                        LOG.error("[CM]: The value of " + fields[1] + " for " + fields[0] + " is not valid - ignoring...");
                        continue;
                    }
                    result.put(fields[0],fields[1]);
                }
            }
            b.close();
            return result;
        }
        catch(FileNotFoundException e)
        {
            return new HashMap<String, String>();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new HashMap<String, String>();
        }

    }

    private static void write_conf(MinecraftServer server, Map<String, String> values)
    {
        if (locked) return;
        try
        {
            File settings_file = server.getActiveAnvilConverter().getFile(server.getFolderName(), "forgedcarpet.conf");
            FileWriter fw = new FileWriter(settings_file);
            for (String key: values.keySet())
            {
                fw.write(key+" "+values.get(key)+"\n");
            }
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            LOG.error("[CM]: failed write the forgedcarpet.conf");
        }
    }

    // stores different defaults in the file
    public static boolean add_or_set_permarule(MinecraftServer server, String setting_name, String string_value)
    {
        if (locked) return false;
        if (settings_store.containsKey(setting_name))
        {
            Map<String, String> conf = read_conf(server);
            conf.put(setting_name, string_value);
            write_conf(server, conf);
            set(setting_name,string_value);
            return true;
        }
        return false;
    }
    // removes overrides of the default values in the file
    public static boolean remove_permrule(MinecraftServer server, String setting_name)
    {
        if (locked) return false;
        if (settings_store.containsKey(setting_name))
        {
            Map<String, String> conf = read_conf(server);
            conf.remove(setting_name);
            write_conf(server, conf);
            set(setting_name,get(setting_name).getDefault());
            return true;
        }
        return false;
    }

    //changes setting temporarily
    public static boolean set(String setting_name, String string_value)
    {
        CarpetSettingEntry en = get(setting_name);
        if (en != FalseEntry)
        {
            en.set(string_value);
            reload_stat(setting_name);
          //  CarpetClientRuleChanger.updateCarpetClientsRule(setting_name, string_value);
            return true;
        }
        return false;
    }

    // used as CarpetSettings.get("pushLimit").integer to get the int value of push limit
    public static CarpetSettingEntry get(String setting_name)
    {
        if (!settings_store.containsKey(setting_name) )
        {
            return FalseEntry;
        }
        return settings_store.get(setting_name);
    }

    public static int getInt(String setting_name)
    {
        return get(setting_name).getIntegerValue();
    }

    public static boolean getBool(String setting_name)
    {
        return get(setting_name).getBoolValue();
    }
    public static String getString(String setting_name) { return get(setting_name).getStringValue(); }

    public static float getFloat(String setting_name)
    {
        return get(setting_name).getFloatValue();
    }

    public static CarpetSettingEntry[] find_all(String tag)
    {
        ArrayList<CarpetSettingEntry> res = new ArrayList<CarpetSettingEntry>();
        for (String rule: settings_store.keySet().stream().sorted().collect(Collectors.toList()))
        {
            if (tag == null || settings_store.get(rule).matches(tag))
            {
                res.add(settings_store.get(rule));
            }
        }
        return res.toArray(new CarpetSettingEntry[0]);
    }

    public static CarpetSettingEntry[] find_nondefault(MinecraftServer server)
    {
        ArrayList<CarpetSettingEntry> res = new ArrayList<CarpetSettingEntry>();
        Map <String,String> defaults = read_conf(server);
        for (String rule: settings_store.keySet().stream().sorted().collect(Collectors.toList()))
        {
            if (!settings_store.get(rule).isDefault() || defaults.containsKey(rule))
            {
                res.add(settings_store.get(rule));
            }
        }
        return res.toArray(new CarpetSettingEntry[0]);
    }

    public static CarpetSettingEntry[] find_startup_overrides(MinecraftServer server)
    {
        ArrayList<CarpetSettingEntry> res = new ArrayList<CarpetSettingEntry>();
        if (locked) return res.toArray(new CarpetSettingEntry[0]);
        Map <String,String> defaults = read_conf(server);
        for (String rule: settings_store.keySet().stream().sorted().collect(Collectors.toList()))
        {
            if (defaults.containsKey(rule))
            {
                res.add(settings_store.get(rule));
            }
        }
        return res.toArray(new CarpetSettingEntry[0]);
    }

    public static String[] toStringArray(CarpetSettingEntry[] entry_array)
    {
        return Stream.of(entry_array).map(CarpetSettingEntry::getName).toArray( String[]::new );
    }

    public static ArrayList<CarpetSettingEntry> getAllCarpetSettings()
    {
        ArrayList<CarpetSettingEntry> res = new ArrayList<CarpetSettingEntry>();
        for (String rule: settings_store.keySet().stream().sorted().collect(Collectors.toList()))
        {
            res.add(settings_store.get(rule));
        }

        return res;
    }

    public static CarpetSettingEntry getCarpetSetting(String rule)
    {
        return settings_store.get(rule);
    }

    public static void resetToVanilla()
    {
        for (String rule: settings_store.keySet())
        {
            get(rule).reset();
            reload_stat(rule);
        }
    }

    public static void resetToUserDefaults(MinecraftServer server)
    {
        resetToVanilla();
        apply_settings_from_conf(server);
    }

    public static void resetToCreative()
    {
        resetToBugFixes();
        set("fillLimit","500000");
        set("fillUpdates","false");
        set("portalCreativeDelay","true");
        set("portalCaching","true");
        set("flippinCactus","true");
        set("hopperCounters","true");
        set("antiCheatSpeed","true");
        set("worldEdit","true");
    }
    public static void resetToSurvival()
    {
        resetToBugFixes();
        set("ctrlQCraftingFix","true");
        set("persistentParrots", "true");
        set("stackableEmptyShulkerBoxes","true");
        set("flippinCactus","true");
        set("hopperCounters","true");
        set("carpets","true");
        set("missingTools","true");
        set("portalCaching","true");
        set("miningGhostBlocksFix","true");
    }
    public static void resetToBugFixes()
    {
        resetToVanilla();
        set("portalSuffocationFix","true");
        set("pistonGhostBlocksFix","serverOnly");
        set("portalTeleportationFix","true");
        set("entityDuplicationFix","true");
        set("inconsistentRedstoneTorchesFix","true");
        set("llamaOverfeedingFix","true");
        set("invisibilityFix","true");
        set("potionsDespawnFix","true");
        set("liquidsNotRandom","true");
        set("mobsDontControlMinecarts","true");
        set("breedingMountingDisabled","true");
        set("growingUpWallJump","true");
        set("reloadSuffocationFix","true");
        set("watchdogFix","true");
        set("unloadedEntityFix","true");
        set("hopperDuplicationFix","true");
        set("calmNetherFires","true");
        set("pistonSerializationFix","true");
        set("reloadUpdateOrderFix","true");
        set("leashFix","true");
        set("randomTickOptimization","true");
        set("dismountFix","true");
        set("disableVanillaTickWarp","true");
        set("ridingPlayerUpdateFix","true");
    }

    public static class CarpetSettingEntry {
        private String rule;
        private String string;
        private int integer;
        private boolean bool;
        private float flt;
        private String[] options;
        private String[] tags;
        private String toast;
        private String[] extra_info;
        private String default_string_value;
        private boolean isFloat;
        private boolean strict;

        //factory
        public static CarpetSettingEntry create(String rule_name, String tags, String toast) {
            return new CarpetSettingEntry(rule_name, tags, toast);
        }

        private CarpetSettingEntry(String rule_name, String tags_string, String toast_string) {
            set("false");
            rule = rule_name;
            default_string_value = string;
            tags = tags_string.split("\\s+"); // never empty
            toast = toast_string;
            options = "true false".split("\\s+");
            isFloat = false;
            extra_info = null;
            strict = true;
        }

        public CarpetSettingEntry defaultTrue() {
            set("true");
            default_string_value = string;
            options = "true false".split("\\s+");
            return this;
        }

        public CarpetSettingEntry defaultFalse() {
            set("false");
            default_string_value = string;
            options = "true false".split("\\s+");
            return this;
        }

        public CarpetSettingEntry choices(String defaults, String options_string) {
            set(defaults);
            default_string_value = string;
            options = options_string.split("\\s+");
            return this;
        }

        public CarpetSettingEntry extraInfo(String... extra_info_string) {
            extra_info = extra_info_string;
            return this;
        }

        public CarpetSettingEntry setFloat() {
            isFloat = true;
            strict = false;
            return this;
        }

        public CarpetSettingEntry setNotStrict() {
            strict = false;
            return this;
        }

        private void set(String unparsed) {
            string = unparsed;
            try {
                integer = Integer.parseInt(unparsed);
            } catch (NumberFormatException e) {
                integer = 0;
            }
            try {
                flt = Float.parseFloat(unparsed);
            } catch (NumberFormatException e) {
                flt = 0.0F;
            }
            bool = (integer > 0) ? true : Boolean.parseBoolean(unparsed);
        }

        //accessors
        public boolean isDefault() {
            return string.equals(default_string_value);
        }

        public String getDefault() {
            return default_string_value;
        }

        public String toString() {
            return rule + ": " + string;
        }

        public String getToast() {
            return toast;
        }

        public String[] getInfo() {
            return extra_info == null ? new String[0] : extra_info;
        }

        public String[] getOptions() {
            return options;
        }

        public String[] getTags() {
            return tags;
        }

        public String getName() {
            return rule;
        }

        public String getStringValue() {
            return string;
        }

        public boolean getBoolValue() {
            return bool;
        }

        public int getIntegerValue() {
            return integer;
        }

        public float getFloatValue() {
            return flt;
        }

        public boolean getIsFloat() {
            return isFloat;
        }

        //actual stuff
        public void reset() {
            set(default_string_value);
        }

        public boolean matches(String tag) {
            tag = tag.toLowerCase();
            if (rule.toLowerCase().contains(tag)) {
                return true;
            }
            for (String t : tags) {
                if (tag.equalsIgnoreCase(t)) {
                    return true;
                }
            }
            return false;
        }

        public String getNextValue() {
            int i;
            for (i = 0; i < options.length; i++) {
                if (options[i].equals(string)) {
                    break;
                }
            }
            i++;
            return options[i % options.length];
        }

        public boolean isStrict() {
            return strict;
        }
    }
}
