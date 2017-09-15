package com.zdf.zrouter.aspectjx;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by xiaofeng on 2017/9/1.
 */

@Aspect
public class TestAspect {
    private static final String TAG = "MethodAspect";

    @Around("execution(* com.zdf.zrouter.api.ZRouter.create(..))")
    public Object aroundCreateMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//        ZRouter target = (ZRouter)joinPoint.getTarget();
//        if (joinPoint.getArgs() == null || joinPoint.getArgs().length != 1) {
//            return joinPoint.proceed();
//        }
//        Object arg = joinPoint.getArgs()[0];
//        if (arg instanceof Class) {
//            Class buildClass = (Class) arg;
//            if (buildClass.isAssignableFrom(getClass())) {
//                return new RouterServiceImpl(target);
//            }
//        }
//        return joinPoint.proceed();

        Log.v("zdf", "AOP aroundCreateMethod");
        return joinPoint.proceed();
    }

    // 构造函数注入
    @Around("execution(com.zdf.zrouter.MainActivity.new(..))")
    public Object aroundNewMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.v("zdf", "MainActivity.new");
        return joinPoint.proceed();
    }

    // 普通方法注入
    @Around("execution(* com.zdf.zrouter.MainActivity.onCreate(..))")
    public Object aroundOnCreateMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.v("zdf", "MainActivity.OnCreate");
        return joinPoint.proceed();
    }

    // 变量注入
    @Around("get(int com.zdf.zrouter.MainActivity.i)")
    public Object aroundIField(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.v("zdf", "MainActivity.i");
        return 10;
    }

    // 变量注入
    @Around("get(Animal com.zdf.zrouter.MainActivity.animal)")
    public Object aroundAnimalField(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.v("zdf", "MainActivity.animal");
        return new Animal();
    }
}
