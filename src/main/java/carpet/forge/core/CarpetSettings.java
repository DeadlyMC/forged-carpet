package carpet.forge.core;

import carpet.forge.core.config.Configuration;

import java.io.File;

public class CarpetSettings {

    private Configuration config;

    //Performance
    public boolean rsturbo;
    public boolean newlight;
    public boolean isTransparent;
    public boolean optimizedDespawnRange;

    //Bug Fixes
    public boolean llamafix;
    public boolean pistonGhostBlocks;
    public boolean miningGhostBlocks;
    public boolean boundingBoxFix;
    public boolean watchDogFix;
    public boolean ctrlQCraftingFix;

    //Tweaks
    public boolean observertweak;

    //Commands
    public boolean commandPing;
    public boolean commandCameraMode;
    public boolean commandBlockInfo;
    public boolean commandSpawn;
    public boolean commandEntityInfo;
    public boolean commandAutoSave;
    public boolean commandCounter;
    public boolean commandFillBiome;
    // public boolean commandPlayer;
    public boolean commandLog;
    public boolean commandPerimeter;
    public boolean commandTick;

    //Helper
    public boolean flipcacti;
    public boolean tntDoNotUpdate;
    public boolean hopperCounters;

    public void init(File file){
        if(config == null){
            config = new Configuration(file);
            this.load();
        }
    }

    public void load(){
        config.load();

        //Tweaks
        observertweak       = config.get("tweaks", "observertweak", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];

        //Helper
        flipcacti           = config.get("helper", "flipcacti", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        tntDoNotUpdate      = config.get("helper", "tntDoNotUpdate", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];
        hopperCounters       = config.get("helper", "hopperCounters", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];

        //Performance
        rsturbo             = config.get("performance", "rsturbo", new boolean[]{false, false}, null, true, 2).getBooleanList()[0];
        newlight            = config.get("performance", "newlight", new boolean[]{false, false}, null, true, 2).getBooleanList()[0];
        isTransparent       = config.get("performance", "isTransparent", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];
        optimizedDespawnRange = config.get("performance", "optimizedDespawnRange", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];

        //Bug Fixes
        llamafix            = config.get("bug fixes", "llamafix", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];
        pistonGhostBlocks   = config.get("bug fixes", "pistonGhostBlocks", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];
        miningGhostBlocks   = config.get("bug fixes", "miningGhostBlocks", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];
        boundingBoxFix      = config.get("bug fixes", "witchHutBB", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];
        watchDogFix         = config.get("bug fixes", "watchDogFix", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];
        ctrlQCraftingFix    = config.get("bug fixes", "ctrlQCraftingFix", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];

        //Commands
        commandCameraMode   = config.get("commands", "commandCameraMode", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandPing         = config.get("commands", "commandPing", new boolean[]{true ,true}, null, true, 2).getBooleanList()[0];
        commandBlockInfo    = config.get("commands", "commandBlockInfo", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandSpawn        = config.get("commands", "commandSpawn", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandEntityInfo   = config.get("commands", "commandEntityInfo", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandAutoSave     = config.get("commands", "commandAutoSave", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandCounter      = config.get("commands", "commandCounter", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandFillBiome    = config.get("commands", "commandFillBiome", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
      //  commandPlayer       = config.get("commands", "commandPlayer", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandLog          = config.get("commands", "commandLog", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandPerimeter    = config.get("commands", "commandPerimeter", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        commandTick         = config.get("commands", "commandTick", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
    }

}

