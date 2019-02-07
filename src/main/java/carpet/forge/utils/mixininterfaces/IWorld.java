package carpet.forge.utils.mixininterfaces;

import carpet.forge.utils.LightingEngine;
import carpet.forge.utils.TickingArea;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.List;

public interface IWorld {
    LightingEngine getLightingEngine();
    long getRandSeed();
    List<TickingArea> getTickingAreas();
    LongOpenHashSet getTickingChunks();
}
