//package com.zdf.zrouter.aspectjx;
//
//import android.util.Log;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//
///**
// * Created by xiaofeng on 2017/9/1.
// */
//
//@Aspect
//public class MethodAspect {
//    private static final String TAG = "MethodAspect";
//
//    @Around("execution(* com.zdf.zrouter.api.ZRouter.create(..))")
//    public Object aroundCreateMethod(ProceedingJoinPoint joinPoint) throws Throwable {
////        ZRouter target = (ZRouter)joinPoint.getTarget();
////        if (joinPoint.getArgs() == null || joinPoint.getArgs().length != 1) {
////            return joinPoint.proceed();
////        }
////        Object arg = joinPoint.getArgs()[0];
////        if (arg instanceof Class) {
////            Class buildClass = (Class) arg;
////            if (buildClass.isAssignableFrom(getClass())) {
////                return new RouterServiceImpl(target);
////            }
////        }
////        return joinPoint.proceed();
//
//        Log.v("zdf", "AOP aroundCreateMethod");
//        return joinPoint.proceed();
//    }
//}
