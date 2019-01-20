package carpet.forge.config.gui;

import carpet.forge.utils.Reference;
import carpet.forge.config.CarpetConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class CarpetGuiConfig extends GuiConfig {
    public CarpetGuiConfig(GuiScreen parent) {
        super(parent,
                getConfigElements(),
                Reference.MOD_ID,
                false,
                false,
                "ForgedCarpet"
        );
        titleLine2 = "General Configurations";
        this.entryList = new PatchEntries(this, this.mc);
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        list.add(new DummyConfigElement.DummyCategoryElement("Performance", "performance", CarpetGuiConfig.Performance.class));
        list.add(new DummyConfigElement.DummyCategoryElement("Bug Fixes", "bugfixes", CarpetGuiConfig.BugFixes.class));
        list.add(new DummyConfigElement.DummyCategoryElement("Tweaks", "tweaks", CarpetGuiConfig.Tweaks.class));
        list.add(new DummyConfigElement.DummyCategoryElement("Commands", "commands", CarpetGuiConfig.Commands.class));
        list.add(new DummyConfigElement.DummyCategoryElement("Helper", "helper", CarpetGuiConfig.Helper.class));
        return list;
    }

    @Override
    public void initGui() {
        if (this.entryList == null || this.needsRefresh) {
            this.entryList = new PatchEntries(this, this.mc);
            this.needsRefresh = false;
        }
        // You can add buttons and initialize fields here
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // You can process any additional buttons you may have added here
        super.actionPerformed(button);
    }

    public static class PatchGuiConfig extends GuiConfig {
        PatchGuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, String configID,
                       boolean allRequireWorldRestart, boolean allRequireMcRestart, String title) {
            super(parentScreen, configElements, modID, configID, allRequireWorldRestart, allRequireMcRestart, title);

            this.entryList = new PatchEntries(this, this.mc);
        }

        @Override
        public void initGui() {
            if (this.entryList == null || this.needsRefresh) {
                this.entryList = new PatchEntries(this, this.mc);
                this.needsRefresh = false;
            }

            // You can add buttons and initialize fields here
            super.initGui();
        }
    }

    public static class Performance extends GuiConfigEntries.CategoryEntry {

        public Performance(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            List<IConfigElement> list = new ArrayList<>();

            for (Property performance : CarpetConfig.config.getCategory("performance").getOrderedValues()) {
                list.add(new PatchElement(performance));
            }

            GuiConfig guiConfig = new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "performance", false, false, "ForgedCarpet");
            guiConfig.titleLine2 = "Performance";

            return guiConfig;
        }

    }

    public static class BugFixes extends GuiConfigEntries.CategoryEntry {

        public BugFixes(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            List<IConfigElement> list = new ArrayList<>();

            for (Property bugfix : CarpetConfig.config.getCategory("bug fixes").getOrderedValues()) {
                list.add(new PatchElement(bugfix));
            }

            GuiConfig guiConfig = new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "bug fixes", false, false, "ForgedCarpet");
            guiConfig.titleLine2 = "Bug Fixes";

            return guiConfig;
        }

    }

    public static class Tweaks extends GuiConfigEntries.CategoryEntry {

        public Tweaks(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            List<IConfigElement> list = new ArrayList<>();

            for (Property tweaks : CarpetConfig.config.getCategory("tweaks").getOrderedValues()) {
                list.add(new PatchElement(tweaks));
            }

            GuiConfig guiConfig = new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "tweaks", false, false, "ForgedCarpet");
            guiConfig.titleLine2 = "Tweaks";

            return guiConfig;
        }

    }

    public static class Helper extends GuiConfigEntries.CategoryEntry {

        public Helper(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            List<IConfigElement> list = new ArrayList<>();

            for (Property helper : CarpetConfig.config.getCategory("helper").getOrderedValues()) {
                list.add(new PatchElement(helper));
            }

            GuiConfig guiConfig = new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "helper", false, false, "ForgedCarpet");
            guiConfig.titleLine2 = "Helper";

            return guiConfig;
        }

    }

    public static class Commands extends GuiConfigEntries.CategoryEntry {

        public Commands(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            List<IConfigElement> list = new ArrayList<>();

            for (Property commands : CarpetConfig.config.getCategory("commands").getOrderedValues()) {
                list.add(new PatchElement(commands));
            }

            GuiConfig guiConfig = new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "commands", false, false, "ForgedCarpet");
            guiConfig.titleLine2 = "Commands";

            return guiConfig;
        }

    }
}
