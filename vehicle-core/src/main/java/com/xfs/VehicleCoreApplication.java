package com.xfs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.xfs.**.mapper")
@SpringBootApplication
public class VehicleCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(VehicleCoreApplication.class, args);
    }
}
