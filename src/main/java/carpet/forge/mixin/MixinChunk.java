package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.LightingHooks;
import carpet.forge.utils.mixininterfaces.IChunk;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(Chunk.class)
public abstract class MixinChunk implements IChunk {

    @Shadow @Final private ExtendedBlockStorage[] storageArrays;

    @Shadow @Final public static ExtendedBlockStorage NULL_BLOCK_STORAGE;

    @Shadow public abstract boolean canSeeSky(BlockPos pos);

    @Shadow @Final private World world;

    @Shadow public abstract int getTopFilledSegment();

    @Shadow private int heightMapMinimum;
    @Shadow @Final private int[] precipitationHeightMap;

    @Shadow protected abstract int getBlockLightOpacity(int x, int y, int z);

    @Shadow @Final private int[] heightMap;
    @Shadow private boolean dirty;
    @Shadow @Final public int x;
    @Shadow @Final public int z;

    @Shadow protected abstract void updateSkylightNeighborHeight(int x, int z, int startY, int endY);

    @Shadow public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow @Nullable public abstract TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode);

    @Shadow public abstract int getLightFor(EnumSkyBlock type, BlockPos pos);

    @Shadow protected abstract void propagateSkylightOcclusion(int x, int z);

    @Shadow private boolean isTerrainPopulated;

    @Shadow public abstract void checkLight();

    @Shadow private boolean isGapLightingUpdated;

    @Shadow protected abstract void recheckGaps(boolean onlyOne);

    @Shadow private boolean ticked;
    @Shadow private boolean isLightPopulated;
    @Shadow @Final private ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue;

    @Shadow @Nullable protected abstract TileEntity createNewTileEntity(BlockPos pos);

    public short[] neighborLightChecks = null;
    public short pendingNeighborLightInits;

    /**
     * @author DeadlyMC
     * @reason if statement arounds
     */
    @Overwrite
    public void generateSkylightMap()
    {
        int i = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                this.precipitationHeightMap[j + (k << 4)] = -999;

                for (int l = i + 16; l > 0; --l)
                {
                    if (this.getBlockLightOpacity(j, l - 1, k) != 0)
                    {
                        this.heightMap[k << 4 | j] = l;

                        if (l < this.heightMapMinimum)
                        {
                            this.heightMapMinimum = l;
                        }

                        break;
                    }
                }

                if (this.world.provider.hasSkyLight())
                {
                    // [FCM] Newlight -- if statement around
                    if (CarpetSettings.newLight) {
                        LightingHooks.fillSkylightColumn((Chunk)(Object)this, j, k);
                    }
                    else
                    {
                        int k1 = 15;
                        int i1 = i + 16 - 1;

                        while (true) {
                            int j1 = this.getBlockLightOpacity(j, i1, k);

                            if (j1 == 0 && k1 != 15) {
                                j1 = 1;
                            }

                            k1 -= j1;

                            if (k1 > 0) {
                                ExtendedBlockStorage extendedblockstorage = this.storageArrays[i1 >> 4];

                                if (extendedblockstorage != NULL_BLOCK_STORAGE) {
                                    extendedblockstorage.setSkyLight(j, i1 & 15, k, k1);
                                    this.world.notifyLightSet(new BlockPos((this.x << 4) + j, i1, (this.z << 4) + k));
                                }
                            }

                            --i1;

                            if (i1 <= 0 || k1 <= 0) {
                                break;
                            }
                        }
                    }
                    // [FCM] Newlight -- end
                }
            }
        }

        this.dirty = true;
    }

    /**
     * @author DeadlyMC
     * @reason if statement arounds
     */
    @Overwrite
    private void relightBlock(int x, int y, int z)
    {
        // [FCM] Newlight -- Changed variable 'i' according to if statement
        int i;
        if (CarpetSettings.newLight)
        {
            i = this.heightMap[z << 4 | x];
        }
        else
        {
            i = this.heightMap[z << 4 | x] & 255;
        }
        // [FCM] End
        int j = i;

        if (y > i)
        {
            j = y;
        }

        while (j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0)
        {
            --j;
        }

        if (j != i)
        {
            // [FCM] Newlight -- Optimization
            if (!CarpetSettings.newLight)
                // Forge: Useless, since heightMap is not updated yet
                this.world.markBlocksDirtyVertical(x + this.x * 16, z + this.z * 16, j, i);
            this.heightMap[z << 4 | x] = j;
            int k = this.x * 16 + x;
            int l = this.z * 16 + z;

            if (this.world.provider.hasSkyLight())
            {
                // [FCM] Newlight -- Optimization
                if (CarpetSettings.newLight) {
                    LightingHooks.relightSkylightColumn(this.world, (Chunk) (Object) this, x, z, i, j); //Forge: Optimized version of World.markBlocksDirtyVertical; heightMap is now updated
                }
                else // Don't mess up the light cache; World.checkLight already does all necessary steps
                {
                    if (j < i)
                    {
                        for (int j1 = j; j1 < i; ++j1)
                        {
                            ExtendedBlockStorage extendedblockstorage2 = this.storageArrays[j1 >> 4];

                            if (extendedblockstorage2 != NULL_BLOCK_STORAGE) {
                                extendedblockstorage2.setSkyLight(x, j1 & 15, z, 15);
                                this.world.notifyLightSet(new BlockPos((this.x << 4) + x, j1, (this.z << 4) + z));
                            }
                        }
                    } else {
                        for (int i1 = i; i1 < j; ++i1)
                        {
                            ExtendedBlockStorage extendedblockstorage = this.storageArrays[i1 >> 4];

                            if (extendedblockstorage != NULL_BLOCK_STORAGE) {
                                extendedblockstorage.setSkyLight(x, i1 & 15, z, 0);
                                this.world.notifyLightSet(new BlockPos((this.x << 4) + x, i1, (this.z << 4) + z));
                            }
                        }
                    }

                    int k1 = 15;

                    while (j > 0 && k1 > 0)
                    {
                        --j;
                        int i2 = this.getBlockLightOpacity(x, j, z);

                        if (i2 == 0)
                        {
                            i2 = 1;
                        }

                        k1 -= i2;

                        if (k1 < 0)
                        {
                            k1 = 0;
                        }

                        ExtendedBlockStorage extendedblockstorage1 = this.storageArrays[j >> 4];

                        if (extendedblockstorage1 != NULL_BLOCK_STORAGE)
                        {
                            extendedblockstorage1.setSkyLight(x, j & 15, z, k1);
                        }
                    }
                }
                // [FCM] End
            }

            int l1 = this.heightMap[z << 4 | x];
            int j2 = i;
            int k2 = l1;

            if (l1 < i)
            {
                j2 = l1;
                k2 = i;
            }

            if (l1 < this.heightMapMinimum)
            {
                this.heightMapMinimum = l1;
            }

            // [FCM] NewLight -- Optimization
            if (CarpetSettings.newLight){
                this.dirty = true;
                return; //Forge: Following checks are not needed if the light cache is not messed up
            }

            if (this.world.provider.hasSkyLight())
            {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                {
                    this.updateSkylightNeighborHeight(k + enumfacing.getXOffset(), l + enumfacing.getZOffset(), j2, k2);
                }

                this.updateSkylightNeighborHeight(k, l, j2, k2);
            }

            this.dirty = true;
        }
    }

    // [FCM] Replace method with a carpet method
    @Inject(method = "setBlockState", at = @At("HEAD"), cancellable = true)
    private void redirectToCarpetMethod(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir){
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
                if (CarpetSettings.newLight){
                    LightingHooks.initSkylightForSection(this.world, (Chunk)(Object)this, extendedblockstorage); //Forge: Always initialize sections properly (See #3870 and #3879)
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
                    if (te != null && te.shouldRefresh(this.world, pos, iblockstate, state)) this.world.removeTileEntity(pos);
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

    @Inject(method = "getLightFor", at = @At(value = "HEAD"))
    private void procLightUpdatesNewLight1(EnumSkyBlock type, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        if (CarpetSettings.newLight)
            ((IWorld) this.world).getLightingEngine().procLightUpdates(type);
    }

    @Redirect(method = "setLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"))
    private void newLightInitSkyLight(Chunk chunk){
    }

    @Inject(method = "setLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void insteadOfGenerateSkylightMap(EnumSkyBlock type, BlockPos pos, int value, CallbackInfo ci, int i, int j, int k, ExtendedBlockStorage extendedblockstorage) {
        if (CarpetSettings.newLight){
            LightingHooks.initSkylightForSection(this.world, (Chunk)(Object)this, extendedblockstorage); //Forge: generateSkylightMap produces the wrong result (See #3870)
        }else{
            this.generateSkylightMap();
        }
    }

    @Inject(method = "getLightSubtracted", at = @At(value = "HEAD"))
    private void procLightUpdatesNewLight2(BlockPos pos, int amount, CallbackInfoReturnable<Integer> cir){
        if (CarpetSettings.newLight)
            ((IWorld) this.world).getLightingEngine().procLightUpdates();
    }

    @Inject(method = "onLoad", at = @At(value = "TAIL"))
    private void onOnLoad(CallbackInfo ci){
        if (CarpetSettings.newLight){
            LightingHooks.onLoad(this.world, (Chunk)(Object)this);
        }
    }

    @Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    private void ifCheckLight(Chunk chunk){
        if (CarpetSettings.newLight){
            this.isTerrainPopulated = true;
        }else{
            this.checkLight();
        }
    }

    /**
     * @author DeadlyMC
     * @reason if statement around
     */
    @Overwrite
    public void onTick(boolean skipRecheckGaps)
    {
        if (this.isGapLightingUpdated && this.world.provider.hasSkyLight() && !skipRecheckGaps)
        {
            this.recheckGaps(this.world.isRemote);
        }

        this.ticked = true;

        // [FCM] Newlight
        if (!CarpetSettings.newLight)
        {
            if (!this.isLightPopulated && this.isTerrainPopulated)
            {
                this.checkLight();
            }
        }

        while (!this.tileEntityPosQueue.isEmpty())
        {
            BlockPos blockpos = this.tileEntityPosQueue.poll();

            if (this.getTileEntity(blockpos, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlockState(blockpos).getBlock().hasTileEntity(this.getBlockState(blockpos)))
            {
                TileEntity tileentity = this.createNewTileEntity(blockpos);
                this.world.setTileEntity(blockpos, tileentity);
                this.world.markBlockRangeForRenderUpdate(blockpos, blockpos);
            }
        }
    }

    @Override
    public short[] getNeighborLightChecks() {
        return neighborLightChecks;
    }

    @Override
    public void setNeighborLightChecks(short[] lightChecks){
        neighborLightChecks = lightChecks;
    }

    @Override
    public short getPendingNeighborLightInits(){
        return this.pendingNeighborLightInits;
    }

    @Override
    public void setPendingNeighborLightInits(short inits){
        pendingNeighborLightInits = inits;
    }

    @Override
    // [FCM] Newlight -- new method added
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
