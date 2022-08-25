package com.lyx.codegen.core;

import com.lyx.codegen.builder.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 生成器最核心的类，保存了实体类、service、mapper的输出路径以及数据库的链接信息
 * @author 黎勇炫
 * @date 2022年08月19日 11:17
 */
@Data
@Accessors(chain = true)
public class Generate {

    private static final String SHOWTABLES = "show tables";

    /** jdbc */
    private JdbcTemplate jdbcTemplate;
    /** 实体类 */
    private String domainPath;
    /** service */
    private String servicePath;
    /** dao */
    private String daoPath;
    /** controller */
    private String controllerPath;
    /** 是否使用lombok */
    private boolean lombok;
    /** 所有表名 */
    private List<String> table;
    /** 表前缀 */
    private String tablePrefix;
     /**
       * 包路径(entity,controller,service包的父包)
       */
    private String pkgRootPath;
     /**
       * 资源路径
       */
    private String sourcePath;

    private BuilderChain chain;

    public Generate(DataSource dataSource,String pkgRootPath,String sourcePath) {
        this.pkgRootPath = pkgRootPath;
        this.sourcePath = sourcePath;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
       * 开始生成代码
       */
    public void doCreate(){
        // 1.在数据库中查询所有的表
        table = jdbcTemplate.queryForList(SHOWTABLES,String.class);
        // 处理表名
        table = table.stream().map(item -> {
            String entityName = buildEntityName(item);
            // 从新建实体类开始
            chain = createChain(jdbcTemplate,entityName,item,pkgRootPath,sourcePath);
            chain.build(jdbcTemplate, entityName, item, pkgRootPath, sourcePath);
            return entityName;
        }).collect(Collectors.toList());
    }

     /**
       * 创建生成类的调用链 实体类-dao-service-controller
       */
    private BuilderChain createChain(JdbcTemplate jdbcTemplate, String entityName, String item, String pkgRootPath, String sourcePath) {
        EntityBuilder chain = new EntityBuilder();
        chain.appendNext(new DaoBuilder())
                .appendNext(new ServiceBuilder())
                .appendNext(new ControllerBuilder());
        return chain;
    }

    /**
       * 构建实体类名称
       */
    private String buildEntityName(String name) {
        // 替换前缀
        if (!StringUtils.isEmpty(tablePrefix)) {
            name = name.replaceFirst(tablePrefix,"");
        }
        // 首字母大写
        // 驼峰命名
        String[] hump = name.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : hump) {
            builder.append(s.substring(0,1).toUpperCase()+s.substring(1));
        }

        return builder.toString();
    }

}
