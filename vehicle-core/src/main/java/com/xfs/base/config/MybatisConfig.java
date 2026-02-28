package com.xfs.base.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//表示扫描 com.xfs.*.mapper下的 Mapper 接口,并自动将其注册为 Spring Bean
@MapperScan("com.xfs.*.mapper")
public class MybatisConfig {
}
