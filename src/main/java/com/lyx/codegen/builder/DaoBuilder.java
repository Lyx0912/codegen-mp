package com.lyx.codegen.builder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 黎勇炫
 * @date 2022年08月23日 11:25
 */
public class DaoBuilder extends BuilderChain{

     /**
       * edit
       */
    public static final String DAO_SUFFIX = "Dao";
     /**
       * edit
       */
    public static final String DAO_PKG_SUFFIX = ".dao";

    @Override
    public void build(JdbcTemplate jdbcTemplate, String entityName, String tableName, String pkgRootPath, String sourcePath) {

        Class<BaseMapper> clazz = BaseMapper.class;

        // 创建接口
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(entityName + DAO_SUFFIX)
                .addAnnotation(Mapper.class)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(clazz), ClassName.get(pkgRootPath + EntityBuilder.ENTITY_PKG_SUFFID, entityName)))
                .addJavadoc(entityName+"\n@author 黎勇炫 \n@Date "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        genJavaSourceFile(pkgRootPath+DAO_PKG_SUFFIX,sourcePath,builder);

        this.next.build(jdbcTemplate,entityName,tableName,pkgRootPath,sourcePath);
    }
}
