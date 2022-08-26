package com.lyx.codegen.builder;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生成controller
 * @author 黎勇炫
 * @date 2022年08月24日 13:51
 */
@RequestMapping
public class ControllerBuilder extends BuilderChain{

    public static final String CONTROLLER_SUFFIX = "Controller";

    @Override
    public void build(JdbcTemplate jdbcTemplate, String entityName, String item, String pkgRootPath, String sourcePath) {

         /**
           * 1.先创建controller类，添加相关注解和权限标识
           * 2.创建属性，注入service类
           * 3.创建api接口
           */

        TypeSpec builder = TypeSpec.classBuilder(entityName + CONTROLLER_SUFFIX)
                // 添加@RestController注解
                .addAnnotation(AnnotationSpec.builder(RestController.class).build())
                // 添加RequestMapping注解
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class).addMember("path", "/"+entityName.toLowerCase()).build())
                .build();

        // 创建接口

    }
}
