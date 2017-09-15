package com.zdf.zrouter.complier.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zdf.zrouter.anno.ZService;
import com.zdf.zrouter.complier.common.Constant;
import com.zdf.zrouter.complier.model.FieldAttr;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * 用于生成各种注入方法的类
 *
 * Created by xiaofeng on 2017/9/4.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AspectProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ZService.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 获取所有被注解的元素（这里是所有被注释了@ZService的Service变量）
        List<Element> allAnnotatedElements = getAllAnnotatedElements(annotations, roundEnv);

        // 生成类和方法列表的键值对
        Map<TypeElement, List<FieldAttr>> classesMap = makeClassesMap(allAnnotatedElements);

        // 生成Aspect类，专门用于代码注入
        generateClass(roundEnv, classesMap);

        return false;
    }

    /**
     * 获取所有被注解的元素列表
     *
     * @param annotations 所有本类支持的注解
     * @param roundEnv    注解上下环境
     * @return 所有被注解的元素列表
     */
    private List<Element> getAllAnnotatedElements(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<Element> elements = new ArrayList<>();
        for (TypeElement anno : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(anno);
            for (Element annotatedElement : annotatedElements) {
                if (!elements.contains(annotatedElement)) {
                    elements.add(annotatedElement);
                }
            }
        }
        return elements;
    }

    /**
     * 构造所有类和方法列表的Map
     *
     * @param elements 所有被注解的元素（方法）
     * @return Map(类元素, 变量元素列表)
     */
    private Map<TypeElement, List<FieldAttr>> makeClassesMap(List<Element> elements) {
        Map<TypeElement, List<FieldAttr>> map = new HashMap<>();
        for (Element element : elements) {
            if (isElementValid(element)) {
                TypeElement classElement = (TypeElement) element.getEnclosingElement();
                List<FieldAttr> attrList;
                if (map.containsKey(classElement)) {
                    attrList = map.get(classElement);
                } else {
                    attrList = new ArrayList<>();
                    map.put(classElement, attrList);
                }
                attrList.add(new FieldAttr(element));
            }
        }
        return map;
    }

    /**
     * 判断被注解的元素是否合法
     * <p>
     * 1. 注解只能修饰变量（FIELD）
     * 2. 变量类型必须以“Service”结尾
     * 3. 变量类型必须是接口（INTERFACE）
     *
     * @param element 被注解的元素
     * @return 是否合法
     */
    private boolean isElementValid(Element element) {
        ElementKind kind = element.getKind();
        if (kind != ElementKind.FIELD) {
            logger.e("Only fields can be with " + getSupportedAnnotationTypes(), element);
            return false;
        }

        // ExecutableElement methodElement = (ExecutableElement) element;
        // if (!isSupportReturnType(methodElement)) {
        //     logger.error("method only support return type is " + SUPPORT_RETURN_TYPE.toString(), methodElement);
        //     return false;
        // }

        VariableElement fieldElement = (VariableElement) element;
        Element fieldType = typeUtils.asElement(fieldElement.asType());
        String fieldTypeName = fieldType.getSimpleName().toString();
        if (!fieldTypeName.endsWith("Service")) {
            logger.e("[" + fieldTypeName + "] this field type must be in end with Service");
            return false;
        }

        if (fieldType.getKind() != ElementKind.INTERFACE) {
            logger.e("[" + fieldTypeName + "] this field type must be interface");
            return false;
        }

        return true;
    }

    /**
     * 生成类文件
     *
     * @param roundEnv
     * @param classesMap
     */
    private void generateClass(RoundEnvironment roundEnv, Map<TypeElement, List<FieldAttr>> classesMap) {
        if (!classesMap.isEmpty()) {
            TypeSpec typeSpec = TypeSpec.classBuilder(Constant.SERVICE_ASPECT_CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(AnnotationSpec.builder(Aspect.class).build())
                    .addMethods(generateAspectMethodList(classesMap))
                    .build();
            writeClassFile(getRootPackageName(roundEnv), typeSpec);
        }
    }

    /**
     * 生成用于注入的所有方法列表
     *
     * @param classesMap
     * @return
     */
    private List<MethodSpec> generateAspectMethodList(Map<TypeElement, List<FieldAttr>> classesMap) {
        List<MethodSpec> methodList = new ArrayList<>();
        for (Map.Entry<TypeElement, List<FieldAttr>> classSet : classesMap.entrySet()) {
            methodList.add(generateConstructorAround(classSet.getKey(), classSet.getValue()));
        }
        return methodList;
    }

    /**
     * 生成用于注入构造方法的方法
     *
     * @param classElement
     * @param attrList
     * @return
     */
    private MethodSpec generateConstructorAround(TypeElement classElement, List<FieldAttr> attrList) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T target = ($T) joinPoint.getTarget()", classElement, classElement);
        for (FieldAttr attr : attrList) {
            String ServiceImplName = getFieldTypeFullName(attr.getFieldElement()) + Constant.SERVICE_CLASS_SUFFIX;
            builder.addStatement("target.$L = new $L(target)", attr.getFieldElement(), ServiceImplName);
        }
        builder.addStatement("return joinPoint.proceed()");

        String methodName = "aroundConstructor_" + classElement.getQualifiedName().toString().replace(".", "_") + "_new";
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ProceedingJoinPoint.class, "joinPoint")
                .returns(Object.class)
                .addException(Throwable.class)
                .addAnnotation(AnnotationSpec.builder(Around.class).addMember("value", "$S", "execution(" + classElement.getQualifiedName() + ".new(..))").build())
                .addCode(builder.build())
                .build();
    }

    /**
     * 生成用于注入OnCreate的方法
     *
     * @param classElement
     * @param attrList
     * @return
     */
    private MethodSpec generateMethodAroundOnCreate(TypeElement classElement, List<FieldAttr> attrList) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T target = ($T) joinPoint.getTarget()", classElement, classElement);
        for (FieldAttr attr : attrList) {
            String ServiceImplName = getFieldTypeFullName(attr.getFieldElement()) + Constant.SERVICE_CLASS_SUFFIX;
            builder.addStatement("target.$L = new $L(target)", attr.getFieldElement(), ServiceImplName);
        }
        builder.addStatement("return joinPoint.proceed()");

        String methodName = "aroundMethod_" + classElement.getQualifiedName().toString().replace(".", "_") + "_onCreate";
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ProceedingJoinPoint.class, "joinPoint")
                .returns(Object.class)
                .addException(Throwable.class)
                .addAnnotation(AnnotationSpec.builder(Around.class).addMember("value", "$S", "execution(* " + classElement.getQualifiedName() + ".onCreate(..))").build())
                .addCode(builder.build())
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

    private MethodSpec generateCreateMethod(TypeElement classElement, VariableElement fieldElement) {
        ClassName ZRouter = ClassName.get("com.zdf.zrouter.api", "ZRouter");
        String ServiceImplName = typeUtils.asElement(fieldElement.asType()).getSimpleName() + "Impl";
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        ClassName ServiceImpl = ClassName.get(packageName, ServiceImplName);

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
        // builder.addStatement("return new $T()", ServiceImpl);
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
}
