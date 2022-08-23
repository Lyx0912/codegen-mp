package com.lyx.codegen.builder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.codegen.mapper.DaoCodeGenProcessor;
import com.lyx.codegen.mapper.GenDao;
import com.lyx.codegen.service.GenService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 黎勇炫
 * @date 2022年08月23日 15:37
 */
public class ServiceBuilder extends BuilderChain{

     /**
       * service后缀
       */
    public static final String SERVICE_SUFFIX = "Service";
    /**
     * serviceimpl后缀
     */
    public static final String SERVICEIMPL_SUFFIX = "ServiceImpl";
    /**
     * service包后缀
     */
    public static final String SERVICE_PKG_SUFFIX = ".service";
    /**
     * serviceimpl包后缀
     */
    public static final String SERVICEIMPL_PKG_SUFFIX = ".service.impl";


    @Override
    public void build(JdbcTemplate jdbcTemplate, String entityName, String tableName, String pkgRootPath, String sourcePath) {

        Class<IService> service = IService.class;
        Class<ServiceImpl> serviceImpl = ServiceImpl.class;
        // 创建inteface
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(entityName + SERVICE_SUFFIX)
                // 继承iservice接口
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(service), ClassName.get(pkgRootPath+EntityBuilder.ENTITY_PKG_SUFFID,entityName)))
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(entityName+"\n@author 黎勇炫 \n@Date "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // 生成service接口
        genJavaSourceFile(pkgRootPath+SERVICE_PKG_SUFFIX, sourcePath, builder);
        // 加载刚才生成的service接口，在生成实现类的时候继承这个接口
        // 创建impl
        TypeSpec.Builder impl = null;
        impl = TypeSpec.classBuilder(entityName + SERVICEIMPL_SUFFIX)
                .superclass(ParameterizedTypeName.get(ClassName.get(serviceImpl), ClassName.get(pkgRootPath+DaoBuilder.DAO_PKG_SUFFIX,entityName+DaoBuilder.DAO_SUFFIX), ClassName.get(pkgRootPath+EntityBuilder.ENTITY_PKG_SUFFID,entityName)))
                .addSuperinterface(ClassName.get(pkgRootPath+SERVICE_PKG_SUFFIX,entityName+SERVICE_SUFFIX))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Service.class)
                .addJavadoc(entityName+"\n@author 黎勇炫 \n@Date "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // 生成实现类
        genJavaSourceFile(pkgRootPath+SERVICEIMPL_PKG_SUFFIX + ".impl", sourcePath, impl);

        // todo 生成mapper文件

        if(null != this.next){
            this.next.build(jdbcTemplate,entityName,tableName,pkgRootPath,sourcePath);
        }
    }
}
