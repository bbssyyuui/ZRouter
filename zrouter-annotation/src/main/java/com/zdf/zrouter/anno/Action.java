package com.zdf.zrouter.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiaofeng on 2017/9/5.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Action {
    String value() default "";
}
