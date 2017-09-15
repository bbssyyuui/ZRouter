package com.zdf.zrouter.complier.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zdf.zrouter.anno.Action;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Path;
import com.zdf.zrouter.anno.Url;
import com.zdf.zrouter.complier.common.Constant;
import com.zdf.zrouter.complier.model.FuncAttr;

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
import javax.lang.model.element.TypeElement;

/**
 * 用于生成各种Service类
 *
 * Created by xiaofeng on 2017/9/3.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ServiceProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Activity.class.getCanonicalName());
        types.add(Path.class.getCanonicalName());
        types.add(Url.class.getCanonicalName());
        types.add(Action.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // 获取所有被注解的元素（这里是所有Service接口的方法）
        List<Element> allAnnotatedElements = getAllAnnotatedElements(annotations, roundEnv);

        // 生成类和方法列表的键值对
        Map<TypeElement, List<FuncAttr>> classesMap = makeClassesMap(allAnnotatedElements);

        // 生成所有Service实现类
        generateAllClasses(classesMap);

        return false;
    }

    /**
     * 获取所有被注解的元素列表
     *
     * @param annotations 所有本类支持的注解
     * @param roundEnv 注解上下环境
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
     * @return Map(类元素, 方法属性列表)
     */
    private Map<TypeElement, List<FuncAttr>> makeClassesMap(List<Element> elements) {
        Map<TypeElement, List<FuncAttr>> map = new HashMap<>();
        for (Element element : elements) {
            if (isElementValid(element)) {
                TypeElement classElement = (TypeElement) element.getEnclosingElement();
                List<FuncAttr> attrList;
                if (map.containsKey(classElement)) {
                    attrList = map.get(classElement);
                } else {
                    attrList = new ArrayList<>();
                    map.put(classElement, attrList);
                }
                attrList.add(new FuncAttr(element));
            }
        }
        return map;
    }

    /**
     * 判断被注解的元素是否合法
     *
     * 1. 注解只能修饰方法（METHOD）
     * 2. 类名必须以“Service”结尾
     * 3. 类的类型必须是接口（INTERFACE）
     *
     * @param element 被注解的元素
     * @return 是否合法
     */
    private boolean isElementValid(Element element) {
        ElementKind kind = element.getKind();
        if (kind != ElementKind.METHOD) {
            logger.e("Only methods can be with " + getSupportedAnnotationTypes());
            return false;
        }

        // ExecutableElement methodElement = (ExecutableElement) element;
        // if (!isSupportReturnType(methodElement)) {
        //     logger.error("method only support return type is " + SUPPORT_RETURN_TYPE.toString());
        //     return false;
        // }

        Element classElement = element.getEnclosingElement();
        String className = classElement.getSimpleName().toString();
        if (!className.endsWith("Service")) {
            logger.e("[" + className + "] this class must be in end with Service");
            return false;
        }

        if (classElement.getKind() != ElementKind.INTERFACE) {
            logger.e("[" + className + "] this class must be interface");
            return false;
        }

        return true;
    }

    /**
     * 生成所有类文件
     *
     * @param classesMap
     */
    private void generateAllClasses(Map<TypeElement, List<FuncAttr>> classesMap) {
        for (Map.Entry<TypeElement, List<FuncAttr>> classSet : classesMap.entrySet()) {
            TypeSpec typeSpec = generateClass(classSet.getKey(), classSet.getValue());
            writeClassFile(getPackageName(classSet.getKey()), typeSpec);
        }
    }

    /**
     * 生成类
     *
     * @param classElement
     * @param attrList
     * @return
     */
    private TypeSpec generateClass(TypeElement classElement, List<FuncAttr> attrList) {
        return TypeSpec.classBuilder(classElement.getSimpleName() + Constant.SERVICE_CLASS_SUFFIX)
                .addSuperinterface(ClassName.get(classElement))
                .addModifiers(Modifier.PUBLIC)
                .addField(generateField())
                .addMethods(generateMethodList(attrList))
                .build();
    }

    /**
     * 生成类属性
     *
     * @return
     */
    private FieldSpec generateField() {
        ClassName context = ClassName.get("android.content", "Context");
        return FieldSpec.builder(context, "context", Modifier.PRIVATE).build();
    }

    /**
     * 生成类方法列表
     *
     * @param funcAttrList
     * @return
     */
    private List<MethodSpec> generateMethodList(List<FuncAttr> funcAttrList) {
        List<MethodSpec> methodList = new ArrayList<>();
        methodList.add(generateConstructMethod());
        for (FuncAttr funcAttr : funcAttrList) {
            methodList.add(generateMethod(funcAttr));
        }
        return methodList;
    }

    /**
     * 生成类构造方法
     *
     * @return
     */
    private MethodSpec generateConstructMethod() {
        ClassName context = ClassName.get("android.content", "Context");

        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(context, "context")
                .addStatement("this.context = context")
                .build();
    }

    /**
     * 生成类方法
     *
     * @param funcAttr
     * @return
     */
    private MethodSpec generateMethod(FuncAttr funcAttr) {
        ClassName intent = ClassName.get("android.content", "Intent");
        ClassName URI = ClassName.get("android.net", "Uri");

        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T intent = new $T()", intent, intent);
        if (funcAttr.getActivity() != null) {
            builder.addStatement("intent.setClass(context, $T.class)", funcAttr.getActivity());
        }
        if (funcAttr.getPath() != null) {
            builder.addStatement("intent.setClassName(context, $S)", funcAttr.getPath());
        }
        if (funcAttr.getUrl() != null) {
            builder.addStatement("intent.setData($T.parse($S))", URI, funcAttr.getUrl());
        }
        if (funcAttr.getAction() != null) {
            builder.addStatement("intent.setAction($S)", funcAttr.getAction());
        }
        builder.addStatement("context.startActivity(intent)");

        return MethodSpec.methodBuilder(funcAttr.getMethodElement().getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addCode(builder.build())
                .build();
    }
}
