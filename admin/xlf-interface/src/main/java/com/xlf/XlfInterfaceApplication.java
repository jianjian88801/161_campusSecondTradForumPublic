package com.xlf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.xlf.system.mapper")
@EnableTransactionManagement
@EnableScheduling
public class XlfInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(XlfInterfaceApplication.class, args);
    }

}
