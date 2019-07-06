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
public abstract class EntityHuskMixin extends EntityZombie
{
    public EntityHuskMixin(World worldIn)
    {
        super(worldIn);
    }
    
    @Redirect(
            method = "getCanSpawnHere",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;canSeeSky(Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean onGetCanSpawnHere(World world, BlockPos pos)
    {
        if (CarpetSettings.huskSpawningInTemples)
        {
            return (this.world.canSeeSky(new BlockPos((EntityHusk) (Object) this))) || ((WorldServer) this.world).getChunkProvider().isInsideStructure(this.world, "Temple", new BlockPos((EntityHusk) (Object) this));
        }
        else
        {
            return this.world.canSeeSky(new BlockPos((EntityHusk) (Object) this));
        }
    }
}
