package com.lyx.codegen.service;

/**
 * @author 黎勇炫
 */
public @interface GenService {
    // 包路径
    String pkgName();

    // 前缀
    String sourcePath() default "src/main/java";

}
