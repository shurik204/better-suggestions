package me.shurik.bettersuggestions;

import com.google.common.collect.Lists;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shurik.bettersuggestions.config.ConfigStore;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModConstants {
    public static final String MOD_ID = "better-suggestions";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConfigStore CONFIG = AutoConfig.register(ConfigStore.class, JanksonConfigSerializer::new).getConfig();
    public static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static void loadConfig() {
        // Update if needed
        if (ModConstants.CONFIG.version < 1) {
            ModConstants.CONFIG.version = 1;
            ModConstants.CONFIG.prioritizedSuggestions = Lists.newArrayList("minecraft:barrier", "data", "tellraw");
            AutoConfig.getConfigHolder(ConfigStore.class).save();
        }
    }
}