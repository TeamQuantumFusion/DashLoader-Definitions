package dev.notalpha.dashloader.mixin.main;

import dev.notalpha.dashloader.DashLoader;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainMixin {
    private static boolean INITIALIZED = false;

    @Inject(
        method = "main*",
        at = @At(value = "HEAD")
    )
    private static void main(String[] args, CallbackInfo ci) {
        if (!INITIALIZED) {
            DashLoader.bootstrap();
            INITIALIZED = true;
        }
    }
}
