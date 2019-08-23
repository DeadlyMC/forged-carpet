package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.helper.BlockSaplingHelper;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockSapling.class)
public abstract class BlockSaplingMixin
{
    @Inject(
            method = "grow(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/block/BlockSapling;generateTree(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"),
            cancellable = true
    )
    private void onGrow(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci)
    {
        if(CarpetSettings.desertShrubs && worldIn.getBiome(pos) == Biomes.DESERT && !BlockSaplingHelper.hasWater(worldIn, pos))
        {
            worldIn.setBlockState(pos, Blocks.DEADBUSH.getDefaultState(), 3);
            ci.cancel();
        }
    }
}
