package carpet.forge.mixin;

import carpet.forge.utils.CarpetProfiler;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@SuppressWarnings("ConstantConditions")
@Mixin(World.class)
public class World_profilerMixin
{
    @Shadow @Final public WorldProvider provider;
    
    @Inject(method = "updateEntities", at = @At(value = "CONSTANT", args = "stringValue=remove", ordinal = 0))
    private void startEntitiesProfiling(CallbackInfo ci)
    {
        if ((World) (Object) (this) instanceof WorldServer)
        {
            String world_name = this.provider.getDimensionType().getName();
            CarpetProfiler.start_section(world_name, "entities");
        }
    }
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRidingEntity()Lnet/minecraft/entity/Entity;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void startEntityProfiling(CallbackInfo ci, int i1, Entity entity2)
    {
        if ((World) (Object) (this) instanceof WorldServer)
        {
            String world_name = this.provider.getDimensionType().getName();
            CarpetProfiler.start_entity_section(world_name, entity2);
        }
    }
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 0), slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=remove", ordinal = 1),
            to = @At(value = "CONSTANT", args = "stringValue=blockEntities")))
    private void stopEntityProfiling(CallbackInfo ci)
    {
        if ((World) (Object) (this) instanceof WorldServer) CarpetProfiler.end_current_entity_section();
    }
    
    @Inject(method = "updateEntities", at = @At(value = "CONSTANT", args = "stringValue=blockEntities"))
    private void stopEntitiesProfilingAndStartTEProfiling(CallbackInfo ci)
    {
        if ((World) (Object) (this) instanceof WorldServer)
        {
            CarpetProfiler.end_current_section();
            String world_name = this.provider.getDimensionType().getName();
            CarpetProfiler.start_section(world_name, "tileentities");
        }
    }
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;isInvalid()Z", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void startTESectionProfiling(CallbackInfo ci, Iterator iterator, TileEntity tileentity)
    {
        if ((World) (Object) (this) instanceof WorldServer)
            CarpetProfiler.start_tileentity_section(this.provider.getDimensionType().getName(), tileentity);
    }
    
    @Inject(method = "updateEntities", at = @At(value = "JUMP", opcode = Opcodes.GOTO, shift = At.Shift.BEFORE), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;removeTileEntity(Lnet/minecraft/util/math/BlockPos;)V"),
            to = @At(value = "CONSTANT", args = "stringValue=pendingBlockEntities"))
    )
    private void stopTESectionProfiling(CallbackInfo ci)
    {
        if ((World) (Object) (this) instanceof WorldServer)
            CarpetProfiler.end_current_entity_section();
    }
    
    @Inject(method = "updateEntities", at = @At("RETURN"))
    private void stopTEProfiling(CallbackInfo ci)
    {
        if ((World) (Object) (this) instanceof WorldServer)
            CarpetProfiler.end_current_section();
    }
}
