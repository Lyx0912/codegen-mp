package com.lyx.codegen.mapper;

public @interface GenDao {
    // 包路径
    String pkgName();

    // 前缀
    String sourcePath() default "src/main/java";
}
