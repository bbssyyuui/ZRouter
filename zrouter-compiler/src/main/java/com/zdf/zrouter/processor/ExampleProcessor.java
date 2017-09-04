package com.zdf.zrouter.processor;

import com.google.auto.service.AutoService;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Url;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by xiaofeng on 2017/9/3.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ExampleProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Activity.class.getCanonicalName());
        types.add(Url.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Activity.class)) {
            // 所有被使用的@Activity

            // 日志
            printLog(element);
        }


        for (Element element : roundEnv.getElementsAnnotatedWith(Activity.class)) {
            //Activity.class是@Target(METHOD)
            //则该element是可以强转为表示方法的ExecutableElement
            ExecutableElement method = (ExecutableElement) element;
            //如果需要用到其他类型的Element，则不可以直接强转，需要通过下面方法转换
            //但有例外情况，我们稍后列举
            TypeElement classElement = (TypeElement) element.getEnclosingElement();
        }


        // Activity.class 以 @Target(ElementType.METHOD)修饰
        for (Element element : roundEnv.getElementsAnnotatedWith(Activity.class)) {
            // 对于Element直接强转
            ExecutableElement executableElement = (ExecutableElement) element;

            // 非对应的Element，通过getEnclosingElement转换获取
            TypeElement classElement = (TypeElement) element.getEnclosingElement();

            // 当(ExecutableElement) element成立时，使用(PackageElement) element.getEnclosingElement();将报错。
            // 需要使用elementUtils来获取
            Elements elementUtils = processingEnv.getElementUtils();
            PackageElement packageElement = elementUtils.getPackageOf(classElement);

            // 全类名
            String fullClassName = classElement.getQualifiedName().toString();
            // 类名
            String className = classElement.getSimpleName().toString();
            // 包名
            String packageName = packageElement.getQualifiedName().toString();
            // 方法名
            String methodName = executableElement.getSimpleName().toString();

            // 取得方法参数列表
            List<? extends VariableElement> methodParameters = executableElement.getParameters();
            // 参数类型列表
            List<String> types = new ArrayList<>();
            for (VariableElement variableElement : methodParameters) {
                TypeMirror methodParameterType = variableElement.asType();
                if (methodParameterType instanceof TypeVariable) {
                    TypeVariable typeVariable = (TypeVariable) methodParameterType;
                    methodParameterType = typeVariable.getUpperBound();

                }
                // 参数名
                String parameterName = variableElement.getSimpleName().toString();
                // 参数类型
                String parameteKind = methodParameterType.toString();
                types.add(methodParameterType.toString());
            }
        }

        return false;
    }

    private void printLog(Element element) {
        //取得Messager对象
        Messager messager = processingEnv.getMessager();
        //输出日志
        messager.printMessage(Diagnostic.Kind.NOTE,
                "Annotation class : className = " + element.getSimpleName().toString());
    }
}
