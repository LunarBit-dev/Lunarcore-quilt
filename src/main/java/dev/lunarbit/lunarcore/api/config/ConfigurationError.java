package dev.lunarbit.lunarcore.api.config;

/**
 * Exception thrown when configuration is invalid or missing required values.
 *
 * <p>This exception provides detailed error messages that guide users on how
 * to fix configuration issues per LC-CFG-005.
 *
 * <p><b>Specification References:</b>
 * <ul>
 *   <li>LC-CFG-005: Required vs optional configuration</li>
 *   <li>LC-CFG-006: Configuration validation</li>
 *   <li>LC-NAM-012: Error naming (ends with "Error")</li>
 * </ul>
 */
public class ConfigurationError extends RuntimeException {

	/**
	 * Creates a configuration error with a message.
	 *
	 * @param message the error message
	 */
	public ConfigurationError(String message) {
		super(message);
	}

	/**
	 * Creates a configuration error with a message and cause.
	 *
	 * @param message the error message
	 * @param cause the underlying cause
	 */
	public ConfigurationError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a configuration error for a missing required field.
	 *
	 * <p>Per LC-CFG-005, this provides clear guidance on how to provide the value.
	 *
	 * @param fieldPath the path to the missing field (e.g., "api.key")
	 * @param envVar the environment variable name
	 * @return a configuration error with helpful message
	 */
	public static ConfigurationError missingRequired(String fieldPath, String envVar) {
		return new ConfigurationError(String.format(
			"Configuration Error: Missing required field '%s'%n%n" +
			"Provide via:%n" +
			"  - Environment variable: %s%n" +
			"  - Config file: { \"%s\": \"your-value\" }%n" +
			"  - Programmatic: config.set(\"%s\", \"your-value\")",
			fieldPath, envVar, fieldPath.replace(".", "\": { \""), fieldPath
		));
	}

	/**
	 * Creates a configuration error for an invalid value.
	 *
	 * @param fieldPath the path to the field
	 * @param value the invalid value
	 * @param reason the reason it's invalid
	 * @return a configuration error
	 */
	public static ConfigurationError invalidValue(String fieldPath, Object value, String reason) {
		return new ConfigurationError(String.format(
			"Configuration Error: Invalid value for '%s': %s%n" +
			"Reason: %s",
			fieldPath, value, reason
		));
	}
}

