package com.lyx.codegen.context;

import com.squareup.javapoet.TypeName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 字段类型关系
 * @author 黎勇炫
 * @date 2022年08月22日 14:45
 */
public class FieldRel {

    private static Map<String, TypeName> rel= null;

    static{
        rel = new HashMap<>();
        rel.put("tinyint",TypeName.get(int.class));
        rel.put("int",TypeName.get(int.class));
        rel.put("String",TypeName.get(String.class));
        rel.put("Date", TypeName.get(Date.class));
        rel.put("bigint",TypeName.get(long.class));
    }

     /**
       * 根据数据库中的类型返回对应的javaType
       */
    public static Object getJavaType(String type){
        if(type.startsWith("varchar")){
            return rel.get("String");
        }
        if(type.startsWith("bigint")){
            return rel.get("bigint");
        }
        if(type.startsWith("int")){
            return rel.get("int");
        }
        return rel.get(type);
    }

}
