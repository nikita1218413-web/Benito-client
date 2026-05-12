package ru.benito.visuals;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный (common) entrypoint мода. На клиенте почти вся логика живёт в {@link BenitoClient}.
 */
public final class BenitoMod implements ModInitializer {

    public static final String MOD_ID = "benito_visuals";
    public static final String MOD_NAME = "Benito Visuals";
    public static final String MOD_VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("[Benito] Инициализация мода {} v{}", MOD_NAME, MOD_VERSION);
    }
}
