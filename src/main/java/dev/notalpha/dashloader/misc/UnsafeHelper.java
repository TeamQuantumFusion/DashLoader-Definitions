package dev.notalpha.dashloader.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class UnsafeHelper {
	public static final sun.misc.Unsafe UNSAFE = getUnsafeInstance();

	private static sun.misc.Unsafe getUnsafeInstance() {
		Class<sun.misc.Unsafe> clazz = sun.misc.Unsafe.class;
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.getType().equals(clazz)) {
				continue;
			}
			final int modifiers = field.getModifiers();
			if (!(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers))) {
				continue;
			}
			try {
				field.setAccessible(true);
				return (sun.misc.Unsafe) field.get(null);
			} catch (Exception ignored) {
			}
			break;
		}

		throw new IllegalStateException("Unsafe is unavailable.");
	}

	@SuppressWarnings("unchecked")
	public static <O> O allocateInstance(Class<O> closs) {
		try {
			return (O) UNSAFE.allocateInstance(closs);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}
}
