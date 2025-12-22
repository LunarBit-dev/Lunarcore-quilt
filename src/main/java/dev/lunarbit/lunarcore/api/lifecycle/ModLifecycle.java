package dev.lunarbit.lunarcore.api.lifecycle;

import org.quiltmc.loader.api.ModContainer;

/**
 * LunarCore lifecycle interface for Quilt mods.
 *
 * <p>This interface provides a Quilt-native abstraction over mod lifecycle events.
 * Unlike Fabric, Quilt uses ModContainer-based initialization which provides better
 * metadata access and container isolation.
 *
 * <p><b>Quilt-Specific Behavior:</b>
 * <ul>
 *   <li>Receives ModContainer directly (Quilt-native pattern)</li>
 *   <li>Access to Quilt's metadata system via container</li>
 *   <li>Better isolation between mods</li>
 * </ul>
 *
 * <p><b>Specification References:</b>
 * <ul>
 *   <li>LC-NAM-004: Verb-noun function naming (initialize)</li>
 *   <li>LC-NAM-007: Class naming conventions</li>
 * </ul>
 *
 * @see org.quiltmc.qsl.base.api.entrypoint.ModInitializer
 */
public interface ModLifecycle {

	/**
	 * Called when the mod is initialized during game startup.
	 *
	 * <p><b>Quilt-Specific:</b> This is called during the Quilt loader's initialization
	 * phase, which happens before the game starts but after all mods are loaded.
	 *
	 * @param container the Quilt ModContainer for this mod
	 */
	void initialize(ModContainer container);

	/**
	 * Called when the mod is being shut down.
	 *
	 * <p>This is optional to implement. Default implementation does nothing.
	 *
	 * <p><b>Note:</b> Quilt does not guarantee this will be called in all
	 * shutdown scenarios (e.g., crashes, forced termination).
	 */
	default void shutdown() {
		// Default: no-op
	}
}

