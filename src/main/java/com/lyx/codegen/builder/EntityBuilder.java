package com.lyx.codegen.builder;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyx.codegen.context.Columns;
import com.lyx.codegen.context.FieldRel;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 黎勇炫
 * @date 2022年08月22日 16:03
 */
public class EntityBuilder extends BuilderChain{

     /**
       *
       */
    public static final String ENTITY_PKG_SUFFID = ".domain";

    @Override
    public BuilderChain appendNext(BuilderChain next){
        this.next = next;
        return next;
    }

    @Override
    public void build(JdbcTemplate jdbcTemplate, String entityName, String tableName, String pkgRootPath, String sourcePath) {
        // 查询表中所有的字段
        List<Columns> columns = jdbcTemplate.query("SHOW FULL COLUMNS FROM " + tableName, new BeanPropertyRowMapper<Columns>(Columns.class));
        // 开始创建实体类
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityName)
                // 关联表
                .addAnnotation(AnnotationSpec.builder(TableName.class).addMember("value","\""+tableName+"\"").build())
                // lombok注解
                .addAnnotation(AnnotationSpec.builder(Data.class).build())
                // 修饰符
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(entityName+"\n@author 黎勇炫 \n@Date "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        for (Columns column : columns) {
            System.out.println(column);
            FieldSpec.Builder fieldBuilder = FieldSpec.builder((TypeName) FieldRel.getJavaType(column.getType()), buildFieldName(column.getField()), Modifier.PRIVATE)
                    .addAnnotation(AnnotationSpec.builder(Schema.class).addMember("title", "\""+column.getComment()+"\"").build());
            // 如果是主键就加上@TableId
            if(!StringUtils.isEmpty(column.getKey())){
                fieldBuilder.addAnnotation(AnnotationSpec.builder(TableId.class).build());
            }
            builder.addField(fieldBuilder.build());
        }

        genJavaSourceFile(pkgRootPath+ENTITY_PKG_SUFFID,sourcePath,builder);
        // 创建实体类
        if(null != next){
            next.build(jdbcTemplate,entityName,tableName,pkgRootPath,sourcePath);
        }
    }

     /**
       * 转换变量名称-小写/驼峰
       */
    private String buildFieldName(String field) {
        field = field.toLowerCase();
        String[] segment = field.split("_");
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < segment.length; i++) {
            if(i==0){
                str.append(segment[i]);
            }else {
                str.append(segment[i].substring(0,1).toUpperCase()+segment[i].substring(1));
            }
        }
        return str.toString();
    }
}
