// 
// Decompiled by Procyon v0.5.36
// 

package com.google.errorprone.annotations.concurrent;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.CLASS)
public @interface LockMethod {
    String[] value();
}
