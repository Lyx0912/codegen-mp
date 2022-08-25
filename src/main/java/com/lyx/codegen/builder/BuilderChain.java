package com.lyx.codegen.builder;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author 黎勇炫
 * @date 2022年08月22日 15:17
 */
@Data
public abstract class BuilderChain {

     /**
       * 调用链的下一个构建者
       */
    protected BuilderChain next;
    public static final String RESOURCE_PATH = "/src/main/resources";

    public BuilderChain appendNext(BuilderChain next){
        this.next = next;
        return next;
    }

    public void genJavaSourceFile(String packageName, String pathStr,
                                  TypeSpec.Builder typeSpecBuilder) {
        TypeSpec typeSpec = typeSpecBuilder.build();
        JavaFile javaFile = JavaFile
                .builder(packageName, typeSpec)
                .build();
//    System.out.println(javaFile);
        String packagePath =
                packageName.replace(".", File.separator) + File.separator + typeSpec.name + ".java";
        try {
            Path path = Paths.get(pathStr);
            File file = new File(path.toFile().getAbsolutePath());
            if(!file.exists()){
                return;
            }
            String sourceFileName = path.toFile().getAbsolutePath() + File.separator + packagePath;
            File sourceFile = new File(sourceFileName);
            if (!sourceFile.exists()) {
                javaFile.writeTo(file);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

     /**
       * 创建mapper文件
       */
    public void createMapperFile(String namespace,String fileName){
        Document document = DocumentHelper.createDocument();
        // 创建docmentType
        document.addDocType("mapper",
                "-//mybatis.org//DTD Mapper 3.0//EN",
                "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        // 添加根节点
        Element root = document.addElement("mapper");
        root.addAttribute("namespace",namespace);
        root.setText("\n\t");

        // 输出xml文件
        try {
            // 创建格式化类
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式，默认UTF-8
            format.setEncoding("UTF-8");
            File file = new File(System.getProperty("user.dir")+RESOURCE_PATH+File.separator+"mapper");
            if(!file.exists()){
                file.mkdir();
            }
            // 创建输出流，此处要使用Writer，需要指定输入编码格式，使用OutputStream则不用
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath()+File.separator+fileName+".xml");
            // 创建xml输出流
            XMLWriter writer = new XMLWriter(fos, format);
            // 生成xml文件
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void build(JdbcTemplate jdbcTemplate, String entityName, String item, String pkgRootPath, String sourcePath);

}
