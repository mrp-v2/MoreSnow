package mrp_v2.moresnow;

import mrp_v2.moresnow.server.Config;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mod(MoreSnow.ID)
public class MoreSnow {
    public static final String ID = "more" + "snow";
    public static final String DISPLAY_NAME = "More Snow";

    public MoreSnow() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
    }

    public static void snowTick(ServerLevel world, boolean isPrecipitating, Biome biome, BlockPos pos) {
        if (isPrecipitating) {
            Pair<BlockState, BlockPos> result = doesSnowGenerate(biome, pos, world);
            if (result != null) {
                world.setBlockAndUpdate(result.getRight(), result.getLeft());
            }
        }
    }

    /**
     * See {@link Biome#shouldSnow(LevelReader, BlockPos)}}.
     */
    @Nullable private static Pair<BlockState, BlockPos> doesSnowGenerate(Biome biome, BlockPos pos, LevelReader world)
    {
        if (biome.warmEnoughToRain(pos))
        {
            return null;
        } else
        {
            Pair<Integer, BlockPos> result = getSnowHeight(world, pos);
            if (result == null) {
                return null;
            }
            if (result.getLeft() < Config.SERVER.getSnowAccumulationLimit()) {
                BlockState state = world.getBlockState(result.getRight());
                if (state.is(Blocks.SNOW)) {
                    int layers = state.getValue(SnowLayerBlock.LAYERS);
                    state = state.setValue(SnowLayerBlock.LAYERS, layers + 1);
                    return Pair.of(state, result.getRight());
                } else {
                    return Pair.of(Blocks.SNOW.defaultBlockState(), result.getRight());
                }
            }
        }
        return null;
    }

    /**
     * Calculates the number of snow layers at a spot in the world.
     *
     * @return A pair representing the number of snow layers and the next block to add snow layers to.
     */
    @Nullable
    private static Pair<Integer, BlockPos> getSnowHeight(LevelReader world, BlockPos pos) {
        Predicate<BlockPos> posTest = (testPos) -> testPos.getY() >= world.getMinBuildHeight() && testPos.getY() < world.getMaxBuildHeight() &&
                world.getBrightness(LightLayer.BLOCK, testPos) < 10;
        if (!posTest.test(pos)) {
            return null;
        }
        BlockState state = world.getBlockState(pos);
        int blockLayers;
        if (state.is(Blocks.SNOW)) {
            blockLayers = state.getValue(SnowLayerBlock.LAYERS);
        } else {
            return null;
        }
        int totalLayers = blockLayers;
        while (blockLayers == 8) {
            pos = pos.above();
            if (!posTest.test(pos)) {
                return null;
            }
            state = world.getBlockState(pos);
            blockLayers = 0;
            if (state.is(Blocks.SNOW)) {
                blockLayers = state.getValue(SnowLayerBlock.LAYERS);
            }
            totalLayers += blockLayers;
        }
        return Pair.of(totalLayers, pos);
    }
}
