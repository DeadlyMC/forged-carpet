package carpet.forge.mixin;

import carpet.forge.fakes.IWorld;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(Chunk.class)
public abstract class Chunk_fillUpdatesMixin
{
    @Shadow @Final private World world;
    
    @Shadow @Nullable public abstract TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode);
    
    // Litematica redirects isRemote, so have to make use of multiple redirects to cancel each line.
    @Redirect(method = "setBlockState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/Block;breakBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V"))
    private void fillUpdatesCheck1(Block block1, World world, BlockPos pos, IBlockState iblockstate, BlockPos blockpos, IBlockState state)
    {
        if (!((IWorld) world).shouldSkipUpdates())
        {
            block1.breakBlock(world, pos, iblockstate);
        }
        else if (block1.hasTileEntity(iblockstate) && world.isRemote)
        {
            TileEntity te = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
            if (te != null && te.shouldRefresh(this.world, pos, iblockstate, state))
                world.removeTileEntity(pos);
        }
    }
    
    @Redirect(method = "setBlockState", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/world/chunk/Chunk;getTileEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/Chunk$EnumCreateEntityType;)Lnet/minecraft/tileentity/TileEntity;"))
    private TileEntity fillUpdatesCheck2(Chunk chunk, BlockPos pos, Chunk.EnumCreateEntityType creationMode)
    {
        if (!((IWorld) this.world).shouldSkipUpdates())
            return chunk.getTileEntity(pos, creationMode);
        return null;
    }
    
    @Redirect(method = "setBlockState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/Block;onBlockAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V"))
    private void fillUpdatesCheck3(Block block, World world, BlockPos pos, IBlockState state)
    {
        if (!((IWorld) this.world).shouldSkipUpdates())
            block.onBlockAdded(world, pos, state);
    }
}
