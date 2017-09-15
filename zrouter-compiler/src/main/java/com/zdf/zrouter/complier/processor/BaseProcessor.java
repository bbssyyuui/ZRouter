package com.zdf.zrouter.complier.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.zdf.zrouter.complier.common.Constant;
import com.zdf.zrouter.complier.util.Logger;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by xiaofeng on 2017/9/13.
 */

public abstract class BaseProcessor extends AbstractProcessor {

    protected Elements elementUtils;
    protected Types typeUtils;
    protected Filer filer;
    protected Logger logger;

    /**
     * 初始化
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        logger = new Logger(processingEnv.getMessager());
    }

    public String getRootPackageName(RoundEnvironment roundEnv) {
        String pn = Constant.COMMON_PACKAGE_NAME;
        Set<? extends Element> elementSet = roundEnv.getRootElements();
        for (Element element : elementSet) {
            String className = element.getSimpleName().toString();
            if (className.equals("BuildConfig")) {
                int index = element.asType().toString().indexOf(className);
                if (index > 1) {
                    pn = element.asType().toString().substring(0, index - 1);
                    break;
                }
            }
        }
        logger.v("###### pn = " + pn);
        return pn;
    }

    /**
     * 获取指定类的包名
     *
     * @param classElement
     * @return
     */
    public String getPackageName(TypeElement classElement) {
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        return packageElement.getQualifiedName().toString();
    }

    /**
     * 获取变量类型的全名（包括包名）
     *
     * @param fieldElement
     * @return
     */
    public String getFieldTypeFullName(VariableElement fieldElement) {
        return fieldElement.asType().toString();
    }

    /**
     * 获取变量类型的简名
     *
     * @param fieldElement
     * @return
     */
    public String getFieldTypeSimpleName(VariableElement fieldElement) {
        return typeUtils.asElement(fieldElement.asType()).getSimpleName().toString();
    }

    /**
     * 写类文件
     *
     * @param packageName
     * @param typeSpec
     */
    protected void writeClassFile(String packageName, TypeSpec typeSpec) {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
