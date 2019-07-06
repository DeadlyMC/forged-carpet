package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.LightingHooks;
import carpet.forge.utils.mixininterfaces.IChunk;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements IChunk
{

    @Shadow
    @Final
    public static ExtendedBlockStorage NULL_BLOCK_STORAGE;

    @Shadow
    @Final
    public int x;

    @Shadow
    @Final
    public int z;

    @Shadow
    @Final
    private ExtendedBlockStorage[] storageArrays;

    @Shadow
    @Final
    private World world;

    @Shadow
    @Final
    private int[] precipitationHeightMap;

    @Shadow
    @Final
    private int[] heightMap;

    @Shadow
    private boolean dirty;

    @Shadow
    public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow
    @Nullable
    public abstract TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode);

    @Shadow
    public abstract int getLightFor(EnumSkyBlock type, BlockPos pos);

    @Shadow
    protected abstract void propagateSkylightOcclusion(int x, int z);

    @Shadow
    protected abstract void relightBlock(int x, int y, int z);

    @Shadow
    public abstract void generateSkylightMap();
    
    @Shadow public abstract boolean canSeeSky(BlockPos pos);
    
    // [FCM] Replace method with a carpet method
    @Inject(method = "setBlockState", at = @At("HEAD"), cancellable = true)
    private void redirectToCarpetMethod(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir)
    {
        cir.setReturnValue(setBlockState_carpet(pos, state, false));
        cir.cancel();
    }

    @Nullable
    public IBlockState setBlockState_carpet(BlockPos pos, IBlockState state, boolean skip_updates) // [FCM] added skip_updates
    {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        int l = k << 4 | i;

        if (j >= this.precipitationHeightMap[l] - 1)
        {
            this.precipitationHeightMap[l] = -999;
        }

        int i1 = this.heightMap[l];
        IBlockState iblockstate = this.getBlockState(pos);

        if (iblockstate == state)
        {
            return null;
        }
        else
        {
            Block block = state.getBlock();
            Block block1 = iblockstate.getBlock();
            int k1 = iblockstate.getLightOpacity(this.world, pos); // Relocate old light value lookup here, so that it is called before TE is removed.
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
            boolean flag = false;

            if (extendedblockstorage == NULL_BLOCK_STORAGE)
            {
                if (block == Blocks.AIR)
                {
                    return null;
                }

                extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, this.world.provider.hasSkyLight());
                this.storageArrays[j >> 4] = extendedblockstorage;
                flag = j >= i1;
                // [FCM] Newlight
                if (CarpetSettings.newLight)
                {
                    LightingHooks.initSkylightForSection(this.world, (Chunk) (Object) this, extendedblockstorage); //Forge: Always initialize sections properly (See #3870 and #3879)
                }
            }

            extendedblockstorage.set(i, j & 15, k, state);

            //if (block1 != block)
            {
                if (!this.world.isRemote)
                {
                    if (block1 != block) //Only fire block breaks when the block changes.
                        block1.breakBlock(this.world, pos, iblockstate);
                    TileEntity te = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                    if (te != null && te.shouldRefresh(this.world, pos, iblockstate, state))
                        this.world.removeTileEntity(pos);
                }
                else if (block1.hasTileEntity(iblockstate))
                {
                    TileEntity te = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                    if (te != null && te.shouldRefresh(this.world, pos, iblockstate, state))
                        this.world.removeTileEntity(pos);
                }
            }

            if (extendedblockstorage.get(i, j & 15, k).getBlock() != block)
            {
                return null;
            }
            else
            {
                // [FCM] Newlight -- Forge: Don't call generateSkylightMap (as it produces the wrong result; sections are initialized above). Never bypass relightBlock (See #3870)
                if (!CarpetSettings.newLight && flag)
                {
                    this.generateSkylightMap();
                }
                else
                {
                    int j1 = state.getLightOpacity(this.world, pos);

                    if (j1 > 0)
                    {
                        if (j >= i1)
                        {
                            this.relightBlock(i, j + 1, k);
                        }
                    }
                    else if (j == i1 - 1)
                    {
                        this.relightBlock(i, j, k);
                    }

                    // [FCM] Newlight -- Forge: Error correction is unnecessary as these are fixed (See #3871)
                    if (!CarpetSettings.newLight)
                    {
                        if (j1 != k1 && (j1 < k1 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0))
                        {
                            this.propagateSkylightOcclusion(i, k);
                        }
                    }
                }

                // If capturing blocks, only run block physics for TE's. Non-TE's are handled in ForgeHooks.onPlaceItemIntoWorld
                // [FCM] Added '!skip_updates' to if statement parameters
                if (!skip_updates && !this.world.isRemote && block1 != block && (!this.world.captureBlockSnapshots || block.hasTileEntity(state)))
                {
                    block.onBlockAdded(this.world, pos, state);
                }

                if (block.hasTileEntity(state))
                {
                    TileEntity tileentity1 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

                    if (tileentity1 == null)
                    {
                        tileentity1 = block.createTileEntity(this.world, state);
                        this.world.setTileEntity(pos, tileentity1);
                    }

                    if (tileentity1 != null)
                    {
                        tileentity1.updateContainingBlockInfo();
                    }
                }

                this.dirty = true;
                return iblockstate;
            }
        }
    }
    
    private short[] neightborLightChecks = null;
    private short pendingNeighborLightInits;
    
    @Override
    public short[] getNeighborLightChecks()
    {
        return this.neightborLightChecks;
    }
    
    @Override
    public void setNeighborLightChecks(short[] in)
    {
        this.neightborLightChecks = in;
    }
    
    @Override
    public short getPendingNeighborLightInits()
    {
        return this.pendingNeighborLightInits;
    }
    
    @Override
    public void setPendingNeighborLightInits(short in)
    {
        this.pendingNeighborLightInits = in;
    }
    
    @Override
    public int getCachedLightFor(EnumSkyBlock type, BlockPos pos)
    {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
        
        if (extendedblockstorage == NULL_BLOCK_STORAGE)
        {
            return this.canSeeSky(pos) ? type.defaultLightValue : 0;
        }
        else if (type == EnumSkyBlock.SKY)
        {
            return !this.world.provider.hasSkyLight() ? 0 : extendedblockstorage.getSkyLight(i, j & 15, k);
        }
        else
        {
            return type == EnumSkyBlock.BLOCK ? extendedblockstorage.getBlockLight(i, j & 15, k) : type.defaultLightValue;
        }
    }

}
