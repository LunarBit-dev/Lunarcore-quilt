package dev.lunarbit.lunarcore.api.lifecycle;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

/**
 * Base adapter that bridges Quilt's ModInitializer to LunarCore's ModLifecycle.
 *
 * <p>This class provides a convenient way to use LunarCore lifecycle patterns
 * while maintaining compatibility with Quilt's entrypoint system.
 *
 * <p><b>Usage:</b>
 * <pre>{@code
 * public class MyMod extends ModLifecycleAdapter {
 *     @Override
 *     public void initialize(ModContainer container) {
 *         // Your initialization logic
 *     }
 * }
 * }</pre>
 *
 * <p><b>Quilt-Specific Behavior:</b>
 * <ul>
 *   <li>Implements Quilt's ModInitializer directly</li>
 *   <li>Provides lifecycle hooks compatible with Quilt's loader</li>
 *   <li>Does NOT share implementation with Fabric</li>
 * </ul>
 *
 * <p><b>Specification References:</b>
 * <ul>
 *   <li>LC-NAM-007: Class naming with "Adapter" suffix</li>
 * </ul>
 */
public abstract class ModLifecycleAdapter implements ModInitializer, ModLifecycle {

	/**
	 * Quilt entrypoint - delegates to LunarCore lifecycle.
	 *
	 * @param mod the mod container
	 */
	@Override
	public final void onInitialize(ModContainer mod) {
		initialize(mod);
	}

	/**
	 * LunarCore initialization method - implement this in your mod.
	 *
	 * @param container the mod container
	 */
	@Override
	public abstract void initialize(ModContainer container);
}

