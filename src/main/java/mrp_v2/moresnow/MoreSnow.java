package mrp_v2.moresnow;

import mrp_v2.moresnow.server.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mod(MoreSnow.ID) public class MoreSnow
{
    public static final String ID = "more" + "snow";
    public static final String DISPLAY_NAME = "More Snow";

    public MoreSnow()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
    }

    public static void snowTick(ServerWorld world, boolean isPrecipitating, Biome biome, BlockPos pos)
    {
        if (isPrecipitating)
        {
            Pair<BlockState, BlockPos> result = doesSnowGenerate(biome, pos, world);
            if (result != null)
            {
                world.setBlockState(result.getRight(), result.getLeft());
            }
        }
    }

    /**
     * See {@link Biome#doesSnowGenerate(IWorldReader, BlockPos)}.
     * Modified to return true if there is already some snow instead of true if there is no snow.
     */
    @Nullable private static Pair<BlockState, BlockPos> doesSnowGenerate(Biome biome, BlockPos pos, IWorldReader world)
    {
        if (biome.getTemperature(pos) >= 0.15F)
        {
            return null;
        } else
        {
            Pair<Integer, BlockPos> result = getSnowHeight(world, pos);
            if (result == null)
            {
                return null;
            }
            if (result.getLeft() < Config.SERVER.getSnowAccumulationLimit())
            {
                BlockState state = world.getBlockState(result.getRight());
                if (state.isIn(Blocks.SNOW))
                {
                    int layers = state.get(SnowBlock.LAYERS);
                    state = state.with(SnowBlock.LAYERS, layers + 1);
                    return Pair.of(state, result.getRight());
                } else
                {
                    return Pair.of(Blocks.SNOW.getDefaultState(), result.getRight());
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
    @Nullable private static Pair<Integer, BlockPos> getSnowHeight(IWorldReader world, BlockPos pos)
    {
        Predicate<BlockPos> posTest = (testPos) -> testPos.getY() >= 0 && testPos.getY() < 256 &&
                world.getLightFor(LightType.BLOCK, testPos) < 10;
        if (!posTest.test(pos))
        {
            return null;
        }
        BlockState state = world.getBlockState(pos);
        int blockLayers;
        if (state.isIn(Blocks.SNOW))
        {
            blockLayers = state.get(SnowBlock.LAYERS);
        } else
        {
            return null;
        }
        int totalLayers = blockLayers;
        while (blockLayers == 8)
        {
            pos = pos.up();
            if (!posTest.test(pos))
            {
                return null;
            }
            state = world.getBlockState(pos);
            blockLayers = 0;
            if (state.isIn(Blocks.SNOW))
            {
                blockLayers = state.get(SnowBlock.LAYERS);
            }
            totalLayers += blockLayers;
        }
        return Pair.of(totalLayers, pos);
    }
}
