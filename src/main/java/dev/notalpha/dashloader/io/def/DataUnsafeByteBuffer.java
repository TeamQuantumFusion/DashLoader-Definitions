package dev.notalpha.dashloader.io.def;

import dev.notalpha.hyphen.scan.annotations.HyphenAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@HyphenAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface DataUnsafeByteBuffer {
}
