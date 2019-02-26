package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSilverfish.class)
public abstract class MixinBlockSilverfish extends Block
{
    public MixinBlockSilverfish(Material blockMaterialIn, MapColor blockMapColorIn)
    {
        super(blockMaterialIn, blockMapColorIn);
    }
    
    @Inject(method = "dropBlockAsItemWithChance", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/entity/monster/EntitySilverfish;spawnExplosionParticle()V"))
    private void dropGravelSilverfish(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune, CallbackInfo ci)
    {
        if (CarpetSettings.getBool("silverFishDropGravel"))
        {
            spawnAsEntity(worldIn, pos, new ItemStack(Blocks.GRAVEL));
        }
    }
}
