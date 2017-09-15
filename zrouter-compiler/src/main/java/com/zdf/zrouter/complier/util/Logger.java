package com.zdf.zrouter.complier.util;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * 日志工具类
 * <p>
 * Created by xiaofeng on 2017/9/13.
 */

public class Logger {
    private Messager msg;

    public Logger(Messager messager) {
        msg = messager;
    }

    public void v(CharSequence other) {
        msg.printMessage(Diagnostic.Kind.OTHER, other);
    }

    public void v(CharSequence other, Element element) {
        msg.printMessage(Diagnostic.Kind.OTHER, other, element);
    }

    public void i(CharSequence info) {
        msg.printMessage(Diagnostic.Kind.NOTE, info);
    }

    public void i(CharSequence info, Element element) {
        msg.printMessage(Diagnostic.Kind.NOTE, info, element);
    }

    public void w(CharSequence warning) {
        msg.printMessage(Diagnostic.Kind.WARNING, warning);

    }

    public void w(CharSequence warning, Element element) {
        msg.printMessage(Diagnostic.Kind.WARNING, warning, element);

    }

    public void e(CharSequence error) {
        msg.printMessage(Diagnostic.Kind.ERROR, error);
    }

    public void e(CharSequence error, Element element) {
        msg.printMessage(Diagnostic.Kind.ERROR, error, element);
    }

    public void e(Throwable error) {
        msg.printMessage(Diagnostic.Kind.ERROR, "An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()));
    }

    public void e(Throwable error, Element element) {
        msg.printMessage(Diagnostic.Kind.ERROR, "An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()), element);
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
