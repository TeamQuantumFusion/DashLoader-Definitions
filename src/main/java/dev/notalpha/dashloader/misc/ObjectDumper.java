package dev.notalpha.dashloader.misc;

import net.minecraft.client.texture.NativeImage;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class ObjectDumper {
	public static String dump(Wrapper object) {
		return ReflectionToStringBuilder.toString(object, new Style());
	}

	public static class Wrapper {
		public final Object data;

		public Wrapper(Object data) {
			this.data = data;
		}
	}

	private static final class Style extends MultilineRecursiveToStringStyle {
		public Style() {
			setFieldNameValueSeparator(": ");
			setUseIdentityHashCode(false);
			setUseShortClassName(true);
		}

		public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
			try {
				if (value != null) {
					if (Objects.equals(fieldName, "glRef")) {
						buffer.append("<id>");
						return;
					}

					if (value instanceof ThreadLocal local) {
						appendDetail(buffer, fieldName, local.get());
						return;
					}

					if (value instanceof HashMap map) {
						appendDetail(buffer, fieldName, (Map<?, ?>) map);
						return;
					}

					if (value instanceof ArrayList list) {
						appendDetail(buffer, fieldName, (List<?>) list);
						return;
					}

					if (value instanceof NativeImage image) {
						buffer.append("Image{ format: ").append(image.getFormat()).append(", size: ").append(image.getWidth()).append("x").append(image.getHeight()).append(" }");
						return;
					}

					if (value instanceof IntBuffer buffer1) {
						buffer.append("IntBuffer [");
						int limit = buffer1.limit();
						if (limit < 50) {
							buffer1.rewind();
							for (int i = 0; i < limit; i++) {
								float v = buffer1.get();
								buffer.append(v);
								buffer.append(", ");
							}
						}
						buffer.append("]");
						return;
					}

					if (value instanceof FloatBuffer buffer1) {
						buffer.append("FloatBuffer [");
						int limit = buffer1.limit();
						if (limit < 50) {
							buffer1.rewind();
							for (int i = 0; i < limit; i++) {
								float v = buffer1.get();
								buffer.append(v);
								buffer.append(", ");
							}
						}
						buffer.append("]");
						return;
					}

					if (value instanceof Enum<?> enumValue) {
						buffer.append(enumValue.name());
						return;
					}
				} else {
					buffer.append("null");
					return;
				}

				try {
					StringBuffer builder = new StringBuffer();
					super.appendDetail(builder, fieldName, value);
					String s = builder.toString();
					String result = s.split("@")[0];
					buffer.append(result);
				} catch (InaccessibleObjectException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();

					buffer.append("unknown");
					try {
						Field spaces = MultilineRecursiveToStringStyle.class.getDeclaredField("spaces");
						spaces.setAccessible(true);
						spaces.setInt(this, spaces.getInt(this) - 2);
					} catch (IllegalAccessException | NoSuchFieldException ex) {
						throw new RuntimeException(ex);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(value == null ? "null" : value.toString(), e);
			}
		}

		@Override
		protected void appendDetail(StringBuffer buffer, String fieldName, Map<?, ?> map) {
			buffer.append(this.getArrayStart());

			// Sort maps to be comparible
			List<Map.Entry<?, ?>> entries = new ArrayList<>(map.entrySet());
			entries.sort((o1, o2) -> o1.getKey().toString().compareTo(o2.toString()));
			entries.forEach((entry) -> {
				buffer.append(getArraySeparator());
				this.appendDetail(buffer, String.valueOf(entry.getKey()), entry.getValue());
			});
			buffer.append(this.getArrayEnd());
		}

		@Override
		protected void appendIdentityHashCode(StringBuffer buffer, Object object) {

		}
	}
}
