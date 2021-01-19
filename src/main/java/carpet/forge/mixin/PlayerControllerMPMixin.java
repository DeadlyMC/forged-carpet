package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.crafting.IRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin
{
    @Shadow
    @Final
    private Minecraft mc;
    
    @Inject(method = "func_194338_a", at = @At("RETURN"))
    private void ctrlQCraftClient(int window, IRecipe recipe, boolean makeAll, EntityPlayer p_194338_4_, CallbackInfo ci)
    {
        if (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown() && CarpetSettings.ctrlQCraftingFix)
        {
            this.mc.playerController.windowClick(window, 0, 1, ClickType.THROW, this.mc.player);
        }
    }
}
