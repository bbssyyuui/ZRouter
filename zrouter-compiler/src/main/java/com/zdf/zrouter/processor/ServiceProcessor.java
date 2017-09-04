package com.zdf.zrouter.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Url;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;

/**
 * Created by xiaofeng on 2017/9/3.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ServiceProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Activity.class.getCanonicalName());
        types.add(Url.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        List<MethodSpec> methodSpecList = new ArrayList<>();

        TypeElement classElement = null;
        for (Element element : roundEnv.getElementsAnnotatedWith(Activity.class)) {
            // 对于Element直接强转
            ExecutableElement executableElement = (ExecutableElement) element;

            // 非对应的Element，通过getEnclosingElement转换获取
            classElement = (TypeElement) element.getEnclosingElement();

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

            Activity activity = element.getAnnotation(Activity.class);



            TypeMirror targetClass = null;
            try {
                activity.value();
            } catch (MirroredTypeException mte) {
                targetClass = mte.getTypeMirror();
            }


            methodSpecList.add(methodOfStartMainActivity(methodName, targetClass));
        }


        if (classElement != null) {
            methodSpecList.add(methodOfAOPAround(classElement));

            TypeSpec classOfService = classOfService(classElement, methodSpecList);
            writeClassFile("com.zdf.zrouter", classOfService);
        }

        return false;
    }

    private MethodSpec methodOfStartMainActivity(String methodName, TypeMirror targetClass) {
        ClassName intent = ClassName.get("android.content", "Intent");
        ClassName context = ClassName.get("android.content", "Context");

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addParameter(context, "context")
//                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .addStatement("$T intent = new $T()", intent, intent)
                .addStatement("intent.setClass(context, $T.class)", targetClass)
                .addStatement("context.startActivity(intent)")
                .build();
    }

    private MethodSpec methodOfAOPAround(TypeElement classElement) {
//        ClassName ZRouter = ClassName.get("com.zdf.zrouter.api", "ZRouter");
        ClassName ServiceImpl = ClassName.get("com.zdf.zrouter", classElement.getSimpleName() + "Impl");

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
//        codeBlockBuilder.add("$T target = ($T)joinPoint.getTarget();\n", ZRouter, ZRouter);
        codeBlockBuilder.beginControlFlow("if (joinPoint.getArgs() == null || joinPoint.getArgs().length != 1)");
        codeBlockBuilder.add("return joinPoint.proceed();\n");
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.add("$T arg = joinPoint.getArgs()[0];\n", Object.class);
        codeBlockBuilder.beginControlFlow("if (arg instanceof Class)");
        codeBlockBuilder.add("$T buildClass = ($T) arg;\n", Class.class, Class.class);
        codeBlockBuilder.beginControlFlow("if (buildClass.isAssignableFrom(getClass()))");
//        codeBlockBuilder.add("return new $T(target);\n", ServiceImpl);
        codeBlockBuilder.add("return new $T();\n", ServiceImpl);
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.add("return joinPoint.proceed();\n");

        return MethodSpec.methodBuilder("aroundCreateMethod")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ProceedingJoinPoint.class, "joinPoint")
                .returns(Object.class)
                .addException(Throwable.class)
                .addAnnotation(AnnotationSpec.builder(Around.class).addMember("value", "$S", "execution(* com.zdf.zrouter.api.ZRouter.create(..))").build())
                .addCode(codeBlockBuilder.build())
                .build();
    }

    private TypeSpec classOfService(TypeElement classElement, List<MethodSpec> methodSpecList) {
        return TypeSpec.classBuilder(classElement.getSimpleName() + "Impl")
                .addSuperinterface(ClassName.get(classElement))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Aspect.class).build())
                .addMethods(methodSpecList)
                .build();
    }

    private void writeClassFile(String packageName, TypeSpec typeSpec) {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
