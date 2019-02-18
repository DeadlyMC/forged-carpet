package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(Template.class)
public abstract class MixinTemplate
{
    @Redirect(method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", ordinal = 0))
    private boolean replaceSetBlockState1(World worldIn, BlockPos blockpos, IBlockState newState, int flags)
    {
        return worldIn.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 4 | (CarpetSettings.getBool("fillUpdates") ? 0 : 128));
    }

    @Redirect(method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", ordinal = 1))
    private boolean cancelSetBlockState2(World worldIn, BlockPos blockpos, IBlockState iblockstate1, int flags)
    {
        return worldIn.setBlockState(blockpos, iblockstate1, flags | (CarpetSettings.getBool("fillUpdates") ? 0 : 128));
    }

    @Redirect(method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V"))
    private void cancelNotifyNeighbours(World worldIn, BlockPos pos, Block blockType, boolean updateObservers)
    {

    }

    @Inject(method = "addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/ITemplateProcessor;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyNeighborsRespectDebug(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Z)V",
                    shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void newNotifyNeighbours(World worldIn, BlockPos pos, ITemplateProcessor templateProcessor, PlacementSettings placementIn, int flags, CallbackInfo ci, Block block, StructureBoundingBox structureboundingbox, Iterator var8, Template.BlockInfo template$blockinfo, BlockPos blockpos, Template.BlockInfo template$blockinfo1)
    {
        if (CarpetSettings.getBool("fillUpdates"))
        {
            worldIn.notifyNeighborsRespectDebug(blockpos, template$blockinfo1.blockState.getBlock(), false);
        }
    }
}
