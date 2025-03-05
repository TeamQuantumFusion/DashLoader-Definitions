package dev.notalpha.dashloader.client.shader;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.misc.UnsafeHelper;
import dev.notalpha.dashloader.mixin.accessor.ShaderProgramAccessor;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;

import java.util.*;

public final class DashShader implements DashObject<ShaderProgram, ShaderProgram> {
	public final Map<String, Sampler> samplers;
	public final String name;
	public final DashGlBlendState blendState;
	public final List<String> attributeNames;
	public final DashShaderStage vertexShader;
	public final DashShaderStage fragmentShader;
	public final int format;
	public final List<DashGlUniform> uniforms;
	public final List<String> samplerNames;
	public transient ShaderProgram toApply;

	public DashShader(Map<String, Sampler> samplers, String name, DashGlBlendState blendState, List<String> attributeNames, DashShaderStage vertexShader, DashShaderStage fragmentShader, int format, List<DashGlUniform> uniforms, List<String> samplerNames) {
		this.samplers = samplers;
		this.name = name;
		this.blendState = blendState;
		this.attributeNames = attributeNames;
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		this.format = format;
		this.uniforms = uniforms;
		this.samplerNames = samplerNames;
	}

	public DashShader(ShaderProgram shader, RegistryWriter writer) {
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) shader;

		this.samplers = new LinkedHashMap<>();
		shaderAccess.getSamplers().forEach((s, o) -> this.samplers.put(s, new Sampler(o)));
		this.name = shader.getName();

		this.blendState = new DashGlBlendState(shaderAccess.getBlendState());
		this.attributeNames = shaderAccess.getAttributeNames();
		this.vertexShader = new DashShaderStage(shader.getVertexShader());
		this.fragmentShader = new DashShaderStage(shader.getFragmentShader());
		this.format = writer.add(shader.getFormat());
		this.uniforms = new ArrayList<>();
		Map<String, GlUniform> loadedUniforms = shaderAccess.getLoadedUniforms();
		shaderAccess.getUniforms().forEach((glUniform) -> this.uniforms.add(new DashGlUniform(glUniform, loadedUniforms.containsKey(glUniform.getName()))));
		this.samplerNames = shaderAccess.getSamplerNames();
	}

	@Override
	public ShaderProgram export(RegistryReader reader) {
		this.toApply = UnsafeHelper.allocateInstance(ShaderProgram.class);
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) this.toApply;
		//object init
		shaderAccess.setLoadedSamplerIds(new ArrayList<>());
		shaderAccess.setLoadedUniformIds(new ArrayList<>());
		shaderAccess.setLoadedAttributeIds(new ArrayList<>());

		shaderAccess.setSamplerNames(new ArrayList<>(this.samplerNames));

		//<init> top
		shaderAccess.setName(this.name);
		shaderAccess.setFormat(reader.get(this.format));

		//JsonHelper.getArray(jsonObject, "samplers", (JsonArray)null)
		var samplersOut = new HashMap<String, Object>();
		this.samplers.forEach((s, o) -> samplersOut.put(s, o.sampler));
		shaderAccess.setSamplers(samplersOut);

		// JsonHelper.getArray(jsonObject, "attributes", (JsonArray)null);
		shaderAccess.setAttributeNames(new ArrayList<>(this.attributeNames));

		final ArrayList<GlUniform> uniforms = new ArrayList<>();
		shaderAccess.setUniforms(uniforms);
		var uniformsOut = new HashMap<String, GlUniform>();
		this.uniforms.forEach((dashGlUniform) -> {
			GlUniform uniform = dashGlUniform.export(this.toApply);
			uniforms.add(uniform);
			if (dashGlUniform.loaded) {
				uniformsOut.put(dashGlUniform.name, uniform);
			}
		});
		shaderAccess.setLoadedUniforms(uniformsOut);

		// JsonHelper.getArray(jsonObject, "uniforms", (JsonArray)null);
		this.toApply.markUniformsDirty();
		this.toApply.modelViewMat = uniformsOut.get("ModelViewMat");
		this.toApply.projectionMat = uniformsOut.get("ProjMat");
		this.toApply.viewRotationMat = uniformsOut.get("IViewRotMat");
		this.toApply.textureMat = uniformsOut.get("TextureMat");
		this.toApply.screenSize = uniformsOut.get("ScreenSize");
		this.toApply.colorModulator = uniformsOut.get("ColorModulator");
		this.toApply.light0Direction = uniformsOut.get("Light0_Direction");
		this.toApply.light1Direction = uniformsOut.get("Light1_Direction");
		this.toApply.fogStart = uniformsOut.get("FogStart");
		this.toApply.fogEnd = uniformsOut.get("FogEnd");
		this.toApply.fogColor = uniformsOut.get("FogColor");
		this.toApply.fogShape = uniformsOut.get("FogShape");
		this.toApply.lineWidth = uniformsOut.get("LineWidth");
		this.toApply.gameTime = uniformsOut.get("GameTime");
		this.toApply.chunkOffset = uniformsOut.get("ChunkOffset");
		return this.toApply;
	}

	@Override
	public void postExport(RegistryReader reader) {
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) this.toApply;
		shaderAccess.setBlendState(this.blendState.export());
		shaderAccess.setVertexShader(this.vertexShader.exportProgram());
		shaderAccess.setFragmentShader(this.fragmentShader.exportProgram());
		final List<Integer> loadedAttributeIds = shaderAccess.getLoadedAttributeIds();

		final int programId = GlStateManager.glCreateProgram();
		shaderAccess.setGlRef(programId);

		if (this.attributeNames != null) {
			ImmutableList<String> names = this.toApply.getFormat().getAttributeNames();
			for (int i = 0; i < names.size(); i++) {
				String attributeName = names.get(i);
				GlUniform.bindAttribLocation(programId, i, attributeName);
				loadedAttributeIds.add(i);
			}
		}
		GlProgramManager.linkProgram(this.toApply);
		shaderAccess.loadref();
	}

	public static class Sampler {
		@DataNullable
		@DataSubclasses({Integer.class, String.class})
		public final Object sampler;

		public Sampler(Object sampler) {
			this.sampler = sampler;
		}
	}
}
