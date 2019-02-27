package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityHusk.class)
public abstract class MixinEntityHusk extends EntityZombie
{
    public MixinEntityHusk(World worldIn)
    {
        super(worldIn);
    }
    
    @Redirect(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;canSeeSky(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean cancelCanSeeSky(World world, BlockPos pos)
    {
        return false;
    }
    
    @Redirect(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntityZombie;getCanSpawnHere()Z"))
    private boolean cancelSuperGetCanSpawnHere(EntityZombie entityZombie)
    {
        return false;
    }
    
    @Inject(method = "getCanSpawnHere", at = @At(value = "HEAD"), cancellable = true)
    private void newGetCanSpawnHere(CallbackInfoReturnable<Boolean> cir)
    {
        if (CarpetSettings.huskSpawningInTemples)
        {
            cir.setReturnValue(super.getCanSpawnHere() && (this.world.canSeeSky(new BlockPos(this)) || ((WorldServer) this.world).getChunkProvider().isInsideStructure(this.world, "Temple", new BlockPos(this))));
        }
        else
        {
            cir.setReturnValue(super.getCanSpawnHere() && this.world.canSeeSky(new BlockPos(this)));
        }
    }
}
