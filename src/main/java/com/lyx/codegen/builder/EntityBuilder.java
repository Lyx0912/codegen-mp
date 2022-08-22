package com.lyx.codegen.builder;

import com.lyx.codegen.context.Columns;
import com.lyx.codegen.context.FieldRel;
import com.lyx.codegen.dto.GenDto;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * @author 黎勇炫
 * @date 2022年08月22日 16:03
 */
public class EntityBuilder extends BuilderChain{

    protected BuilderChain next;
    private static final String ENTITY_SUFFIX = ".domain";

    public EntityBuilder(JdbcTemplate jdbcTemplate, String entityName, String tableName,String packageName,String sourcePath) {
        super(jdbcTemplate,entityName,tableName,packageName,sourcePath);
    }

    @Override
    public BuilderChain appendNext(BuilderChain next){
        this.next = next;
        return next;
    }

    @Override
    public void build() {
        // 查询表中所有的字段
        List<Columns> columns = jdbcTemplate.query("SHOW FULL COLUMNS FROM " + tableName, new BeanPropertyRowMapper<Columns>(Columns.class));
        // 开始创建实体类
        TypeSpec.Builder builder = TypeSpec.classBuilder(entityName)
                // 修饰符
                .addModifiers(Modifier.PUBLIC);
        for (Columns column : columns) {
            FieldSpec.Builder fieldBuilder = FieldSpec.builder((TypeName) FieldRel.getJavaType(column.getType()), column.getField(), Modifier.PRIVATE);
            builder.addField(fieldBuilder.build());
        }

        genJavaSourceFile(packagePath+ENTITY_SUFFIX,sourcePath,builder);
        // 创建实体类
        if(null != next){
            next.build();
        }
    }
}
