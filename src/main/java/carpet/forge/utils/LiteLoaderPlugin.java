package carpet.forge.utils;

import carpet.forge.ForgedCarpet;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class LiteLoaderPlugin implements IMixinConfigPlugin
{
    @Override
    public void onLoad(String mixinPackage) { }
    
    @Override
    public String getRefMapperConfig() { return null; }
    
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        try
        {
            Class.forName("com.mumfrey.liteloader.LiteMod");
            ForgedCarpet.logger.info("Liteloader detected!");
            return mixinClassName.equals("carpet.forge.mixin.NetHandlerPlayServerLiteMixin");
        }
        catch (ClassNotFoundException e)
        {
            ForgedCarpet.logger.info("Liteloader not detected!");
            return mixinClassName.equals("carpet.forge.mixin.NetHandlerPlayServerForgeMixin");
        }
    }
    
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    
    @Override
    public List<String> getMixins() { return null; }
    
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
