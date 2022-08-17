package com.lyx.codegen.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD})
public @interface TypeConverter {
  String toTypeFullName() default "java.lang.String";

}
