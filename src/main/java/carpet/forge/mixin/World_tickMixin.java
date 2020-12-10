package carpet.forge.mixin;

import carpet.forge.helper.TickSpeed;
import carpet.forge.utils.CarpetProfiler;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@SuppressWarnings("ConstantConditions")
@Mixin(World.class)
public abstract class World_tickMixin
{
    @Shadow @Final public WorldProvider provider;
    
    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
    private void stopEntityProcessing(World world, Entity entity)
    {
        if (TickSpeed.process_entities) world.updateEntity(entity);
    }
    
    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ITickable;update()V"))
    private void stopTileEntityProcessing(ITickable tickable)
    {
        if (TickSpeed.process_entities) tickable.update();
    }
    
    @Inject(
            method = "updateEntities",
            at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V")
    )
    private void startEntitiesProfiling(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
        {
            String world_name = this.provider.getDimensionType().getName();
            CarpetProfiler.start_section(world_name, "entities");
        }
    }
    
    @Inject(
            method = "updateEntities",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/entity/Entity;getRidingEntity()Lnet/minecraft/entity/Entity;"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void startEntityProfiling(CallbackInfo ci, int i1, Entity entity2)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
        {
            String world_name = this.provider.getDimensionType().getName();
            CarpetProfiler.start_entity_section(world_name, entity2);
        }
    }
    
    @Inject(
            method = "updateEntities",
            at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/profiler/Profiler;endSection()V"),
            slice = @Slice(from = @At(value = "INVOKE", ordinal = 1,
                    target = "Lnet/minecraft/world/World;onEntityRemoved(Lnet/minecraft/entity/Entity;)V"))
    )
    private void stopEntityProfiling(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
            CarpetProfiler.end_current_entity_section();
    }
    
    @Inject(
            method = "updateEntities",
            at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"),
            slice = @Slice(from = @At(value = "INVOKE", ordinal = 1,
                    target = "Lnet/minecraft/world/World;onEntityRemoved(Lnet/minecraft/entity/Entity;)V"))
    )
    private void stopEntitiesProfilingAndStartTEProfiling(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
        {
            CarpetProfiler.end_current_section();
            String world_name = this.provider.getDimensionType().getName();
            CarpetProfiler.start_section(world_name, "tileentities");
        }
    }
    
    @Redirect(
            method = "updateEntities",
            at = @At(value = "INVOKE", ordinal = 0, remap = false,
                    target = "Ljava/util/Iterator;next()Ljava/lang/Object;"),
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/world/World;tickableTileEntities:Ljava/util/List;", ordinal = 1),
                    to = @At(value = "INVOKE",
                            target = "Lnet/minecraft/tileentity/TileEntity;hasWorld()Z"))
    )
    private Object startTESectionProfiling(Iterator<TileEntity> iter)
    {
        TileEntity tileEntity = iter.next();
        
        if ((World)((Object)(this)) instanceof WorldServer)
            CarpetProfiler.start_tileentity_section(this.provider.getDimensionType().getName(), tileEntity);
        return tileEntity;
    }
    
    @Inject(
            method = "updateEntities", at = @At(value = "JUMP", opcode = Opcodes.GOTO, shift = At.Shift.BEFORE),
            slice = @Slice(
                    from = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/chunk/Chunk;removeTileEntity(Lnet/minecraft/util/math/BlockPos;)V"),
                    to = @At(value = "CONSTANT", args = "stringValue=pendingBlockEntities"))
    )
    private void stopTESectionProfiling(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
            CarpetProfiler.end_current_entity_section();
    }
    
    @Inject(
            method = "updateEntities",
            at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/profiler/Profiler;endSection()V"),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;notifyBlockUpdate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;I)V"))
    )
    private void stopTEProfiling(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
            CarpetProfiler.end_current_section();
    }
}
