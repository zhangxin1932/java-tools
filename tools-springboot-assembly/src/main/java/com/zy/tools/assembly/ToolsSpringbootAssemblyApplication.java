package com.zy.tools.assembly;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zy.tools.assembly.mapper")
public class ToolsSpringbootAssemblyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolsSpringbootAssemblyApplication.class, args);
    }

}

