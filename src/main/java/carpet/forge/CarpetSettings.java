package carpet.forge;

import carpet.forge.network.CarpetPacketHandler;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static carpet.forge.CarpetSettings.RuleCategory.*;

public class CarpetSettings
{
    public static final String carpetVersion = "v1.2.1";
    public static final String minecraftVersion = "1.12.2";
    public static final Logger LOG = LogManager.getLogger();
    public static boolean locked = false;
    public static boolean impendingFillSkipUpdates = false;
    
    
    @Rule(desc = "Enables /spawn command for spawn tracking", category = COMMANDS)
    public static boolean commandSpawn = true;
    
    @Rule(desc = "Enables /tick command to control game speed", category = COMMANDS)
    public static boolean commandTick = true;
    
    @Rule(desc = "Enables /log command to monitor events in the game via chat and overlays", category = COMMANDS)
    public static boolean commandLog = true;
    
    @Rule(
            desc = "Enables /distance command to measure in game distance between points",
            category = COMMANDS,
            extra = {
            "Also enables brown carpet placement action if 'carpets' rule is turned on as well"
            }
    )
    public static boolean commandDistance = true;
    
    @Rule(
            desc = "Enables /blockinfo command",
            category = COMMANDS,
            extra = {
            "Also enables gray carpet placement action if 'carpets' rule is turned on as well"
            }
    )
    public static boolean commandBlockInfo = true;
    
    @Rule(
            desc = "Enables /entityinfo command",
            category = COMMANDS,
            extra = {
            "Also enables yellow carpet placement action if 'carpets' rule is turned on as well"
            }
    )
    public static boolean commandEntityInfo = true;
    
    @Rule(desc = "Enables /unload command to inspect chunk unloading order", category = COMMANDS)
    public static boolean commandUnload = true;
    
    @Rule(
            desc = "Enables /c and /s commands to quickly switch between camera and survival modes",
            category = COMMANDS,
            extra = {
            "/c and /s commands are available to all players regardless of their permission levels"
            }
    )
    public static boolean commandCameramode = true;
    
    @Rule(desc = "Enables /perimeterinfo command that scans the area around the block for potential spawnable spots", category = COMMANDS)
    public static boolean commandPerimeterInfo = true;
    
    @Rule(desc = "Enables /fillbiome command to change the biome of an area", category = COMMANDS)
    public static boolean commandFillBiome = true;
    
    @Rule(desc = "Enables /autosave command to query information about the autosave and execute commands relative to the autosave", category = COMMANDS)
    public static boolean commandAutosave = true;
    
    @Rule(desc = "Enables /ping for players to get their ping", category = COMMANDS)
    public static boolean commandPing = true;
    
    @Rule(desc = "Enables /player command to control/spawn players", category = COMMANDS)
    public static boolean commandPlayer = true;
    
    @Rule(desc = "Placing carpets may issue carpet commands for non-op players", category = SURVIVAL)
    public static boolean carpets = false;
    
    @Rule(
            desc = "Players can flip and rotate blocks when holding cactus",
            category = {CREATIVE, SURVIVAL},
            extra = {
            "Doesn't cause block updates when rotated/flipped",
            "Applies to pistons, observers, droppers, repeaters, stairs, glazed terracotta etc..."
            }
    )
    @CreativeDefault
    @SurvivalDefault
    public static boolean flippinCactus = false;
    
    @Rule(desc = "Observers don't pulse when placed", category = CREATIVE)
    public static boolean observersDoNonUpdate = false;
    
    @Rule(
            desc = "Transparent observers, TNT and redstone blocks. May cause lighting artifacts",
            category = CREATIVE,
            validator = "validateFlyingMachineTransparent"
    )
    public static boolean flyingMachineTransparent = false;
    private static boolean validateFlyingMachineTransparent(boolean value) {
        int newOpacity = value ? 0 : 255;
        Blocks.OBSERVER.setLightOpacity(newOpacity);
        Blocks.REDSTONE_BLOCK.setLightOpacity(newOpacity);
        Blocks.TNT.setLightOpacity(newOpacity);
        return true;
    }
    
    @Rule(desc = "TNT doesn't update when placed against a power source", category = TNT)
    public static boolean TNTDoNotUpdate = false;
    
    @Rule(
            desc = "hoppers pointing to wool will count items passing through them",
            category = {COMMANDS, CREATIVE, SURVIVAL},
            extra = {
            "Enables /counter command, and actions while placing red and green carpets on wool blocks",
            "Use /counter <color?> reset to reset the counter, and /counter <color?> to query",
            "In survival, place green carpet on same color wool to query, red to reset the counters",
            "Counters are global and shared between players, 16 channels available",
            "Items counted are destroyed, count up to one stack per tick per hopper"
            }
    )
    @CreativeDefault
    @SurvivalDefault
    public static boolean hopperCounters = false;
    
    @Rule(desc = "fill/clone/setblock and structure blocks cause block updates", category = CREATIVE)
    @CreativeDefault("false")
    public static boolean fillUpdates = true;
    
    @Rule(
            desc = "Customizable fill/clone volume limit",
            category = CREATIVE,
            options = {"32768", "250000", "1000000"},
            validator = "validateNonNegative"
    )
    @CreativeDefault("500000")
    public static int fillLimit = 32768;
    
    @Rule(desc = "Pumpkins and fence gates can be placed in mid air", category = CREATIVE)
    public static boolean relaxedBlockPlacement = false;
    
    @Rule(desc = "Explosions won't destroy blocks", category = TNT)
    public static boolean explosionNoBlockDamage = false;
    
    @Rule(desc = "Prevents llamas from taking player food while breeding", category = FIX)
    @BugFixDefault
    public static boolean llamaOverfeedingFix = false;
    
    @Rule(
            desc = "Fix for piston ghost blocks",
            category = FIX,
            extra = {
            "Does not work properly on vanilla clients with non-vanilla push limits"
            }
    )
    @BugFixDefault
    public static boolean pistonGhostBlocksFix = false;
    
    @Rule(
            desc = "Remove ghost blocks when mining too fast",
            category = FIX,
            extra = "Fixed in 1.13"
    )
    @SurvivalDefault
    public static boolean miningGhostBlocksFix = false;
    
    @Rule(
            desc = "Structure bounding boxes (i.e. witch huts) will generate correctly",
            category = FIX,
            extra = {
            "Fixes spawning issues due to incorrect bounding boxes"
            }
    )
    public static boolean boundingBoxFix = false;
    
    @Rule(
            desc = "Fixes server crashing under heavy load and low tps",
            category = FIX,
            extra = {
            "Won't prevent crashes if the server doesn't respond in max-tick-time ticks"
            }
    )
    @BugFixDefault
    public static boolean watchdogFix = false;
    
    @Rule(desc = "Spawned mobs that would otherwise despawn immediately, won't be placed in world", category = OPTIMIZATIONS)
    public static boolean optimizedDespawnRange = false;
    
    @Rule(desc = "Prevents players from rubberbanding when moving too fast", category = {CREATIVE, SURVIVAL})
    @CreativeDefault
    public static boolean antiCheatSpeed = false;
    
    @Rule(
            desc = "Dropping entire stacks works also from on the crafting UI result slot",
            category = {FIX, SURVIVAL}
    )
    @SurvivalDefault
    public static boolean ctrlQCraftingFix = false;
    
    @Rule(
            desc = "Empty shulker boxes can stack to 64 when dropped on the ground",
            category = SURVIVAL,
            extra = {
            "To move them around between inventories, use shift click to move entire stacks"
            }
    )
    @SurvivalDefault
    public static boolean stackableEmptyShulkerBoxes = false;
    
    @Rule(desc = "Only husks spawn in desert temples", category = {EXPERIMENTAL, FEATURE})
    public static boolean huskSpawningInTemples = false;
    
    @Rule(desc = "Silverfish drop a gravel item when breaking out of a block", category = EXPERIMENTAL)
    public static boolean silverFishDropGravel = false;
    
    @Rule(desc = "Shulkers will respawn in end cities", category = {FEATURE, EXPERIMENTAL})
    public static boolean shulkerSpawningInEndCities = false;
    
    @Rule(desc = "Cactus in dispensers rotates blocks.", extra = "Rotates block anti-clockwise if possible", category = FEATURE)
    public static boolean rotatorBlock = false;
    
    @Rule(
            desc = "Sets a different motd message on client trying to connect to the server",
            extra = "use '_' to use the startup setting from server.properties",
            options = "_",
            category = CREATIVE
    )
    public static String customMOTD = "_";
    
    @Rule(desc = "Customizable powered rail power range", category = CREATIVE, options = {"9", "15", "30"}, validator = "validatePositive")
    public static int railPowerLimit = 9;
    
    @Rule( desc = "Saplings turn into dead shrubs in hot climates and no water access", category = FEATURE )
    public static boolean desertShrubs = false;
    
    @Rule( desc = "Guardians turn into Elder Guardian when struck by lightning", category = FEATURE )
    public static boolean renewableSponges = false;
    
    @Rule( desc = "Players absorb XP instantly, without delay", category = CREATIVE )
    public static boolean xpNoCooldown = false;
    
    @Rule(
            desc = "Portals won't let a creative player go through instantly",
            extra = "Holding obsidian in either hand won't let you through at all",
            category = CREATIVE
    )
    public static boolean portalCreativeDelay = false;
    
    @Rule(desc = "Adds back farmland bug where entities teleport on top of farmland that turns back to dirt.", category = EXPERIMENTAL)
    public static boolean farmlandBug;
    
    @Rule(desc = "Places the mined block in the player inventory when sneaking.", category = FEATURE)
    public static boolean carefulBreak = false;
    
    // ===== API ===== //
    private static Map<String, Field> rules = new HashMap<>();
    private static Map<String, String> defaults = new HashMap<>();
    
    static
    {
        for (Field field : CarpetSettings.class.getFields())
        {
            if (field.isAnnotationPresent(Rule.class))
            {
                Rule rule = field.getAnnotation(Rule.class);
                String name = rule.name().isEmpty() ? field.getName() : rule.name();
                
                if (field.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC))
                    throw new AssertionError("Access modifiers of rule field for \"" + name + "\" should be \"public static\"");
                
                if (field.getType() != boolean.class && field.getType() != int.class && field.getType() != double.class && field.getType() != String.class && !field.getType().isEnum())
                {
                    throw new AssertionError("Rule \"" + name + "\" has invalid type");
                }
                
                Object def;
                try
                {
                    def = field.get(null);
                }
                catch (ReflectiveOperationException e)
                {
                    throw new AssertionError(e);
                }
                if (def == null)
                    throw new AssertionError("Rule \"" + name + "\" has null default value");
                
                if (field.getType() != boolean.class && !field.getType().isEnum())
                {
                    boolean containsDefault = false;
                    for (String option : rule.options())
                    {
                        Object val;
                        if (field.getType() == int.class)
                        {
                            try
                            {
                                val = Integer.parseInt(option);
                            }
                            catch (NumberFormatException e)
                            {
                                throw new AssertionError("Rule \"" + name + "\" has invalid option \"" + option + "\"");
                            }
                        }
                        else if (field.getType() == double.class)
                        {
                            try
                            {
                                val = Double.parseDouble(option);
                            }
                            catch (NumberFormatException e)
                            {
                                throw new AssertionError("Rule \"" + name + "\" has invalid option \"" + option + "\"");
                            }
                        }
                        else
                        {
                            val = option;
                        }
                        if (val.equals(def))
                            containsDefault = true;
                    }
                    if (!containsDefault)
                    {
                        throw new AssertionError("Default value of \"" + def + "\" for rule \"" + name + "\" is not included in its options. This is required for Carpet Client to work.");
                    }
                }
                
                String validator = rule.validator();
                if (!validator.isEmpty())
                {
                    Method method;
                    try
                    {
                        method = CarpetSettings.class.getDeclaredMethod(validator, field.getType());
                    }
                    catch (NoSuchMethodException e)
                    {
                        throw new AssertionError("Validator \"" + validator + "\" for rule \"" + name + "\" doesn't exist");
                    }
                    if (!Modifier.isStatic(method.getModifiers()) || method.getReturnType() != boolean.class)
                    {
                        throw new AssertionError("Validator \"" + validator + "\" for rule \"" + name + "\" must be a static method returning a boolean");
                    }
                }
                
                rules.put(name.toLowerCase(Locale.ENGLISH), field);
                defaults.put(name.toLowerCase(Locale.ENGLISH), String.valueOf(def));
            }
        }
    }
    
    private static boolean validatePositive(int value) {
        return value > 0;
    }
    
    private static boolean validateNonNegative(int value) {
        return value >= 0;
    }
    
    public static boolean hasRule(String ruleName) {
        return rules.containsKey(ruleName.toLowerCase(Locale.ENGLISH));
    }
    
    public static String get(String ruleName) {
        Field field = rules.get(ruleName.toLowerCase(Locale.ENGLISH));
        if (field == null)
            return "false";
        try
        {
            return String.valueOf(field.get(null));
        }
        catch (ReflectiveOperationException e)
        {
            throw new AssertionError(e);
        }
    }
    
    
    // ===== IMPLEMENTATION ===== //
    
    public static String getDescription(String ruleName) {
        Field field = rules.get(ruleName.toLowerCase(Locale.ENGLISH));
        if (field == null)
            return "Error";
        return field.getAnnotation(Rule.class).desc();
    }

    public static RuleCategory[] getCategories(String ruleName) {
        Field field = rules.get(ruleName.toLowerCase(Locale.ENGLISH));
        if (field == null)
            return new RuleCategory[0];
        return field.getAnnotation(Rule.class).category();
    }
    
    public static String getDefault(String ruleName) {
        String def = defaults.get(ruleName.toLowerCase(Locale.ENGLISH));
        return def == null ? "false" : locked && ruleName.startsWith("command") ? "false" : def;
    }
    
    @SuppressWarnings("unchecked")
    public static String[] getOptions(String ruleName) {
        Field field = rules.get(ruleName.toLowerCase(Locale.ENGLISH));
        if (field == null || field.getType() == boolean.class)
        {
            return new String[]{"false", "true"};
        }
        else if (field.getType().isEnum())
        {
            return Arrays.stream(((Class<? extends Enum<?>>) field.getType()).getEnumConstants()).map(Enum::name).toArray(String[]::new);
        }
        else
        {
            return field.getAnnotation(Rule.class).options();
        }
    }
    
    public static String[] getExtraInfo(String ruleName) {
        Field field = rules.get(ruleName.toLowerCase(Locale.ENGLISH));
        if (field == null)
            return new String[0];
        return field.getAnnotation(Rule.class).extra();
    }
    
    public static String getActualName(String ruleName) {
        Field field = rules.get(ruleName.toLowerCase(Locale.ENGLISH));
        if (field == null)
            return "null";
        String name = field.getAnnotation(Rule.class).name();
        return name.isEmpty() ? field.getName() : name;
    }
    
    public static boolean isDouble(String ruleName) {
        Field field = rules.get(ruleName.toLowerCase(Locale.ENGLISH));
        if (field == null)
            return false;
        else
            return field.getType() == double.class;
    }
    
    
    public static boolean set(String ruleName, String value)
    {
        return set(ruleName, value, true);
    }
    
    @SuppressWarnings("unchecked")
    public static boolean set(String ruleName, String value, boolean update) {
        Field field = rules.get(ruleName.toLowerCase(Locale.ENGLISH));
        if (field == null)
            return false;
        
        Class<?> fieldType = field.getType();
        Object newValue;
        if (fieldType == boolean.class)
        {
            if ("true".equalsIgnoreCase(value))
                newValue = true;
            else if ("false".equalsIgnoreCase(value))
                newValue = false;
            else
                return false;
        }
        else if (fieldType == int.class)
        {
            try
            {
                newValue = new Integer(value);
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }
        else if (fieldType == double.class)
        {
            try
            {
                newValue = new Double(value);
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }
        else if (fieldType == String.class)
        {
            newValue = value;
        }
        else if (fieldType.isEnum())
        {
            newValue = null;
            for (Enum<?> constant : ((Class<? extends Enum<?>>) fieldType).getEnumConstants())
            {
                if (constant.name().equalsIgnoreCase(value))
                {
                    newValue = constant;
                    break;
                }
            }
            if (newValue == null)
                return false;
        }
        else
        {
            throw new AssertionError("Rule \"" + ruleName + "\" has an invalid type");
        }
        
        String validatorMethod = field.getDeclaredAnnotation(Rule.class).validator();
        if (!validatorMethod.isEmpty())
        {
            try
            {
                Method validator = CarpetSettings.class.getDeclaredMethod(validatorMethod, field.getType());
                if (!((Boolean) validator.invoke(null, newValue)))
                    return false;
            }
            catch (ReflectiveOperationException e)
            {
                throw new AssertionError(e);
            }
        }
        
        try
        {
            field.set(null, newValue);
        }
        catch (ReflectiveOperationException e)
        {
            throw new AssertionError(e);
        }
        
        if (update)
        {
            CarpetPacketHandler.updateRuleWithConnectedClients(ruleName, value);
        }
        return true;
    }
    
    public static String[] findNonDefault() {
        List<String> rules = new ArrayList<>();
        for (String rule : CarpetSettings.rules.keySet())
            if (!get(rule).equalsIgnoreCase(getDefault(rule)))
                rules.add(getActualName(rule));
        Collections.sort(rules);
        return rules.toArray(new String[0]);
    }
    
    public static String[] findAll(String filter) {
        String actualFilter = filter == null ? null : filter.toLowerCase(Locale.ENGLISH);
        return rules.keySet().stream().filter(rule -> {
            if (actualFilter == null)
                return true;
            if (rule.contains(actualFilter))
                return true;
            for (RuleCategory ctgy : getCategories(rule))
                if (ctgy.name().equalsIgnoreCase(actualFilter))
                    return true;
            return false;
        }).map(CarpetSettings::getActualName).sorted().toArray(String[]::new);
    }
    
    public static void resetToUserDefaults(MinecraftServer server) {
        resetToVanilla();
        applySettingsFromConf(server);
    }
    
    public static void resetToVanilla() {
        for (String rule : rules.keySet())
        {
            set(rule, getDefault(rule));
        }
    }
    
    public static void resetToBugFixes() {
        resetToVanilla();
        rules.forEach((name, field) -> {
            if (field.isAnnotationPresent(BugFixDefault.class))
            {
                set(name, field.getAnnotation(BugFixDefault.class).value());
            }
        });
    }
    
    public static void resetToCreative() {
        resetToBugFixes();
        rules.forEach((name, field) -> {
            if (field.isAnnotationPresent(CreativeDefault.class))
            {
                set(name, field.getAnnotation(CreativeDefault.class).value());
            }
        });
    }
    
    public static void resetToSurvival() {
        resetToBugFixes();
        rules.forEach((name, field) -> {
            if (field.isAnnotationPresent(SurvivalDefault.class))
            {
                set(name, field.getAnnotation(SurvivalDefault.class).value());
            }
        });
    }
    
    public static void applySettingsFromConf(MinecraftServer server) {
        resetToVanilla();
        Map<String, String> conf = readConf(server);
        boolean is_locked = locked;
        locked = false;
        if (is_locked)
        {
            LOG.info("[CM]: Carpet Mod is locked by the administrator");
        }
        for (String key : conf.keySet())
        {
            if (!set(key, conf.get(key)))
                LOG.error("[CM]: The value of " + conf.get(key) + " for " + key + " is not valid - ignoring...");
            else
                LOG.info("[CM]: loaded setting " + key + " as " + conf.get(key) + " from carpet.conf");
        }
        locked = is_locked;
    }
    
    private static Map<String, String> readConf(MinecraftServer server) {
        try
        {
            File settings_file = server.getActiveAnvilConverter().getFile(server.getFolderName(), "carpet.conf");
            BufferedReader b = new BufferedReader(new FileReader(settings_file));
            String line = "";
            Map<String, String> result = new HashMap<String, String>();
            while ((line = b.readLine()) != null)
            {
                line = line.replaceAll("\\r|\\n", "");
                if ("locked".equalsIgnoreCase(line))
                {
                    locked = true;
                }
                String[] fields = line.split("\\s+", 2);
                if (fields.length > 1)
                {
                    if (!hasRule(fields[0]))
                    {
                        LOG.error("[CM]: Setting " + fields[0] + " is not a valid - ignoring...");
                        continue;
                    }
                    result.put(fields[0], fields[1]);
                }
            }
            b.close();
            return result;
        }
        catch (FileNotFoundException e)
        {
            return new HashMap<String, String>();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new HashMap<String, String>();
        }
        
    }
    
    private static void writeConf(MinecraftServer server, Map<String, String> values) {
        if (locked)
            return;
        try
        {
            File settings_file = server.getActiveAnvilConverter().getFile(server.getFolderName(), "carpet.conf");
            FileWriter fw = new FileWriter(settings_file);
            for (String key : values.keySet())
            {
                fw.write(key + " " + values.get(key) + "\n");
            }
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            LOG.error("[CM]: failed write the carpet.conf");
        }
    }
    
    // stores different defaults in the file
    public static boolean addOrSetPermarule(MinecraftServer server, String setting_name, String string_value) {
        if (locked)
            return false;
        if (hasRule(setting_name))
        {
            Map<String, String> conf = readConf(server);
            conf.put(setting_name, string_value);
            writeConf(server, conf);
            return set(setting_name, string_value);
        }
        return false;
    }
    
    // removes overrides of the default values in the file
    public static boolean removePermarule(MinecraftServer server, String setting_name) {
        if (locked)
            return false;
        if (hasRule(setting_name))
        {
            Map<String, String> conf = readConf(server);
            conf.remove(setting_name);
            writeConf(server, conf);
            return set(setting_name, getDefault(setting_name));
        }
        return false;
    }
    
    
    // ===== CONFIG ===== //
    
    public static String[] findStartupOverrides(MinecraftServer server) {
        ArrayList<String> res = new ArrayList<String>();
        if (locked)
            return res.toArray(new String[0]);
        Map<String, String> defaults = readConf(server);
        for (String rule : rules.keySet().stream().sorted().collect(Collectors.toList()))
        {
            if (defaults.containsKey(rule))
            {
                res.add(get(rule));
            }
        }
        return res.toArray(new String[0]);
    }
    
    public static enum RuleCategory
    {
        TNT, FIX, SURVIVAL, CREATIVE, EXPERIMENTAL, OPTIMIZATIONS, FEATURE, COMMANDS
    }
    
    /**
     * Any field in this class annotated with this class is interpreted as a carpet rule.
     * The field must be static and have a type of one of:
     * - boolean
     * - int
     * - double
     * - String
     * - a subclass of Enum
     * The default value of the rule will be the initial value of the field.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface Rule
    {
        /**
         * The rule name, by default the same as the field name
         */
        String name() default ""; // default same as field name
        
        /**
         * A description of the rule
         */
        String desc();
        
        /**
         * Extra information about the rule
         */
        String[] extra() default {};
        
        /**
         * A list of categories the rule is in
         */
        RuleCategory[] category();
        
        /**
         * Options to select in menu and in carpet client.
         * Inferred for booleans and enums. Otherwise, must be present.
         */
        String[] options() default {};
        
        /**
         * The name of the validator method called when the rule is changed.
         * The validator method must:
         * - be declared in CarpetSettings
         * - be static
         * - have a return type of boolean
         * - have a single parameter whose type is the same as that of the rule
         * The validator returns true if the value of the rule is accepted, and false otherwise.
         */
        String validator() default ""; // default no method
    }
    
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface CreativeDefault
    {
        String value() default "true";
    }
    
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface SurvivalDefault
    {
        String value() default "true";
    }
    
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface BugFixDefault
    {
        String value() default "true";
    }
}
