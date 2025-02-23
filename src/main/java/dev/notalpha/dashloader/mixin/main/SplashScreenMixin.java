package dev.notalpha.dashloader.mixin.main;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.DashLoaderClient;
import dev.notalpha.dashloader.client.ui.toast.DashToast;
import dev.notalpha.dashloader.client.ui.toast.DashToastState;
import dev.notalpha.dashloader.client.ui.toast.DashToastStatus;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.misc.ProfilerUtil;
import dev.notalpha.taski.builtin.StaticTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.toast.Toast;
import net.minecraft.resource.ResourceReload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SplashOverlay.class, priority = 69420)
public class SplashScreenMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private long reloadCompleteTime;
    @Shadow
    @Final
    private ResourceReload reload;
    @Mutable
    @Shadow
    @Final
    private boolean reloading;

    @Inject(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J", shift = At.Shift.BEFORE, ordinal = 1)
    )
    private void done(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.client.setOverlay(null);
        if (this.client.currentScreen != null) {
            if (this.client.currentScreen instanceof TitleScreen) {
                this.client.currentScreen = new TitleScreen(false);
            }
        }

        DashLoader.LOG.info("Minecraft reloaded in {}", ProfilerUtil.getTimeStringFromStart(ProfilerUtil.RELOAD_START));
        Cache cache = DashLoaderClient.CACHE;
        if (DashLoaderClient.CACHE.getStatus() == CacheStatus.SAVE && client.getToastManager().getToast(DashToast.class, Toast.TYPE) == null) {
            DashToastState rawState;
            if (ConfigHandler.INSTANCE.config.showCachingToast) {
                DashToast toast = new DashToast();
                client.getToastManager().add(toast);
                rawState = toast.state;
            } else {
                rawState = new DashToastState();
            }

            final Thread thread = new Thread(() -> {
                DashToastState state = rawState;
                DashToastState finalState = state;
                state.setStatus(DashToastStatus.PROGRESS);
                long start = System.currentTimeMillis();
                boolean save = cache.save(stepTask -> finalState.task = stepTask);
                if (save) {
                    state.setOverwriteText("Created cache in " + ProfilerUtil.getTimeStringFromStart(start));
                    state.setStatus(DashToastStatus.DONE);
                } else {
                    // Only show toast on fail.
                    if (!ConfigHandler.INSTANCE.config.showCachingToast) {
                        DashToast toast = new DashToast();
                        client.getToastManager().add(toast);
                        state = toast.state;
                    }
                    state.setOverwriteText("Internal error, Please check logs.");
                    state.task = new StaticTask("Crash", 0);
                    state.setStatus(DashToastStatus.CRASHED);
                }
                cache.reset();
                state.setDone();
            });
            thread.setName("dashloader-thread");
            thread.start();
        } else {
            cache.reset();
        }
    }

    @Inject(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;isComplete()Z", shift = At.Shift.BEFORE)
    )
    private void removeMinimumTime(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.reloadCompleteTime == -1L && this.reload.isComplete()) {
            this.reloading = false;
        }
    }
}
