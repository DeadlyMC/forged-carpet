package carpet.forge.mixin;

import carpet.forge.helper.BlockRotator;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bootstrap.class)
public abstract class BootstrapMixin
{
    @Inject(method = "registerDispenserBehaviors", at = @At("HEAD"))
    private static void registerCarpetDispenserBehaviours(CallbackInfo ci)
    {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Item.getItemFromBlock(Blocks.CACTUS), new BlockRotator.CactusDispenserBehaviour());
    }
}
