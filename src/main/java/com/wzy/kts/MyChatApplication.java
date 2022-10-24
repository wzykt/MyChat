package com.wzy.kts;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.wzy.kts.dao.mapper")
public class MyChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyChatApplication.class, args);
    }
}
