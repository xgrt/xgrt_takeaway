package com.xgrt.Aspect;

import com.xgrt.annotation.AutoFill;
import com.xgrt.constant.AutoFillConstant;
import com.xgrt.context.BaseContext;
import com.xgrt.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面
 * 实现公共字段自动填充
 */
@Aspect
@Component
@Slf4j//用于记录日志
public class AutoFillAspect {
    //定义 切入点、通知

    /**
     * 切入点
     */
    @Pointcut(" execution(* com.xgrt.mapper.*.*(..)) && @annotation(com.xgrt.annotation.AutoFill)")
    public void autoFillCut(){}

    /**
     * 前置通知
     * 需要传入连接点
     * 公共字段赋值
     */

    @Before("autoFillCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段填充……");

        //获取到 当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();//获取方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库注解类型

        //获取到 当前被拦截的方法的参数——实体对象
        Object[] args = joinPoint.getArgs();//要把 实体对象 作为方法的第一个参数
        if (args==null || args.length==0){
            return;//做一个保险
        }
        Object entity = args[0];

        //准备需要赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //针对 当前不同的操作类型，为对应的 属性 赋值（通过反射实现）
        if (operationType==OperationType.INSERT){
            //为4个公共字段命名
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为 对象属性 赋值、
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if (operationType==OperationType.UPDATE){
            //为2个公共字段命名
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为 对象属性 赋值、
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}
