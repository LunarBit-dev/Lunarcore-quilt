package dev.lunarbit.lunarcore;

import dev.lunarbit.lunarcore.api.config.Config;
import dev.lunarbit.lunarcore.api.lifecycle.ModLifecycleAdapter;
import dev.lunarbit.lunarcore.api.logging.LunarLogger;
import dev.lunarbit.lunarcore.api.metadata.MetadataHelper;
import org.quiltmc.loader.api.ModContainer;

/**
 * LunarCore Quilt SDK initialization.
 *
 * <p>This class demonstrates proper usage of LunarCore SDK components:
 * <ul>
 *   <li>Lifecycle management via ModLifecycleAdapter</li>
 *   <li>Logging via LunarLogger</li>
 *   <li>Configuration via Config</li>
 *   <li>Metadata access via MetadataHelper</li>
 * </ul>
 *
 * <p><b>This is a Quilt-native implementation.</b>
 * It does NOT share binaries with LunarCore Fabric, only concepts and specifications.
 */
public class LunarCoreQuilt extends ModLifecycleAdapter {

	private static final LunarLogger LOGGER = LunarLogger.getLogger("Core");
	private static Config config;

	@Override
	public void initialize(ModContainer container) {
		// Log initialization start
		LOGGER.info("Initializing LunarCore Quilt SDK");

		// Display mod metadata
		String modId = MetadataHelper.getModId(container);
		String modName = MetadataHelper.getModName(container);
		String version = MetadataHelper.getModVersionString(container);

		LOGGER.info("Mod ID: " + modId);
		LOGGER.info("Mod Name: " + modName);
		LOGGER.info("Version: " + version);

		// Initialize configuration
		Config.ConfigBuilder configBuilder = new Config.ConfigBuilder(modId);

		// Set defaults per LC-CFG-008
		configBuilder.setDefault("log.level", "INFO");
		configBuilder.setDefault("timeout", 30000);
		configBuilder.setDefault("max.retries", 3);

		// Load from environment
		configBuilder.fromEnvironment();

		// Build immutable config
		config = configBuilder.build();

		// Configure logger from config
		String logLevel = config.getString("log.level", "INFO");
		try {
			LOGGER.setLevel(dev.lunarbit.lunarcore.api.logging.LogLevel.parse(logLevel));
			LOGGER.info("Log level set to: " + logLevel);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Invalid log level '" + logLevel + "', using default INFO");
		}

		// Log configuration values (demonstrate redaction)
		LOGGER.debug("Configuration loaded:");
		LOGGER.debug("  Timeout: " + config.getInt("timeout"));
		LOGGER.debug("  Max Retries: " + config.getInt("max.retries"));

		// Demonstrate sensitive data redaction
		LOGGER.debug("API Key: sk-1234567890abcdef"); // Will be redacted to sk-12...[REDACTED]
		LOGGER.debug("Password: mySecretPassword"); // Will be redacted to [REDACTED]

		// Check for optional mods
		if (MetadataHelper.isModLoaded("quilted_fabric_api")) {
			LOGGER.info("Quilted Fabric API detected");
		}

		LOGGER.info("LunarCore Quilt SDK initialized successfully");
	}

	@Override
	public void shutdown() {
		LOGGER.info("Shutting down LunarCore Quilt SDK");
	}

	/**
	 * Gets the global configuration instance.
	 *
	 * @return the configuration
	 * @throws IllegalStateException if not yet initialized
	 */
	public static Config getConfig() {
		if (config == null) {
			throw new IllegalStateException("LunarCore not yet initialized");
		}
		return config;
	}
}

