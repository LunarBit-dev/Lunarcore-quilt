package dev.lunarbit.lunarcore.api.metadata;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.Version;

import java.util.Collection;
import java.util.Optional;

/**
 * Helper class for accessing Quilt mod metadata in a LunarCore-friendly way.
 *
 * <p>This class provides convenient methods for accessing mod information
 * from Quilt's loader, with additional utilities and null-safety.
 *
 * <p><b>Quilt-Specific Behavior:</b>
 * <ul>
 *   <li>Uses Quilt's ModMetadata system (different structure from Fabric)</li>
 *   <li>Supports Quilt's extended metadata fields</li>
 *   <li>Provides access to Quilt-specific version semantics</li>
 * </ul>
 *
 * <p><b>Specification References:</b>
 * <ul>
 *   <li>LC-NAM-007: Class naming conventions (helper suffix)</li>
 *   <li>LC-NAM-004: Function naming (get/fetch verbs)</li>
 * </ul>
 */
public final class MetadataHelper {

	private MetadataHelper() {
		// Utility class - prevent instantiation
	}

	/**
	 * Gets the mod ID from the container.
	 *
	 * @param container the mod container
	 * @return the mod ID
	 */
	public static String getModId(ModContainer container) {
		return container.metadata().id();
	}

	/**
	 * Gets the mod name from the container.
	 *
	 * @param container the mod container
	 * @return the mod name
	 */
	public static String getModName(ModContainer container) {
		return container.metadata().name();
	}

	/**
	 * Gets the mod version from the container.
	 *
	 * @param container the mod container
	 * @return the mod version
	 */
	public static Version getModVersion(ModContainer container) {
		return container.metadata().version();
	}

	/**
	 * Gets the mod version as a string.
	 *
	 * @param container the mod container
	 * @return the mod version string
	 */
	public static String getModVersionString(ModContainer container) {
		return container.metadata().version().toString();
	}

	/**
	 * Gets the mod description.
	 *
	 * @param container the mod container
	 * @return the mod description, or empty string if not present
	 */
	public static String getDescription(ModContainer container) {
		return container.metadata().description();
	}

	/**
	 * Gets the mod's contributors.
	 *
	 * <p><b>Quilt-Specific:</b> Returns Quilt's contributor data which
	 * may include role information.
	 *
	 * @param container the mod container
	 * @return collection of contributors (specific type varies by Quilt version)
	 */
	public static Collection<?> getContributors(ModContainer container) {
		return container.metadata().contributors();
	}

	/**
	 * Gets a contact information entry.
	 *
	 * @param container the mod container
	 * @param key the contact key (e.g., "homepage", "issues", "sources")
	 * @return the contact value, or empty if not present
	 */
	public static Optional<String> getContactInfo(ModContainer container, String key) {
		return Optional.ofNullable(container.metadata().contactInfo().get(key));
	}

	/**
	 * Gets the mod's homepage URL.
	 *
	 * @param container the mod container
	 * @return the homepage URL, or empty if not present
	 */
	public static Optional<String> getHomepage(ModContainer container) {
		return getContactInfo(container, "homepage");
	}

	/**
	 * Gets the mod's issues URL.
	 *
	 * @param container the mod container
	 * @return the issues URL, or empty if not present
	 */
	public static Optional<String> getIssuesUrl(ModContainer container) {
		return getContactInfo(container, "issues");
	}

	/**
	 * Gets the mod's sources URL.
	 *
	 * @param container the mod container
	 * @return the sources URL, or empty if not present
	 */
	public static Optional<String> getSourcesUrl(ModContainer container) {
		return getContactInfo(container, "sources");
	}

	/**
	 * Checks if a mod with the given ID is loaded.
	 *
	 * <p><b>Quilt-Specific:</b> Uses Quilt's loader to check mod presence.
	 *
	 * @param modId the mod ID to check
	 * @return true if the mod is loaded
	 */
	public static boolean isModLoaded(String modId) {
		return org.quiltmc.loader.api.QuiltLoader.isModLoaded(modId);
	}

	/**
	 * Gets a loaded mod container by ID.
	 *
	 * @param modId the mod ID
	 * @return optional containing the mod container if loaded
	 */
	public static Optional<ModContainer> getModContainer(String modId) {
		return org.quiltmc.loader.api.QuiltLoader.getModContainer(modId);
	}
}

