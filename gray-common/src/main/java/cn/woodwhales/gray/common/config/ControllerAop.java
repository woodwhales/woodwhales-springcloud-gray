package cn.woodwhales.gray.common.config;

import cn.woodwhales.gray.common.util.ControllerAopTool;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author woodwhales on 2022-12-29 9:13
 */
@Slf4j
@Aspect
@Component
public class ControllerAop {

    @Pointcut("execution (* cn.woodwhales.gray.*.controller..*.*(..))")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object providerAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        return ControllerAopTool.providerAspect(joinPoint);
    }

}
