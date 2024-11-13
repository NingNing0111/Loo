package me.pgthinker;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Project: me.pgthinker
 * @Author: De Ning
 * @Date: 2024/10/26 19:08
 * @Description:
 */
@SpringBootApplication
@EnableSpringUtil
public class ClientApp  {

    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }

}
