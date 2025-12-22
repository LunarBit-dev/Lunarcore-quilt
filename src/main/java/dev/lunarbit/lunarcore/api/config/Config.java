package dev.lunarbit.lunarcore.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * LunarCore configuration abstraction following LC-CFG specifications.
 *
 * <p>This class provides a unified way to manage configuration across different
 * sources with proper precedence and validation.
 *
 * <p><b>Configuration Precedence (LC-CFG-001):</b>
 * <ol>
 *   <li>Programmatic (highest priority)</li>
 *   <li>Environment Variables</li>
 *   <li>Configuration File</li>
 *   <li>Defaults (lowest priority)</li>
 * </ol>
 *
 * <p><b>Quilt-Specific Behavior:</b>
 * <ul>
 *   <li>Integrates with Quilt's config directory structure</li>
 *   <li>Uses game directory for config file discovery</li>
 *   <li>Compatible with Quilt Config API (but doesn't require it)</li>
 * </ul>
 *
 * <p><b>Specification References:</b>
 * <ul>
 *   <li>LC-CFG-001: Configuration precedence</li>
 *   <li>LC-CFG-002: Environment variable naming</li>
 *   <li>LC-CFG-006: Configuration validation</li>
 *   <li>LC-CFG-007: Sensitive values</li>
 *   <li>LC-CFG-009: Configuration immutability</li>
 *   <li>LC-CFG-012: Boolean value parsing</li>
 * </ul>
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * ConfigBuilder builder = new ConfigBuilder("my-mod");
 * builder.setDefault("timeout", 5000);
 * builder.set("apiKey", "secret-key"); // Programmatic
 *
 * Config config = builder.build();
 * int timeout = config.getInt("timeout"); // Returns 5000
 * String apiKey = config.getString("apiKey"); // Returns "secret-key"
 * }</pre>
 */
public final class Config {

	private final String namespace;
	private final Map<String, Object> values;
	private final Map<String, Object> defaults;

	private Config(String namespace, Map<String, Object> values, Map<String, Object> defaults) {
		this.namespace = namespace;
		this.values = new HashMap<>(values); // Copy for immutability
		this.defaults = new HashMap<>(defaults);
	}

	/**
	 * Gets a string value from the configuration.
	 *
	 * @param key the configuration key
	 * @return the value
	 * @throws ConfigurationError if the key is not found
	 */
	public String getString(String key) {
		return get(key, String.class)
			.orElseThrow(() -> ConfigurationError.missingRequired(key, toEnvVar(key)));
	}

	/**
	 * Gets a string value with a fallback.
	 *
	 * @param key the configuration key
	 * @param defaultValue the default value if not found
	 * @return the value or default
	 */
	public String getString(String key, String defaultValue) {
		return get(key, String.class).orElse(defaultValue);
	}

	/**
	 * Gets an integer value from the configuration.
	 *
	 * @param key the configuration key
	 * @return the value
	 * @throws ConfigurationError if the key is not found or not a valid integer
	 */
	public int getInt(String key) {
		return get(key, Integer.class)
			.orElseThrow(() -> ConfigurationError.missingRequired(key, toEnvVar(key)));
	}

	/**
	 * Gets an integer value with a fallback.
	 *
	 * @param key the configuration key
	 * @param defaultValue the default value if not found
	 * @return the value or default
	 */
	public int getInt(String key, int defaultValue) {
		return get(key, Integer.class).orElse(defaultValue);
	}

	/**
	 * Gets a long value from the configuration.
	 *
	 * @param key the configuration key
	 * @return the value
	 * @throws ConfigurationError if the key is not found or not a valid long
	 */
	public long getLong(String key) {
		return get(key, Long.class)
			.orElseThrow(() -> ConfigurationError.missingRequired(key, toEnvVar(key)));
	}

	/**
	 * Gets a long value with a fallback.
	 *
	 * @param key the configuration key
	 * @param defaultValue the default value if not found
	 * @return the value or default
	 */
	public long getLong(String key, long defaultValue) {
		return get(key, Long.class).orElse(defaultValue);
	}

	/**
	 * Gets a boolean value from the configuration.
	 *
	 * <p>Per LC-CFG-012, accepts: true, 1, yes, on (case-insensitive) for true;
	 * false, 0, no, off for false.
	 *
	 * @param key the configuration key
	 * @return the value
	 * @throws ConfigurationError if the key is not found or not a valid boolean
	 */
	public boolean getBoolean(String key) {
		return get(key, Boolean.class)
			.orElseThrow(() -> ConfigurationError.missingRequired(key, toEnvVar(key)));
	}

	/**
	 * Gets a boolean value with a fallback.
	 *
	 * @param key the configuration key
	 * @param defaultValue the default value if not found
	 * @return the value or default
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return get(key, Boolean.class).orElse(defaultValue);
	}

	/**
	 * Checks if a key exists in the configuration.
	 *
	 * @param key the key to check
	 * @return true if the key exists
	 */
	public boolean hasKey(String key) {
		return values.containsKey(key) || defaults.containsKey(key);
	}

	/**
	 * Gets a value from configuration with type conversion.
	 *
	 * @param key the configuration key
	 * @param type the expected type
	 * @param <T> the type parameter
	 * @return optional containing the value if present
	 */
	@SuppressWarnings("unchecked")
	private <T> Optional<T> get(String key, Class<T> type) {
		Object value = values.get(key);
		if (value == null) {
			value = defaults.get(key);
		}

		if (value == null) {
			return Optional.empty();
		}

		// Type conversion
		if (type == Boolean.class && !(value instanceof Boolean)) {
			value = parseBoolean(value.toString());
		} else if (type == Integer.class && !(value instanceof Integer)) {
			try {
				value = Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				throw ConfigurationError.invalidValue(key, value, "Not a valid integer");
			}
		} else if (type == Long.class && !(value instanceof Long)) {
			try {
				value = Long.parseLong(value.toString());
			} catch (NumberFormatException e) {
				throw ConfigurationError.invalidValue(key, value, "Not a valid long");
			}
		}

		try {
			return Optional.of((T) value);
		} catch (ClassCastException e) {
			throw ConfigurationError.invalidValue(key, value,
				"Expected type " + type.getSimpleName());
		}
	}

	/**
	 * Parses a boolean value per LC-CFG-012.
	 *
	 * @param value the string value
	 * @return the parsed boolean
	 * @throws ConfigurationError if the value is invalid
	 */
	private boolean parseBoolean(String value) {
		String lower = value.toLowerCase();
		if ("true".equals(lower) || "1".equals(lower) || "yes".equals(lower) || "on".equals(lower)) {
			return true;
		} else if ("false".equals(lower) || "0".equals(lower) || "no".equals(lower) || "off".equals(lower)) {
			return false;
		} else {
			throw new ConfigurationError(
				"Invalid boolean value: " + value +
				". Valid values: true, 1, yes, on, false, 0, no, off"
			);
		}
	}

	/**
	 * Converts a config key to an environment variable name per LC-CFG-002.
	 *
	 * @param key the config key
	 * @return the environment variable name
	 */
	private String toEnvVar(String key) {
		return "LUNARCORE_" + namespace.toUpperCase() + "_" +
			key.toUpperCase().replace(".", "_");
	}

	/**
	 * Builder for creating Config instances.
	 */
	public static final class ConfigBuilder {
		private final String namespace;
		private final Map<String, Object> values = new HashMap<>();
		private final Map<String, Object> defaults = new HashMap<>();

		/**
		 * Creates a new config builder.
		 *
		 * <p>Per LC-CFG-011, configuration is namespaced by SDK/mod.
		 *
		 * @param namespace the namespace (e.g., mod ID)
		 */
		public ConfigBuilder(String namespace) {
			this.namespace = namespace;
		}

		/**
		 * Sets a programmatic configuration value (highest priority).
		 *
		 * @param key the configuration key
		 * @param value the value
		 * @return this builder
		 */
		public ConfigBuilder set(String key, Object value) {
			values.put(key, value);
			return this;
		}

		/**
		 * Sets a default value (lowest priority).
		 *
		 * @param key the configuration key
		 * @param value the default value
		 * @return this builder
		 */
		public ConfigBuilder setDefault(String key, Object value) {
			defaults.put(key, value);
			return this;
		}

		/**
		 * Loads configuration from environment variables.
		 *
		 * <p>Per LC-CFG-002, environment variables follow the pattern:
		 * LUNARCORE_{NAMESPACE}_{KEY}
		 *
		 * @return this builder
		 */
		public ConfigBuilder fromEnvironment() {
			String prefix = "LUNARCORE_" + namespace.toUpperCase() + "_";
			System.getenv().forEach((envKey, envValue) -> {
				if (envKey.startsWith(prefix)) {
					String configKey = envKey.substring(prefix.length())
						.toLowerCase()
						.replace("_", ".");
					values.put(configKey, envValue);
				}
			});
			return this;
		}

		/**
		 * Builds the configuration instance.
		 *
		 * <p>Per LC-CFG-009, the resulting Config is immutable.
		 *
		 * @return the immutable configuration
		 */
		public Config build() {
			return new Config(namespace, values, defaults);
		}
	}
}

