package carpet.forge.logging.mixins;

import carpet.forge.logging.LoggerRegistry;
import carpet.forge.utils.Messenger;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin
{
    @Shadow
    protected WorldInfo worldInfo;

    @Inject(method = "updateWeatherBody", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;isThundering()Z", ordinal = 1))
    private void loggerRegistryWeatherThunder(CallbackInfo ci)
    {
        if (LoggerRegistry.__weather)
        {
            LoggerRegistry.getLogger("weather").log(() -> new ITextComponent[]{Messenger.s(null, "Thunder is set to: " + this.worldInfo.isThundering() + " time: " + this.worldInfo.getThunderTime())}, "TYPE", "Thunder", "THUNDERING", this.worldInfo.isThundering(), "TIME", this.worldInfo.getThunderTime());
        }
    }

    @Inject(method = "updateWeatherBody", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;isRaining()Z", ordinal = 1))
    private void loggerRegistryWeatherRaining(CallbackInfo ci)
    {
        if (LoggerRegistry.__weather)
        {
            LoggerRegistry.getLogger("weather").log(() -> new ITextComponent[]{Messenger.s(null, "Rain is set to: " + this.worldInfo.isRaining() + " time: " + this.worldInfo.getRainTime())}, "TYPE", "Rain", "RAINING", this.worldInfo.isRaining(), "TIME", this.worldInfo.getRainTime());
        }
    }
}
