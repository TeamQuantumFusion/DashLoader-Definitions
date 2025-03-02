package dev.notalpha.dashloader.misc;

import net.minecraft.client.texture.NativeImage;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.function.Supplier;

public class ObjectDumper {
    public static String dump(Object object) {
        return ReflectionToStringBuilder.toString(object, new Style());
    }

    private static final class Style extends MultilineRecursiveToStringStyle {
        public Style() {
            setFieldNameValueSeparator(": ");
            setUseIdentityHashCode(false);
            setUseShortClassName(true);
        }

        public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
            try {
                if (value == null) {
                    buffer.append(fieldName).append("null");
                    return;
                }
                if (Objects.equals(fieldName, "glRef")) {
                    buffer.append("<id>");
                    return;
                }

                switch (value) {
                    case ThreadLocal local -> appendDetail(buffer, fieldName, local.get());
                    case HashMap map -> appendDetail(buffer, fieldName, (Map<?, ?>) map);
                    case ArrayList list -> appendDetail(buffer, fieldName, (List<?>) list);
                    case NativeImage image ->
                        buffer.append("Image{ format: ").append(image.getFormat()).append(", size: ").append(image.getWidth()).append("x").append(image.getHeight()).append(" }");
                    case IntBuffer buffer1 -> appendBuff(buffer, buffer1, buffer1::get);
                    case FloatBuffer buffer1 -> appendBuff(buffer, buffer1, buffer1::get);
                    case ByteBuffer buffer1 -> appendBuff(buffer, buffer1, buffer1::get);
                    case Enum<?> enumValue -> buffer.append(enumValue.name());
                    default -> fallback(buffer, fieldName, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(value == null ? "null" : value.toString(), e);
            }
        }

        private void fallback(StringBuffer buffer, String fieldName, Object value) {
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
        }

        private void appendBuff(StringBuffer buffer, Buffer buff, Supplier supplier) {
            buffer.append(buff.getClass().getSimpleName());
            buffer.append("[ ");
            int limit = buff.limit();
            if (limit < 50) {
                buff.rewind();
                for (int i = 0; i < limit; i++) {
                    buffer.append(supplier.get());
                    buffer.append(", ");
                }
            } else {
                buffer.append("... ");
            }
            buffer.append("] limit: ");
            buffer.append(buff.limit());
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
