package com.lyx.codegen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.auto.service.AutoService;
import com.lyx.codegen.processor.BaseCodeGenProcessor;
import com.lyx.codegen.service.GenService;
import com.lyx.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author 黎勇炫
 * @date 2022年08月19日 8:59
 */
@AutoService(CodeGenProcessor.class)
public class DaoCodeGenProcessor extends BaseCodeGenProcessor {

    public static final String SUFFIX = "Dao";

    /**
     * 生成class 类
     *
     * @param typeElement
     * @param roundEnvironment
     * @return
     */
    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {

        Class<BaseMapper> clazz = BaseMapper.class;
        // 生成xxxDao并继承BaseMapper<xxx>
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(typeElement.getSimpleName() + SUFFIX)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(clazz), ClassName.get(typeElement)))
                .addModifiers(Modifier.PUBLIC);

        // 输出java文件
        genJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(GenDao.class).sourcePath(),builder);
    }

    /**
     * 需要解析的类上的注解
     *
     * @return
     */
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenDao.class;
    }

    /**
     * 获取生成的包路径
     *
     * @param typeElement
     * @return
     */
    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenDao.class).pkgName();
    }
}
