package com.xgrt.annotation;

import com.xgrt.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 * 用于表示某个方法需要进行 功能字段自动填充处理
 */
@Target(ElementType.METHOD)//表示此注解只能加在方法上
@Retention(RetentionPolicy.RUNTIME)//固定写法
public @interface AutoFill {
    //指定当前数据库操作的类型: update insert
    OperationType value();
}
