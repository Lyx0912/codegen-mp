package com.lyx.codegen.builder;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
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

    public void createMapperFile(){
        // todo 生成mapper文件
    }

    public abstract void build(JdbcTemplate jdbcTemplate, String entityName, String item, String pkgRootPath, String sourcePath);

}
