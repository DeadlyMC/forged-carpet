package carpet.forge.mixin;

import carpet.forge.CarpetSettings;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityHusk.class)
public abstract class MixinEntityHusk extends EntityZombie
{
    public MixinEntityHusk(World worldIn)
    {
        super(worldIn);
    }
    
    @Redirect(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntityZombie;getCanSpawnHere()Z"))
    private boolean ifSpawnHusks(EntityZombie entityZombie)
    {
        if (CarpetSettings.huskSpawningInTemples)
        {
            return super.getCanSpawnHere() && (this.world.canSeeSky(new BlockPos(this)) || ((WorldServer) this.world).getChunkProvider().isInsideStructure(this.world, "Temple", new BlockPos(this)));
        }
        else
        {
            return super.getCanSpawnHere() && this.world.canSeeSky(new BlockPos(this));
        }
    }
    
    @Redirect(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;canSeeSky(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean cancelCanSeeSky(World world, BlockPos pos)
    {
        return false;
    }
}
