package me.pgthinker.annotation;

import me.pgthinker.enums.CmdTypeProto.CmdType;

import java.lang.annotation.*;

/**
 * @Project: me.pgthinker.annotation
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/14 12:21
 * @Description:
 */
@Target(ElementType.METHOD)//作用在参数和方法上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented//表明这个注解应该被 javadoc工具记录
public @interface MessageLog {
}
