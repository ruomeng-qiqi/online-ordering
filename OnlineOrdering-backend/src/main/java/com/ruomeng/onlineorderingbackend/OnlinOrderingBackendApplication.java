package com.ruomeng.onlineorderingbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ruomeng.onlineorderingbackend.mapper")
public class OnlinOrderingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlinOrderingBackendApplication.class, args);
    }

}
