package mrp_v2.moresnow.mixin;

import mrp_v2.moresnow.MoreSnow;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.level.ServerLevel;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerLevel.class) public abstract class ServerWorldMixin
{
    @Inject(method = "tickChunk", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 6),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void tickEnvironment(LevelChunk chunkIn, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkpos, boolean flag,
            int i, int j, ProfilerFiller profilerfiller, BlockPos blockpos2, BlockPos blockpos3, Biome biome)
    {
        MoreSnow.snowTick((ServerLevel) (Object) this, flag, biome, blockpos2);
    }
}
