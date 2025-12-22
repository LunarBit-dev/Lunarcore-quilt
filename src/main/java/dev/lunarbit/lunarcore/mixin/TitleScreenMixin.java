package dev.lunarbit.lunarcore.mixin;

import dev.lunarbit.lunarcore.api.logging.LunarLogger;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Example mixin demonstrating LunarCore logging integration.
 *
 * <p>This mixin shows how to use LunarLogger in mixin classes while
 * following Quilt-specific patterns.
 */
@Mixin(TitleScreen.class)
public class TitleScreenMixin {

	private static final LunarLogger LOGGER = LunarLogger.getLogger("UI.TitleScreen");

	@Inject(method = "init", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {
		LOGGER.info("Title screen initialized - LunarCore Quilt is active");
		LOGGER.debug("This is a debug message demonstrating component naming: LunarCore.UI.TitleScreen");
	}
}

