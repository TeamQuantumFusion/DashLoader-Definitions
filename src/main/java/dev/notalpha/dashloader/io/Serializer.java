package dev.notalpha.dashloader.io;

import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.io.def.NativeImageData;
import dev.notalpha.dashloader.io.def.NativeImageDataDef;
import dev.notalpha.dashloader.registry.data.ChunkData;
import dev.notalpha.hyphen.HyphenSerializer;
import dev.notalpha.hyphen.SerializerFactory;
import dev.notalpha.hyphen.io.ByteBufferIO;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.taski.builtin.StepTask;
import net.minecraft.client.font.UnihexFont;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;

public class Serializer<O> {
	private final HyphenSerializer<ByteBufferIO, O> serializer;

	public Serializer(Class<O> aClass) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, aClass);
		factory.addAnnotationProvider(ChunkData.class, new DataSubclasses() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return DataSubclasses.class;
			}

			@Override
			public Class<?>[] value() {
				return new Class[]{ChunkData.class};
			}
		});
		factory.setClassName(getSerializerClassName(aClass));
		factory.addAnnotationProvider(UnihexFont.BitmapGlyph.class, new DataSubclasses() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return DataSubclasses.class;
			}

			@Override
			public Class<?>[] value() {
				return new Class[]{
						UnihexFont.FontImage32x16.class,
						UnihexFont.FontImage16x16.class,
						UnihexFont.FontImage8x16.class,
				};
			}
		});

		factory.addDynamicDef(NativeImageData.class, NativeImageDataDef::new);
		this.serializer = factory.build();
	}

	@NotNull
	private static <O> String getSerializerClassName(Class<O> holderClass) {
		return holderClass.getSimpleName().toLowerCase() + "-serializer";
	}

	public O get(ByteBufferIO io) {
		return this.serializer.get(io);
	}

	public void put(ByteBufferIO io, O data) {
		this.serializer.put(io, data);
	}

	public long measure(O data) {
		return this.serializer.measure(data);
	}

	public void save(Path path, StepTask task, O data) {
		var measure = (int) this.serializer.measure(data);
		var io = ByteBufferIO.createDirect(measure);
		this.serializer.put(io, data);
		io.rewind();
		try {

			IOHelper.save(path, task, io, measure, ConfigHandler.INSTANCE.config.compression);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public O load(Path path) {
		try {
			ByteBufferIO io = IOHelper.load(path);
			return this.serializer.get(io);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
