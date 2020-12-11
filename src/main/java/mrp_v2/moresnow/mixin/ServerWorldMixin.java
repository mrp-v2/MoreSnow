package mrp_v2.moresnow.mixin;

import mrp_v2.moresnow.MoreSnow;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerWorld.class) public abstract class ServerWorldMixin
{
    @Inject(method = "tickEnvironment", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 6),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void tickEnvironment(Chunk chunkIn, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkpos, boolean flag,
            int i, int j, IProfiler iprofiler, BlockPos blockpos2, BlockPos blockpos3, Biome biome)
    {
        MoreSnow.snowTick((ServerWorld) (Object) this, flag, biome, blockpos2);
    }
}
