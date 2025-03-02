package dev.notalpha.dashloader.client;

import dev.notalpha.dashloader.api.DashEntrypoint;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheFactory;
import dev.notalpha.dashloader.client.blockstate.DashBlockState;
import dev.notalpha.dashloader.client.font.*;
import dev.notalpha.dashloader.client.identifier.DashIdentifier;
import dev.notalpha.dashloader.client.identifier.DashModelIdentifier;
import dev.notalpha.dashloader.client.identifier.DashSpriteIdentifier;
import dev.notalpha.dashloader.client.model.*;
import dev.notalpha.dashloader.client.model.components.DashBakedQuad;
import dev.notalpha.dashloader.client.model.components.DashBakedQuadCollection;
import dev.notalpha.dashloader.client.model.predicates.*;
import dev.notalpha.dashloader.client.shader.DashShader;
import dev.notalpha.dashloader.client.shader.DashVertexFormat;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import dev.notalpha.dashloader.client.splash.SplashModule;
import dev.notalpha.dashloader.client.sprite.content.DashImage;
import dev.notalpha.dashloader.client.sprite.content.DashSprite;
import dev.notalpha.dashloader.client.sprite.content.DashSpriteContents;
import dev.notalpha.dashloader.client.sprite.content.SpriteContentModule;
import dev.notalpha.dashloader.client.sprite.stitch.SpriteStitcherModule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.List;

public class DashLoaderClient implements DashEntrypoint {
	public static final Cache CACHE;
	public static boolean NEEDS_RELOAD = false;

	static {
		CacheFactory cacheManagerFactory = CacheFactory.create();
		List<DashEntrypoint> entryPoints = FabricLoader.getInstance().getEntrypoints("dashloader", DashEntrypoint.class);
		for (DashEntrypoint entryPoint : entryPoints) {
			entryPoint.onDashLoaderInit(cacheManagerFactory);
		}

		CACHE = cacheManagerFactory.build(Path.of("./dashloader-cache/client/"));
	}

	@Override
	public void onDashLoaderInit(CacheFactory factory) {
		factory.addModule(new FontModule());
		factory.addModule(new ModelModule());
		factory.addModule(new ShaderModule());
		factory.addModule(new SplashModule());
		factory.addModule(new SpriteStitcherModule());
		factory.addModule(new SpriteContentModule());

		factory.addMissingHandler(
				Identifier.class,
				(identifier, registryWriter) -> {
					if (identifier instanceof ModelIdentifier m) {
						return new DashModelIdentifier(m);
					} else {
						return new DashIdentifier(identifier);
					}
				}
		);

		factory.addMissingHandler(
				Sprite.class,
				DashSprite::new
		);
		factory.addMissingHandler(
				MultipartModelSelector.class,
				(selector, writer) -> {
					if (selector == MultipartModelSelector.TRUE) {
						return new DashStaticPredicate(true);
					} else if (selector == MultipartModelSelector.FALSE) {
						return new DashStaticPredicate(false);
					} else if (selector instanceof AndMultipartModelSelector s) {
						return new DashAndPredicate(s, writer);
					} else if (selector instanceof OrMultipartModelSelector s) {
						return new DashOrPredicate(s, writer);
					} else if (selector instanceof SimpleMultipartModelSelector s) {
						return new DashSimplePredicate(s);
					} else if (selector instanceof BooleanSelector s) {
						return new DashStaticPredicate(s.selector);
					} else {
						throw new RuntimeException("someone is having fun with lambda selectors again");
					}
				}
		);

		//noinspection unchecked
		for (Class<? extends DashObject<?, ?>> aClass : new Class[]{
				DashIdentifier.class,
				DashModelIdentifier.class,
				DashBasicBakedModel.class,
				DashBuiltinBakedModel.class,
				DashMultipartBakedModel.class,
				DashWeightedBakedModel.class,
				DashBakedQuad.class,
				DashBakedQuadCollection.class,
				DashSpriteIdentifier.class,
				DashAndPredicate.class,
				DashOrPredicate.class,
				DashSimplePredicate.class,
				DashStaticPredicate.class,
				DashImage.class,
				DashSprite.class,
				DashSpriteContents.class,
				DashBitmapFont.class,
				DashBlankFont.class,
				DashSpaceFont.class,
				DashTrueTypeFont.class,
				DashUnihexFont.class,
				DashBlockState.class,
				DashVertexFormat.class,
				DashShader.class
		}) {
			factory.addDashObject(aClass);
		}
	}
}
