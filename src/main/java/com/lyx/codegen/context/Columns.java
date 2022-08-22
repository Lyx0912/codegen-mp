package com.lyx.codegen.context;

import lombok.Data;

/**
 * @author 黎勇炫
 * @date 2022年08月22日 14:42
 */
@Data
public class Columns {
     /** 字段 */
    private String field;
    /** 类型 */
    private String type;
    /** 主键 */
    private String key;
    /** 描述 */
    private String comment;
}
