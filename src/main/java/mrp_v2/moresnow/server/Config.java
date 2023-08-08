package mrp_v2.moresnow.server;

import mrp_v2.moresnow.MoreSnow;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

@Mod.EventBusSubscriber(modid = MoreSnow.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final Config SERVER;
    public static final ForgeConfigSpec serverSpec;
    public static final String TRANSLATION_KEY = MoreSnow.ID + ".config.gui";

    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    private final ForgeConfigSpec.IntValue snowAccumulationLimit;
    private int sal;

    Config(final ForgeConfigSpec.Builder builder) {
        builder.comment(" Server configuration settings").push("server");
        final String sal = "snowAccumulationLimit";
        snowAccumulationLimit = builder.comment(" How many layers of snow are allowed to accumulate.")
                .translation(TRANSLATION_KEY + sal).defineInRange(sal, 8, 1, 2048);
        builder.pop();
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        LogManager.getLogger().debug(MoreSnow.DISPLAY_NAME + " config just got changed on the file system!");
        SERVER.updateValues();
    }

    private void updateValues() {
        this.sal = this.snowAccumulationLimit.get();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        LogManager.getLogger().debug("Loaded " + MoreSnow.DISPLAY_NAME + " config file {}",
                configEvent.getConfig().getFileName());
        SERVER.updateValues();
    }

    public int getSnowAccumulationLimit() {
        return this.sal;
    }
}
