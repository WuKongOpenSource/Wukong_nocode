package com.kakarote.module;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zhangzhiwei
 */
@SpringBootApplication
@MapperScan(basePackages = "com.kakarote.module.mapper")
@EnableFeignClients(basePackages = {"com.kakarote.module"})
public class ModuleApplication {
	public static void main(String[] args) {
		SpringApplication.run(ModuleApplication.class, args);
	}
}
