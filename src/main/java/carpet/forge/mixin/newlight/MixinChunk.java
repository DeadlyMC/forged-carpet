package carpet.forge.mixin.newlight;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.LightingHooks;
import carpet.forge.utils.mixininterfaces.IChunk;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// CREDITS : Nessie
@Mixin(Chunk.class)
public abstract class MixinChunk implements IChunk
{
    @Shadow
    @Final
    public static ExtendedBlockStorage NULL_BLOCK_STORAGE;
    @Shadow
    @Final
    public int x;
    
    private short[] neightborLightChecks = null;
    private short pendingNeighborLightInits;
    private int copyOfJ;
    private int copyOfK;
    
    @Shadow
    @Final
    private int[] heightMap;
    @Shadow
    @Final
    private World world;
    @Shadow
    private int heightMapMinimum;
    @Shadow
    @Final
    private ExtendedBlockStorage[] storageArrays;
    @Shadow
    private boolean isTerrainPopulated;
    
    @Shadow
    public abstract boolean canSeeSky(BlockPos pos);
    
    @Shadow
    protected abstract int getBlockLightOpacity(int x, int y, int z);
    
    @Shadow
    public abstract void checkLight();
    
    @Shadow
    public abstract void generateSkylightMap();
    
    @Shadow
    public abstract int getLightFor(EnumSkyBlock type, BlockPos pos);
    
    @Shadow
    protected abstract void propagateSkylightOcclusion(int x, int z);
    
    // Since we can't use LocalCapture directly in @Redirected methods we'll simply make a copy of them for ourselves.
    @Inject(method = "generateSkylightMap", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void setJAndKFields(CallbackInfo ci, int i, int j, int k)
    {
        if (CarpetSettings.newLight)
        {
            this.copyOfJ = j;
            this.copyOfK = k;
        }
    }
    
    @Redirect(method = "generateSkylightMap", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z"))
    private boolean callFillSkylightColumnInWorldsWithSkylight(WorldProvider worldProvider)
    {
        if (CarpetSettings.newLight)
        {
            if (this.world.provider.hasSkyLight())
            {
                LightingHooks.fillSkylightColumn((Chunk) (Object) this, this.copyOfJ, this.copyOfK);
            }
            return false;
        }
        else
        {
            return this.world.provider.hasSkyLight();
        }
    }
    
    // Soft override the method since there isn't really a "clean" way to do it with Mixins.
    @Inject(method = "relightBlock", at = @At("HEAD"), cancellable = true)
    private void onRelightBlock(int x, int y, int z, CallbackInfo ci)
    {
        if (CarpetSettings.newLight)
        {
            ci.cancel();
            int i = this.heightMap[z << 4 | x];
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
                this.heightMap[z << 4 | x] = j;
                
                if (this.world.provider.hasSkyLight())
                {
                    LightingHooks.relightSkylightColumn(this.world, (Chunk) (Object) this, x, z, i, j); // Forge: Optimized version of World.markBlocksDirtyVertical; heightMap is now updated (See #3871)
                }
                
                int l1 = this.heightMap[z << 4 | x];
                if (l1 < this.heightMapMinimum)
                {
                    this.heightMapMinimum = l1;
                }
            }
        }
    }
    
    @Redirect(method = "setBlockState", at = @At(value = "NEW",
            target = "net/minecraft/world/chunk/storage/ExtendedBlockStorage"))
    private ExtendedBlockStorage onSetBlockState(final int y, final boolean storeSkylight)
    {
        if (CarpetSettings.newLight)
        {
            final ExtendedBlockStorage extendedblockstorage1 = new ExtendedBlockStorage(y, storeSkylight);
            LightingHooks.initSkylightForSection(this.world, (Chunk) (Object) this, extendedblockstorage1); //Forge: Always initialize sections properly (See #3870 and #3879)
            return extendedblockstorage1;
        }
        else
        {
            return new ExtendedBlockStorage(y >> 4 << 4, this.world.provider.hasSkyLight());
        }
    }
    
    @ModifyVariable(method = "setBlockState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;set(IIILnet/minecraft/block/state/IBlockState;)V",
            ordinal = 0))
    private boolean setFlagToFalse(boolean flag)
    {
        if (CarpetSettings.newLight)
            return false;
        else
            return true;
    }
    
    @Redirect(method = "setBlockState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;propagateSkylightOcclusion(II)V"))
    private void cancelPropagateSkylightOcclusion(Chunk chunk, int x, int z)
    {
        if (!CarpetSettings.newLight)
            this.propagateSkylightOcclusion(x, z);
    }
    
    @Redirect(method = "setBlockState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;getLightFor(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I"))
    public int cancelGetLightFor(Chunk chunk, EnumSkyBlock type, BlockPos pos)
    {
        if (CarpetSettings.newLight)
            return 0;
        else
            return this.getLightFor(EnumSkyBlock.SKY, pos);
    }
    
    @Inject(method = "getLightFor", at = @At("HEAD"), cancellable = true)
    private void onGetLightFor(EnumSkyBlock type, BlockPos pos, CallbackInfoReturnable<Integer> cir)
    {
        if (CarpetSettings.newLight)
        {
            ((IWorld) this.world).getLightingEngine().procLightUpdates(type);
            cir.setReturnValue(this.getCachedLightFor(type, pos));
        }
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
    
    @Redirect(method = "setLightFor", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"))
    private void weCallThisElsewhere(Chunk chunk)
    {
        if (!CarpetSettings.newLight)
            this.generateSkylightMap();
    }
    
    @Inject(method = "setLightFor", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void insteadOfGenerateSkylightMap(EnumSkyBlock type, BlockPos pos, int value, CallbackInfo ci, int i, int j, int k, ExtendedBlockStorage extendedblockstorage)
    {
        if (CarpetSettings.newLight)
            LightingHooks.initSkylightForSection(this.world, (Chunk) (Object) this, extendedblockstorage);
        //Forge: generateSkylightMap produces the wrong result (See #3870)
    }
    
    @Inject(method = "getLightSubtracted", at = @At("HEAD"))
    private void onGetLightSubtracted(BlockPos pos, int amount, CallbackInfoReturnable<Integer> cir)
    {
        if (CarpetSettings.newLight)
            ((IWorld) this.world).getLightingEngine().procLightUpdates();
    }
    
    @Inject(method = "onLoad", at = @At("RETURN"))
    private void postOnLoad(CallbackInfo ci)
    {
        if (CarpetSettings.newLight)
            LightingHooks.onLoad(this.world, (Chunk) (Object) this);
    }
    
    @Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    protected void noPopulateCheckLight(Chunk chunk)
    {
        if (CarpetSettings.newLight)
            this.isTerrainPopulated = true;
        else
            this.checkLight();
    }
    
    @Redirect(method = "onTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    private void onCheckLight(Chunk chunk)
    {
        if (!CarpetSettings.newLight)
            this.checkLight();
    }
    
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
}
