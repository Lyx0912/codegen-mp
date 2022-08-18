package com.lyx.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.auto.service.AutoService;
import com.lyx.codegen.processor.BaseCodeGenProcessor;
import com.lyx.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import sun.rmi.runtime.Log;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.annotation.Annotation;

/**
 * @author 黎勇炫
 * @date 2022年08月18日 9:29
 */
@AutoService(CodeGenProcessor.class)
public class ServiceCodeGenProcessor extends BaseCodeGenProcessor {

    public static final String SUFFIX = "Service";
    public static final String IMPL_SUFFIX = "ServiceImpl";

    /**
     * 需要解析的类上的注解
     *
     * @return
     */
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenService.class;
    }

    /**
     * 获取生成的包路径
     *
     * @param typeElement
     * @return
     */
    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenService.class).pkgName();
    }

    /**
     * 生成class 类
     *
     * @param typeElement
     * @param roundEnvironment
     * @return
     */
    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        Class<IService> service = IService.class;
        Class<ServiceImpl> serviceImpl = ServiceImpl.class;
        // 创建inteface
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(typeElement.getSimpleName() + SUFFIX)
                // 继承iservice接口
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(service), ClassName.get(typeElement)))
                .addModifiers(Modifier.PUBLIC);
        String packageName = generatePackage(typeElement);
        // 生成service接口
        genJavaSourceFile(packageName, typeElement.getAnnotation(GenService.class).sourcePath(), builder);
        // 加载刚才生成的service接口，在生成实现类的时候继承这个接口
        // 创建impl
        TypeSpec.Builder impl = null;
        impl = TypeSpec.classBuilder(typeElement.getSimpleName() + IMPL_SUFFIX)
                .superclass(ParameterizedTypeName.get(ClassName.get(serviceImpl), ClassName.get(typeElement), ClassName.get(typeElement)))
//                .addSuperinterface(builder.build().)
                .addModifiers(Modifier.PUBLIC);

        genJavaSourceFile(packageName + ".impl", typeElement.getAnnotation(GenService.class).sourcePath(), impl);
    }
}
