package com.lyx.codegen.builder;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 生成controller
 * @author 黎勇炫
 * @date 2022年08月24日 13:51
 */
public class ControllerBuilder extends BuilderChain{

    @Override
    public void build(JdbcTemplate jdbcTemplate, String entityName, String item, String pkgRootPath, String sourcePath) {

    }
}
