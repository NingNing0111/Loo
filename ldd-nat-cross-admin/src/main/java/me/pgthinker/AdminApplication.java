package me.pgthinker;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Project: me.pgthinker
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/14 22:05
 * @Description:
 */
@SpringBootApplication
@EnableSpringUtil
@EnableScheduling
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class,args);
    }
}
