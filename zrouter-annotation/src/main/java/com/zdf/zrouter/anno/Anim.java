package com.zdf.zrouter.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiaofeng on 2017/9/8.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Anim {
    int in();
    int out();
}
