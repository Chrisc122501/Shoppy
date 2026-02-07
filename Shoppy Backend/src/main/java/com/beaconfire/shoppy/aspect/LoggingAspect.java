package com.beaconfire.shoppy.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // define a pointcut for all methods in services
    @Pointcut("execution(* com.beaconfire.onlineshopping.service.*.*(..))")
    public void serviceMethods() {}

    // log before method execution
    @Before("serviceMethods()")
    public void logBeforeMethod() {
        System.out.println("Method execution started...");
    }

    // log after method execution returns successfully
    @AfterReturning("serviceMethods()")
    public void logAfterReturning() {
        System.out.println("Method executed successfully...");
    }

    // log when a method throws an exception
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        System.out.println("Method threw an exception: " + ex.getMessage());
    }
}