package com.chat.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 统计service业务执行时间
 */
@Slf4j
@Aspect
@Component
public class ServiceLogAspect {

    @Around("execution(* com.chat.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        // joinPoint是一个AOP框架提供的对象，表示当前正在执行的连接点（即方法调用）
        // joinPoint.getTarget()返回当前被代理的目标对象
        // joinPoint.getTarget().getClass().getName()获取目标对象的类名
        // joinPoint.getSignature()返回一个描述当前连接点签名的对象
        // joinPoint.getSignature().getName()获取当前方法的名称
        String pointName = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
        stopWatch.start("执行主业务:" + pointName);
        // 需要统计每一个service实现的执行时间 如果执行时间太久 则进行error级别的日志输出
        Object proceed = joinPoint.proceed();
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        log.info(stopWatch.shortSummary());
        log.info("任务总数：" + stopWatch.getTaskCount());
        log.info("任务执行总时间：" + stopWatch.getTotalTimeMillis() + "ms");
        // 时间差
        long takeTimes = stopWatch.getTotalTimeMillis();
        if (takeTimes > 3000) {
            log.error("{}执行耗费了{}毫秒", pointName,takeTimes);
        } else if (takeTimes > 2000) {
            log.warn("{}执行耗费了{}毫秒", pointName,takeTimes);
        } else {
            log.info("{}执行耗费了{}毫秒", pointName,takeTimes);
        }
        return proceed;
    }

//    @Around("execution(* com.chat.service.impl..*.*(..))")
//    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
//        // 开始时间
//        long begin = System.currentTimeMillis();
//        // 需要统计每一个service实现的执行时间 如果执行时间太久 则进行error级别的日志输出
//        Object proceed = joinPoint.proceed();
//        // joinPoint是一个AOP框架提供的对象，表示当前正在执行的连接点（即方法调用）
//        // joinPoint.getTarget()返回当前被代理的目标对象
//        // joinPoint.getTarget().getClass().getName()获取目标对象的类名
//        // joinPoint.getSignature()返回一个描述当前连接点签名的对象
//        // joinPoint.getSignature().getName()获取当前方法的名称
//        String pointName = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
//        // 结束时间
//        long end = System.currentTimeMillis();
//        // 时间差
//        long takeTimes = end - begin;
//        if (takeTimes > 3000) {
//            log.error("{}执行耗费了{}毫秒", pointName,takeTimes);
//        } else if (takeTimes > 2000) {
//            log.warn("{}执行耗费了{}毫秒", pointName,takeTimes);
//        } else {
//            log.info("{}执行耗费了{}毫秒", pointName,takeTimes);
//        }
//        return proceed;
//    }
}