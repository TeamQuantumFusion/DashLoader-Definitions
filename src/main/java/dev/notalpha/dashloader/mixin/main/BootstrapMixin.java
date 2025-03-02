package dev.notalpha.dashloader.mixin.main;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.misc.ProfilerUtil;
import net.minecraft.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Bootstrap.class, priority = -69)
public class BootstrapMixin {
	@Unique
	private static long BOOTSTRAP_START = -1;

	@Inject(method = "initialize", at = @At("HEAD"))
	private static void timeStart(CallbackInfo ci) {
		BOOTSTRAP_START = System.currentTimeMillis();
	}

	@Inject(method = "initialize", at = @At("TAIL"))
	private static void timeStop(CallbackInfo ci) {
		DashLoader.LOG.info("Minecraft bootstrap in {}", ProfilerUtil.getTimeStringFromStart(BOOTSTRAP_START));
	}
}
