package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import carpet.forge.utils.LightingHooks;
import carpet.forge.fakes.IChunk;
import carpet.forge.fakes.IWorld;
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

@Mixin(Chunk.class)
public abstract class Chunk_newLightMixin
{
    private int copyOfJ;
    private int copyOfK;
    
    private int copyOfZ;
    private int copyOfX;
    private int copyOfI;
    private int copyOfJ2;
    
    @Shadow
    @Final
    private World world;
    
    @Shadow
    private boolean isTerrainPopulated;
    
    @Shadow
    protected abstract void propagateSkylightOcclusion(int x, int z);
    
    @Shadow private boolean dirty;
    
    // Since we can't use LocalCapture directly in @Redirected methods we'll simply make a copy of them for ourselves.
    @Inject(
            method = "generateSkylightMap",
            at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void copyJAndK(CallbackInfo ci, int i, int j, int k)
    {
        if (CarpetSettings.newLight)
        {
            this.copyOfJ = j;
            this.copyOfK = k;
        }
    }
    
    @Redirect(
            method = "generateSkylightMap",
                at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z")
    )
    private boolean onGenerateSkylightMap(WorldProvider worldProvider)
    {
        if (CarpetSettings.newLight)
        {
            if (worldProvider.hasSkyLight())
            {
                LightingHooks.fillSkylightColumn((Chunk) (Object) this, this.copyOfJ, this.copyOfK);
            }
            return false;
        }
        else
        {
            return worldProvider.hasSkyLight();
        }
    }
    
    @Inject(
            method = "relightBlock",
            at = @At(value = "FIELD", ordinal = 0,
                    target = "Lnet/minecraft/world/chunk/Chunk;heightMap:[I")
    )
    private void copyVars(int x, int y, int z, CallbackInfo ci)
    {
        this.copyOfZ = z;
        this.copyOfX = x;
    }
    
    @ModifyConstant(method = "relightBlock", constant = @Constant(intValue = 255))
    private int onRelightBlock(int original)
    {
        return CarpetSettings.newLight ? -1 : original;
    }
    
    @Redirect(method = "relightBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;markBlocksDirtyVertical(IIII)V"))
    private void onRelightBlock2(World world, int x, int z, int y1, int y2)
    {
        if (!CarpetSettings.newLight)
            world.markBlocksDirtyVertical(x, z, y1, y2);
    }
    
    @Inject(
            method = "relightBlock",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void copyIAndJ(int x, int y, int z, CallbackInfo ci, int i, int j, int k, int l, World var14, int var15, int var16, int var17, int var18)
    {
        this.copyOfI = i;
        this.copyOfJ2 = j;
    }
    
    @Redirect(method = "relightBlock", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z"))
    private boolean onRelightBlock3(WorldProvider worldProvider)
    {
        if (CarpetSettings.newLight)
        {
            if (worldProvider.hasSkyLight())
            {
                LightingHooks.relightSkylightColumn(this.world, (Chunk) (Object)this, copyOfX, copyOfZ, copyOfI, copyOfJ2);
            }
            return false;
        }
        else
        {
            return worldProvider.hasSkyLight();
        }
    }
    
    @Inject(
            method = "relightBlock",
            at = @At(value = "INVOKE", ordinal = 1,
                    target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z"),
            cancellable = true
    )
    private void onRelightBlock4(int x, int y, int z, CallbackInfo ci)
    {
        if (CarpetSettings.newLight)
        {
            this.dirty = true;
            ci.cancel();
        }
    }
    
    @Redirect(method = "setBlockState", at = @At(value = "NEW",
            target = "net/minecraft/world/chunk/storage/ExtendedBlockStorage"))
    private ExtendedBlockStorage onSetBlockState(final int y, final boolean storeSkylight)
    {
        final ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(y, storeSkylight);
        if (CarpetSettings.newLight)
        {
            LightingHooks.initSkylightForSection(this.world, (Chunk) (Object) this, extendedblockstorage); //Forge: Always initialize sections properly (See #3870 and #3879)
        }
        return extendedblockstorage;
    }
    
    @ModifyVariable(method = "setBlockState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;set(IIILnet/minecraft/block/state/IBlockState;)V",
            ordinal = 0))
    private boolean setFlagToFalse(boolean flag)
    {
        if (CarpetSettings.newLight)
            return false;
        else
            return flag;
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
            return chunk.getLightFor(type, pos);
    }
    
    @Inject(method = "getLightFor", at = @At("HEAD"), cancellable = true)
    private void onGetLightFor(EnumSkyBlock type, BlockPos pos, CallbackInfoReturnable<Integer> cir)
    {
        if (!CarpetSettings.newLight) return;
        ((IWorld) this.world).getLightingEngine().procLightUpdates(type);
        cir.setReturnValue(((IChunk) this).getCachedLightFor(type, pos));
    }
    
    @Redirect(method = "setLightFor", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"))
    private void weCallThisElsewhere(Chunk chunk)
    {
        if (!CarpetSettings.newLight)
            chunk.generateSkylightMap();
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
            chunk.checkLight();
    }
    
    @Redirect(method = "onTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    private void onCheckLight(Chunk chunk)
    {
        if (!CarpetSettings.newLight)
            chunk.checkLight();
    }
}
