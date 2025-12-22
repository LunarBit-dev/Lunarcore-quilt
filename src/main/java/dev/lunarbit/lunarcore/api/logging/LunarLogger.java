package dev.lunarbit.lunarcore.api.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * LunarCore logger wrapper following LC-LOG specifications.
 *
 * <p>This class wraps SLF4J (used by Quilt) to provide LunarCore-compliant
 * logging with automatic formatting, sensitive data redaction, and component naming.
 *
 * <p><b>Quilt-Specific Behavior:</b>
 * <ul>
 *   <li>Uses SLF4J which is the standard for Quilt mods</li>
 *   <li>Integrates with Quilt's logging infrastructure</li>
 *   <li>Logs appear in the standard Minecraft log</li>
 * </ul>
 *
 * <p><b>Specification References:</b>
 * <ul>
 *   <li>LC-LOG-001: Log levels</li>
 *   <li>LC-LOG-002: Default log level (INFO)</li>
 *   <li>LC-LOG-003: Log format</li>
 *   <li>LC-LOG-004: Timestamp format</li>
 *   <li>LC-LOG-005: Component naming (LunarCore.* pattern)</li>
 *   <li>LC-LOG-007: Sensitive data redaction</li>
 *   <li>LC-LOG-010: Performance (lazy evaluation)</li>
 * </ul>
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * LunarLogger logger = LunarLogger.getLogger("Auth");
 * logger.info("User authenticated successfully");
 * logger.debug(() -> "Expensive operation: " + computeExpensiveString());
 * }</pre>
 */
public final class LunarLogger {

	// LC-LOG-004: ISO 8601 format with millisecond precision
	private static final DateTimeFormatter TIMESTAMP_FORMAT =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

	// LC-LOG-007: Sensitive data patterns
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"(?i)(password|passwd|pwd)\\s*[:=]\\s*[\"']?([^\"'\\s]+)",
		Pattern.CASE_INSENSITIVE
	);
	private static final Pattern API_KEY_PATTERN = Pattern.compile(
		"(?i)(api[_-]?key|apikey|key)\\s*[:=]\\s*[\"']?([a-zA-Z0-9_-]{4})([a-zA-Z0-9_-]+)",
		Pattern.CASE_INSENSITIVE
	);
	private static final Pattern TOKEN_PATTERN = Pattern.compile(
		"(?i)(token|bearer)\\s*[:=]\\s*[\"']?([a-zA-Z0-9_-]{8})([a-zA-Z0-9_-]+)",
		Pattern.CASE_INSENSITIVE
	);

	private final Logger slf4jLogger;
	private final String component;
	private LogLevel threshold = LogLevel.INFO; // LC-LOG-002: Default level

	private LunarLogger(String component) {
		// LC-LOG-005: Component naming with LunarCore. prefix
		this.component = "LunarCore." + component;
		this.slf4jLogger = LoggerFactory.getLogger(this.component);
	}

	/**
	 * Gets a LunarCore logger for the specified component.
	 *
	 * <p><b>Component Naming:</b> Per LC-LOG-005, the component will be prefixed
	 * with "LunarCore." automatically. Use PascalCase for subcomponents.
	 *
	 * @param component the component name (without "LunarCore." prefix)
	 * @return a logger instance
	 */
	public static LunarLogger getLogger(String component) {
		return new LunarLogger(component);
	}

	/**
	 * Sets the log level threshold for this logger.
	 *
	 * <p><b>Note:</b> This only affects LunarCore-specific filtering.
	 * The underlying SLF4J logger is configured separately.
	 *
	 * @param level the minimum level to log
	 */
	public void setLevel(LogLevel level) {
		this.threshold = level;
	}

	/**
	 * Gets the current log level threshold.
	 *
	 * @return the current threshold
	 */
	public LogLevel getLevel() {
		return threshold;
	}

	// LC-LOG-010: Check methods for lazy evaluation

	public boolean isErrorEnabled() {
		return LogLevel.ERROR.isEnabled(threshold);
	}

	public boolean isWarnEnabled() {
		return LogLevel.WARN.isEnabled(threshold);
	}

	public boolean isInfoEnabled() {
		return LogLevel.INFO.isEnabled(threshold);
	}

	public boolean isDebugEnabled() {
		return LogLevel.DEBUG.isEnabled(threshold);
	}

	public boolean isTraceEnabled() {
		return LogLevel.TRACE.isEnabled(threshold);
	}

	// ERROR level logging

	public void error(String message) {
		if (isErrorEnabled()) {
			slf4jLogger.error(formatMessage(LogLevel.ERROR, message));
		}
	}

	public void error(String message, Throwable throwable) {
		if (isErrorEnabled()) {
			slf4jLogger.error(formatMessage(LogLevel.ERROR, message), throwable);
		}
	}

	public void error(Supplier<String> messageSupplier) {
		if (isErrorEnabled()) {
			error(messageSupplier.get());
		}
	}

	// WARN level logging

	public void warn(String message) {
		if (isWarnEnabled()) {
			slf4jLogger.warn(formatMessage(LogLevel.WARN, message));
		}
	}

	public void warn(String message, Throwable throwable) {
		if (isWarnEnabled()) {
			slf4jLogger.warn(formatMessage(LogLevel.WARN, message), throwable);
		}
	}

	public void warn(Supplier<String> messageSupplier) {
		if (isWarnEnabled()) {
			warn(messageSupplier.get());
		}
	}

	// INFO level logging

	public void info(String message) {
		if (isInfoEnabled()) {
			slf4jLogger.info(formatMessage(LogLevel.INFO, message));
		}
	}

	public void info(String message, Throwable throwable) {
		if (isInfoEnabled()) {
			slf4jLogger.info(formatMessage(LogLevel.INFO, message), throwable);
		}
	}

	public void info(Supplier<String> messageSupplier) {
		if (isInfoEnabled()) {
			info(messageSupplier.get());
		}
	}

	// DEBUG level logging

	public void debug(String message) {
		if (isDebugEnabled()) {
			slf4jLogger.debug(formatMessage(LogLevel.DEBUG, message));
		}
	}

	public void debug(String message, Throwable throwable) {
		if (isDebugEnabled()) {
			slf4jLogger.debug(formatMessage(LogLevel.DEBUG, message), throwable);
		}
	}

	public void debug(Supplier<String> messageSupplier) {
		if (isDebugEnabled()) {
			debug(messageSupplier.get());
		}
	}

	// TRACE level logging

	public void trace(String message) {
		if (isTraceEnabled()) {
			slf4jLogger.trace(formatMessage(LogLevel.TRACE, message));
		}
	}

	public void trace(String message, Throwable throwable) {
		if (isTraceEnabled()) {
			slf4jLogger.trace(formatMessage(LogLevel.TRACE, message), throwable);
		}
	}

	public void trace(Supplier<String> messageSupplier) {
		if (isTraceEnabled()) {
			trace(messageSupplier.get());
		}
	}

	/**
	 * Formats a log message according to LC-LOG-003 and LC-LOG-004.
	 *
	 * Format: [TIMESTAMP] [LEVEL] [COMPONENT] MESSAGE
	 *
	 * @param level the log level
	 * @param message the message to log
	 * @return formatted message
	 */
	private String formatMessage(LogLevel level, String message) {
		String timestamp = TIMESTAMP_FORMAT.format(Instant.now());
		String redactedMessage = redactSensitiveData(message);
		return String.format("[%s] [%s] [%s] %s",
			timestamp, level.name(), component, redactedMessage);
	}

	/**
	 * Redacts sensitive data from log messages per LC-LOG-007.
	 *
	 * @param message the original message
	 * @return message with sensitive data redacted
	 */
	private String redactSensitiveData(String message) {
		if (message == null) {
			return null;
		}

		String result = message;

		// Redact passwords
		result = PASSWORD_PATTERN.matcher(result).replaceAll("$1: [REDACTED]");

		// Redact API keys (show first 4 chars)
		result = API_KEY_PATTERN.matcher(result).replaceAll("$1: $2...[REDACTED]");

		// Redact tokens (show first 8 chars)
		result = TOKEN_PATTERN.matcher(result).replaceAll("$1: $2...[REDACTED]");

		return result;
	}
}

