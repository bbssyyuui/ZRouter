package com.zdf.zrouter.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zdf.zrouter.anno.ZService;
import com.zdf.zrouter.processor.util.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by xiaofeng on 2017/9/4.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AspectProcessor extends AbstractProcessor {

    private Filer filer;
    private Logger logger;
    private Types types;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new Logger(processingEnv.getMessager());
        filer = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ZService.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        VariableElement fieldElement = null;
        TypeElement classElement = null;
        PackageElement packageElement = null;
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(ZService.class);
        for (Element element : annotatedElements) {
            fieldElement = (VariableElement) element;
            classElement = (TypeElement) element.getEnclosingElement();
            packageElement = elements.getPackageOf(classElement);
        }

        if (fieldElement != null) {
            TypeSpec classOfService = generateClass(classElement, fieldElement);
            writeClassFile("com.zdf.zrouter", classOfService);
        }

        return false;
    }

    private TypeSpec generateClass(TypeElement classElement, VariableElement fieldElement) {
        return TypeSpec.classBuilder("AspectMaker")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Aspect.class).build())
//                .addMethod(generateOnCreateMethod(classElement, fieldElement))
//                .addMethod(generateCreateMethod(fieldElement))
                .addMethod(generateOnCreateMethod2(classElement, fieldElement))
                .build();
    }

    private MethodSpec generateOnCreateMethod(TypeElement classElement, VariableElement fieldElement) {
        ClassName ZRouter = ClassName.get("com.zdf.zrouter.api", "ZRouter");

        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T target = ($T)joinPoint.getTarget()", classElement, classElement);
        builder.addStatement("target.$L = $T.newInstance(target).create($T.class)", fieldElement, ZRouter, fieldElement);
        builder.addStatement("return joinPoint.proceed()");

        return MethodSpec.methodBuilder("aroundOnCreateMethod")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ProceedingJoinPoint.class, "joinPoint")
                .returns(Object.class)
                .addException(Throwable.class)
                .addAnnotation(AnnotationSpec.builder(Around.class).addMember("value", "$S", "execution(* " + classElement.getQualifiedName() + ".onCreate(..))").build())
                .addCode(builder.build())
                .build();
    }

    private MethodSpec generateOnCreateMethod2(TypeElement classElement, VariableElement fieldElement) {
        String ServiceImplName = types.asElement(fieldElement.asType()).getSimpleName() + "Impl";
        ClassName ServiceImpl = ClassName.get("com.zdf.zrouter", ServiceImplName);

        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T target = ($T)joinPoint.getTarget()", classElement, classElement);
        builder.addStatement("target.$L = new $T(target)", fieldElement, ServiceImpl);
        builder.addStatement("return joinPoint.proceed()");

        return MethodSpec.methodBuilder("aroundOnCreateMethod")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ProceedingJoinPoint.class, "joinPoint")
                .returns(Object.class)
                .addException(Throwable.class)
                .addAnnotation(AnnotationSpec.builder(Around.class).addMember("value", "$S", "execution(* " + classElement.getQualifiedName() + ".onCreate(..))").build())
                .addCode(builder.build())
                .build();
    }

    private MethodSpec generateCreateMethod(VariableElement fieldElement) {
        ClassName ZRouter = ClassName.get("com.zdf.zrouter.api", "ZRouter");
        String ServiceImplName = types.asElement(fieldElement.asType()).getSimpleName() + "Impl";
        ClassName ServiceImpl = ClassName.get("com.zdf.zrouter", ServiceImplName);

        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T target = ($T)joinPoint.getTarget()", ZRouter, ZRouter);
        builder.beginControlFlow("if (joinPoint.getArgs() == null || joinPoint.getArgs().length != 1)");
        builder.addStatement("return joinPoint.proceed()");
        builder.endControlFlow();
        builder.addStatement("$T arg = joinPoint.getArgs()[0]", Object.class);
        builder.beginControlFlow("if (arg instanceof Class)");
        builder.addStatement("$T buildClass = ($T) arg", Class.class, Class.class);
        builder.beginControlFlow("if (buildClass.isAssignableFrom($T.class))", fieldElement);
        builder.addStatement("return new $T(target.getContext())", ServiceImpl);
//        builder.addStatement("return new $T()", ServiceImpl);
        builder.endControlFlow();
        builder.endControlFlow();
        builder.addStatement("return joinPoint.proceed()");

        return MethodSpec.methodBuilder("aroundCreateMethod")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ProceedingJoinPoint.class, "joinPoint")
                .returns(Object.class)
                .addException(Throwable.class)
                .addAnnotation(AnnotationSpec.builder(Around.class).addMember("value", "$S", "execution(* com.zdf.zrouter.api.ZRouter.create(..))").build())
                .addCode(builder.build())
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
