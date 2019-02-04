package carpet.forge.tweak.tntDoNotUpdate;

import carpet.forge.CarpetMain;
import carpet.forge.CarpetSettings;
import net.minecraft.block.BlockTNT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockTNT.class)
public abstract class MixinBlockTNT {

    @Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockPowered(Lnet/minecraft/util/math/BlockPos;)Z"))
    public boolean onUpdate(World worldIn, BlockPos pos) {
        return worldIn.isBlockPowered(pos) && !CarpetSettings.getBool("TNTDoNotUpdate");
    }
}
