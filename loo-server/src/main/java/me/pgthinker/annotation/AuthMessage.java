package me.pgthinker.annotation;

import java.lang.annotation.*;

/**
 * @Project: me.pgthinker.annotation
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/12 10:45
 * @Description:
 */

@Target(ElementType.METHOD)//作用在参数和方法上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented//表明这个注解应该被 javadoc工具记录
public @interface AuthMessage {
}
