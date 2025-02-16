package dev.notalpha.dashloader.mixin.main;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.DashLoaderClient;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes f3 + t reset the cache. Also makes shift + f3 + t not reset it.
 */
@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Unique
    private boolean shiftHeld = false;

    @Inject(
        method = "processF3",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;reloadResources()Ljava/util/concurrent/CompletableFuture;",
            shift = At.Shift.BEFORE
        )
    )
    private void f3tReloadWorld(int key, CallbackInfoReturnable<Boolean> cir) {
        if (!this.shiftHeld) {
            if (DashLoaderClient.CACHE.getStatus() == CacheStatus.IDLE) {
                DashLoader.LOG.info("Clearing cache.");
                DashLoaderClient.CACHE.remove();
            }
        }
    }

    @Inject(
        method = "onKey",
        at = @At("HEAD")
    )
    private void keyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        this.shiftHeld = action != 0 && modifiers == GLFW.GLFW_MOD_SHIFT;
    }
}
