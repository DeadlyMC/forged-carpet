package carpet.forge;

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

public class CarpetSettings
{
    public static final String carpetVersion = "v19_05_02";
    public static final String minecraftVersion = "1.12.2";
    public static final Logger LOG = LogManager.getLogger();
    public static boolean locked = false;
    
    // ===== COMMANDS ===== //
    /*
     * Rules in this category should start with the "command" prefix
     */
    
    
    // ===== CREATIVE TOOLS ===== //
    
    
    // ===== FIXES ===== //
    /*
     * Rules in this category should end with the "Fix" suffix
     */
    
    
    // ===== SURVIVAL FEATURES ===== //
    
    
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
    
    @SuppressWarnings("unchecked")
    public static boolean set(String ruleName, String value) {
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
        
        //CarpetClientRuleChanger.updateCarpetClientsRule(ruleName, value);
        
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
