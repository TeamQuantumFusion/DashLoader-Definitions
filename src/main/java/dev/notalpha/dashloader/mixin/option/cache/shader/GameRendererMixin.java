package dev.notalpha.dashloader.mixin.option.cache.shader;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;
import java.util.HashMap;

@Mixin(value = GameRenderer.class, priority = 69)
public abstract class GameRendererMixin {
    @WrapOperation(
        method = "loadPrograms",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/resource/ResourceFactory;Ljava/lang/String;Lnet/minecraft/client/render/VertexFormat;)Lnet/minecraft/client/gl/ShaderProgram;"
        )
    )
    private ShaderProgram shaderCreation(ResourceFactory factory, String name, VertexFormat format, Operation<ShaderProgram> original) throws IOException {
        HashMap<String, ShaderProgram> shaders = ShaderModule.SHADERS.get(CacheStatus.LOAD);
        if (shaders != null) {
            // If we are reading from cache load the shader and check if its cached.
            var shader = shaders.get(name);
            if (shader != null) {
                return shader;
            }
        }

        ShaderProgram shader = original.call(factory, name, format);
        ShaderModule.SHADERS.visit(CacheStatus.SAVE, map -> map.put(name, shader));
        return shader;
    }
}
