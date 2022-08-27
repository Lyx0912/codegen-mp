package com.lyx.codegen.builder;

import com.squareup.javapoet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.Modifier;
import java.util.Map;

/**
 * 生成controller
 * @author 黎勇炫
 * @date 2022年08月24日 13:51
 */
@RequestMapping
public class ControllerBuilder extends BuilderChain{

    public static final String CONTROLLER_SUFFIX = "Controller";
    public static final String CONTROLLER_OKG_SUFFIX = ".controller";

    @Override
    public void build(JdbcTemplate jdbcTemplate, String entityName, String item, String pkgRootPath, String sourcePath) {

         /**
           * 1.先创建controller类，添加相关注解和权限标识
           * 2.创建属性，注入service类
           * 3.创建api接口
           */

        TypeSpec.Builder builder = TypeSpec.classBuilder(entityName + CONTROLLER_SUFFIX)
                // 添加@RestController注解
                .addAnnotation(AnnotationSpec.builder(RestController.class).build())
                // 添加RequestMapping注解
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class).addMember("value", "\"/" + entityName.toLowerCase()+"\"").build())
                // 导入实体类
                // 权限修饰符
                .addModifiers(Modifier.PUBLIC);

        // 添加Service注入
        builder.addField(FieldSpec.builder(ClassName.get(pkgRootPath+ServiceBuilder.SERVICE_PKG_SUFFIX,entityName+ServiceBuilder.SERVICE_SUFFIX),entityName.substring(0,1).toLowerCase()+entityName.substring(1)+ServiceBuilder.SERVICE_SUFFIX)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Autowired.class).build())
                .build());
        // 查询接口
        builder.addMethod(
                MethodSpec.methodBuilder("query")
                        .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                                        .addMember("value","\"/query\"")
                                        .build()
                        )
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Map.class)
                        .addCode("\nList<"+entityName+"> list = "+entityName+ServiceBuilder.SERVICE_SUFFIX+".list();\nreturn\tnew HashMap<Object,Object>();")
                        .build()
        );
        // 编辑接口
        builder.addMethod(
                MethodSpec.methodBuilder("query")
                        .addParameter(ParameterSpec.builder(ClassName.get(pkgRootPath+EntityBuilder.ENTITY_PKG_SUFFID,entityName),entityName.toLowerCase()).addAnnotation(RequestBody.class).build())
                        .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                                        .addMember("value","\"/edit\"")
                                        .build()
                        )
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Map.class)
                        .addCode("\nList<"+entityName+"> list = "+entityName+ServiceBuilder.SERVICE_SUFFIX+".list();\nreturn\tnew HashMap<Object,Object>();")
                        .build()
        );

        // 删除接口
        builder.addMethod(
                MethodSpec.methodBuilder("remove")
                        .addParameter(ParameterSpec.builder(String[].class,"ids").addAnnotation(RequestBody.class).build())
                        .addAnnotation(
                            AnnotationSpec.builder(RequestMapping.class)
                                .addMember("value","\"/remove\"")
                                .build()
                        )
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Map.class)
                        .addCode("\nList<"+entityName+"> list = "+entityName+ServiceBuilder.SERVICE_SUFFIX+".list();\nreturn\tnew HashMap<Object,Object>();")
                        .build());
        // 添加接口
//        builder.addMethod();
        genJavaSourceFile(pkgRootPath+CONTROLLER_OKG_SUFFIX,sourcePath,builder);
    }
}
