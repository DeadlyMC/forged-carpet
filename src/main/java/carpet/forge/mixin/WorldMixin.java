package carpet.forge.mixin;

import carpet.forge.utils.CarpetProfiler;
import carpet.forge.utils.mixininterfaces.IWorld;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld
{
    
    @Shadow
    @Final
    public WorldProvider provider;
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/profiler/Profiler;endSection()V",
            ordinal = 0))
    private void startEntitiesProfiling(CallbackInfo ci)
    {
        if ((World) ((Object) (this)) instanceof WorldServer)
        {
            String world_name = this.provider.getDimensionType().getName();
            CarpetProfiler.start_section(world_name, "entities");
        }
    }
    
    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0, remap = false),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;loadedEntityList:Ljava/util/List;", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRidingEntity()Lnet/minecraft/entity/Entity;", ordinal = 0)))
    private Object startEntityProfiling(List<Entity> entityList, int index)
    {
        Entity entity = entityList.get(index);
        
        if ((World) ((Object) (this)) instanceof WorldServer)
        {
            CarpetProfiler.start_entity_section(this.provider.getDimensionType().getName(), entity);
        }
        
        return entity;
    }
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;onEntityRemoved(Lnet/minecraft/entity/Entity;)V", ordinal = 1),
                    to = @At(value = "CONSTANT", args = "stringValue=blockEntities", ordinal = 0)))
    private void stopEntityProfiling(CallbackInfo ci)
    {
        if ((World) ((Object) (this)) instanceof WorldServer)
        {
            CarpetProfiler.end_current_entity_section();
        }
    }
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=remove", ordinal = 1),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/World;tileEntitiesToBeRemoved:Ljava/util/List;", ordinal = 0)))
    private void stopEntitiesProfilingStartTEProfiling(CallbackInfo ci)
    {
        if ((World) ((Object) (this)) instanceof WorldServer)
        {
            CarpetProfiler.end_current_section();
            String world_name = this.provider.getDimensionType().getName();
            CarpetProfiler.start_section(world_name, "tileentities");
        }
    }
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/profiler/Profiler;endSection()V",
            ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyBlockUpdate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;I)V"),
            to = @At(value = "TAIL")))
    private void stopTEProfiling(CallbackInfo ci)
    {
        if ((World) ((Object) (this)) instanceof WorldServer)
        {
            CarpetProfiler.end_current_section();
        }
    }
    
    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 0, remap = false),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;tickableTileEntities:Ljava/util/List;", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;hasWorld()Z")))
    private Object startTESectionProfiling(Iterator<TileEntity> iter)
    {
        TileEntity tileEntity = iter.next();
        
        if ((World) ((Object) (this)) instanceof WorldServer)
        {
            CarpetProfiler.start_tileentity_section(this.provider.getDimensionType().getName(), tileEntity);
        }
        
        return tileEntity;
    }
    
    @Inject(method = "updateEntities", at = @At(value = "JUMP", opcode = Opcodes.GOTO, shift = At.Shift.BEFORE),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;removeTileEntity(Lnet/minecraft/util/math/BlockPos;)V"),
                    to = @At(value = "CONSTANT", args = "stringValue=pendingBlockEntities")))
    private void stopTESectionProfiling(CallbackInfo ci)
    {
        if ((World) ((Object) (this)) instanceof WorldServer)
        {
            CarpetProfiler.end_current_entity_section();
        }
    }
}
