package com.lyx.codegen.dto;

import com.google.auto.service.AutoService;
import com.lyx.codegen.processor.BaseCodeGenProcessor;
import com.lyx.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.*;
import com.squareup.javapoet.TypeSpec.Builder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

/**
 * @author gim vo 代码生成器
 */
@AutoService(value = CodeGenProcessor.class)
public class DtoCodeGenProcessor extends BaseCodeGenProcessor {

  public static final String SUFFIX = "DTO";

  @Override
  public Class<? extends Annotation> getAnnotation() {
    return GenDto.class;
  }

  @Override
  public String generatePackage(TypeElement typeElement) {
    return typeElement.getAnnotation(GenDto.class).pkgName();
  }

  @Override
  protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
    // 遍历获得所有字段
    Set<VariableElement> fields = findFields(typeElement, ve -> Objects.isNull(ve.getAnnotation(IgnoreDto.class)));
    // 类名
    String className = typeElement.getSimpleName() + SUFFIX;
    // 资源名
    String sourceClassName = typeElement.getSimpleName() + SUFFIX;
    // 开始创建类
    Builder builder = TypeSpec.classBuilder(className)
//        .superclass(AbstractBaseJpaVO.class) 继承AbstractBaseJpaVO类
        // 修饰符
        .addModifiers(Modifier.PUBLIC)
        // 添加注解
        .addAnnotation(Schema.class)
        .addAnnotation(Data.class);
    // 生产getter、setter方法
//    addSetterAndGetterMethod(builder, fields);
    for (VariableElement field : fields) {
      builder.addField(FieldSpec
              .builder(TypeName.get(field.asType()) , String.valueOf(field.getSimpleName()), Modifier.PRIVATE)
              .addAnnotation(AnnotationSpec.builder(Schema.class)
                      .addMember("title", "$S", getFieldDesc(field))
                      .build())
              .build());

    }
    String packageName = generatePackage(typeElement);
    genJavaSourceFile(packageName,typeElement.getAnnotation(GenDto.class).sourcePath(),builder);
  }
}
