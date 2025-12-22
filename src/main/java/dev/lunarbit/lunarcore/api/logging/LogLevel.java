package dev.lunarbit.lunarcore.api.logging;

/**
 * LunarCore log levels following LC-LOG-001 specification.
 *
 * <p>All LunarCore SDKs MUST support exactly these five levels with these values.
 *
 * <p><b>Specification:</b> LC-LOG-001
 */
public enum LogLevel {
	/**
	 * System failures requiring immediate attention.
	 * Value: 50
	 *
	 * <p>Example: Connection refused, authentication failed
	 */
	ERROR(50),

	/**
	 * Issues that don't stop execution.
	 * Value: 40
	 *
	 * <p>Example: Deprecated API usage, retry attempts
	 */
	WARN(40),

	/**
	 * General operational messages.
	 * Value: 30
	 *
	 * <p>Example: Service started, request completed
	 *
	 * <p><b>Default Level:</b> This is the default log level per LC-LOG-002
	 */
	INFO(30),

	/**
	 * Detailed diagnostic information.
	 * Value: 20
	 *
	 * <p>Example: Request payloads, state transitions
	 */
	DEBUG(20),

	/**
	 * Very detailed flow information.
	 * Value: 10
	 *
	 * <p>Example: Function entry/exit, variable values
	 */
	TRACE(10);

	private final int value;

	LogLevel(int value) {
		this.value = value;
	}

	/**
	 * Gets the numeric value of this log level.
	 *
	 * @return the numeric value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Checks if this level is enabled given a threshold level.
	 *
	 * @param threshold the threshold level
	 * @return true if this level should be logged
	 */
	public boolean isEnabled(LogLevel threshold) {
		return this.value >= threshold.value;
	}

	/**
	 * Parses a log level from a string.
	 *
	 * @param level the level string (case-insensitive)
	 * @return the parsed log level
	 * @throws IllegalArgumentException if the level is invalid
	 */
	public static LogLevel parse(String level) {
		try {
			return LogLevel.valueOf(level.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
				"Invalid log level: " + level + ". Valid levels: ERROR, WARN, INFO, DEBUG, TRACE"
			);
		}
	}
}

