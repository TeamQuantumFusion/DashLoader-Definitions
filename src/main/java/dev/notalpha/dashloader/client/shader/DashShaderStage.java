package dev.notalpha.dashloader.client.shader;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.mixin.accessor.ShaderStageAccessor;
import net.minecraft.client.gl.ShaderStage;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public final class DashShaderStage {
	public final ShaderStage.Type shaderType;
	public final String name;
	public final List<String> shader;

	public DashShaderStage(ShaderStage.Type shaderType, String name, List<String> shader) {
		this.shaderType = shaderType;
		this.name = name;
		this.shader = shader;
	}

	public DashShaderStage(ShaderStage program) {
		ShaderStageAccessor access = (ShaderStageAccessor) program;
		this.shaderType = access.getType();
		this.name = program.getName();
		List<String> shader = ShaderModule.WRITE_PROGRAM_SOURCES.get(CacheStatus.SAVE).get(access.getGlRef());
		if (shader == null) {
			throw new RuntimeException();
		}
		this.shader = shader;
	}

	public int createProgram(ShaderStage.Type type) {
		//noinspection ConstantConditions (MixinAccessor shit)
		int id = GlStateManager.glCreateShader(((ShaderStageAccessor.TypeAccessor) (Object) type).getGlType());
		GlStateManager.glShaderSource(id, this.shader);
		GlStateManager.glCompileShader(id);
		if (GlStateManager.glGetShaderi(id, GlConst.GL_COMPILE_STATUS) == 0) {
			String errorString = StringUtils.trim(GlStateManager.glGetShaderInfoLog(id, 32768));
			throw new RuntimeException("Couldn't compile " + type.getName() + " : " + errorString);
		} else {
			return id;
		}
	}

	public ShaderStage exportProgram() {
		Map<String, ShaderStage> loadedShaders = this.shaderType.getLoadedShaders();
		final ShaderStage program = ShaderStageAccessor.create(this.shaderType, this.createProgram(this.shaderType), this.name);
		loadedShaders.put(this.name, program);
		return program;
	}
}
