package com.zdf.zrouter.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zdf.zrouter.anno.Action;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Url;
import com.zdf.zrouter.processor.model.Address;
import com.zdf.zrouter.processor.util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by xiaofeng on 2017/9/3.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ServiceProcessor extends AbstractProcessor {

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
        types.add(Activity.class.getCanonicalName());
        types.add(Url.class.getCanonicalName());
        types.add(Action.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        scanAllAnnotations(roundEnv);

        return false;
    }

    private void scanAllAnnotations(RoundEnvironment roundEnv) {
        List<Element> allAnnotatedElements = new ArrayList<>();
        Set<String> supportedAnnoSets = getSupportedAnnotationTypes();
        for (String anno : supportedAnnoSets) {
            TypeElement typeElement = elements.getTypeElement(anno);
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(typeElement);
            for (Element annotatedElement : annotatedElements) {
                if (!allAnnotatedElements.contains(annotatedElement)) {
                    allAnnotatedElements.add(annotatedElement);
                }
            }
        }

        Map<TypeElement, List<Address>> servicesMap = makeServicesMap(allAnnotatedElements);
        generateAllServices(servicesMap);
    }

    private boolean isElementValid(Element element) {
        ElementKind kind = element.getKind();
        if (kind != ElementKind.METHOD) {
            logger.error("Only classes and methods can be  with " + getSupportedAnnotationTypes(), element);
            return false;
        }

//        ExecutableElement methodElement = (ExecutableElement) element;
//        if (!isSupportReturnType(methodElement)) {
//            logger.error("method only support return type is " + SUPPORT_RETURN_TYPE.toString(), methodElement);
//            return false;
//        }

        Element classElement = element.getEnclosingElement();
        String className = classElement.getSimpleName().toString();
        if (!className.endsWith("Service")) {
            logger.error("[" + className + "] this class must be in end with Service", classElement);
            return false;
        }

        if (classElement.getKind() != ElementKind.INTERFACE) {
            logger.error("[" + className + "] this class must be interface", classElement);
            return false;
        }

        return true;
    }

    private Map<TypeElement, List<Address>> makeServicesMap(List<Element> allAnnotatedElements) {
        Map<TypeElement, List<Address>> servicesMap = new HashMap<>();
        for (Element element : allAnnotatedElements) {
            if (isElementValid(element)) {
                TypeElement classElement = (TypeElement) element.getEnclosingElement();
                List<Address> addressList;
                if (servicesMap.containsKey(classElement)) {
                    addressList = servicesMap.get(classElement);
                } else {
                    addressList = new ArrayList<>();
                    servicesMap.put(classElement, addressList);
                }
                addressList.add(new Address(element));
            }
        }
        return servicesMap;
    }

    private void generateAllServices(Map<TypeElement, List<Address>> servicesMap) {
        for (Map.Entry<TypeElement, List<Address>> serviceSet : servicesMap.entrySet()) {
            TypeSpec serviceClass = generateService(serviceSet.getKey(), serviceSet.getValue());
            writeClassFile("com.zdf.zrouter", serviceClass);
        }
    }

    private TypeSpec generateService(TypeElement classElement, List<Address> addressList) {
        List<MethodSpec> methodSpecList = new ArrayList<>();
        methodSpecList.add(generateConstructMethod());
        for (Address address : addressList) {
            methodSpecList.add(generateMethod(address));
        }

        return TypeSpec.classBuilder(classElement.getSimpleName() + "Impl")
                .addSuperinterface(ClassName.get(classElement))
                .addModifiers(Modifier.PUBLIC)
                .addField(generateField())
                .addMethods(methodSpecList)
                .build();
    }

    private FieldSpec generateField() {
        ClassName context = ClassName.get("android.content", "Context");
        return FieldSpec.builder(context, "context", Modifier.PRIVATE).build();
    }

    private MethodSpec generateConstructMethod() {
        ClassName context = ClassName.get("android.content", "Context");

        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(context, "context")
                .addStatement("this.context = context")
                .build();
    }

    private MethodSpec generateMethod(Address address) {
        ClassName intent = ClassName.get("android.content", "Intent");
        ClassName URI = ClassName.get("android.net", "Uri");

        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T intent = new $T()", intent, intent);
        if (address.getActivity() != null) {
            builder.addStatement("intent.setClass(context, $T.class)", address.getActivity());
        }
        if (address.getUrl() != null) {
            builder.addStatement("intent.setData($T.parse($S))", URI, address.getUrl());
        }
        if (address.getAction() != null) {
            builder.addStatement("intent.setAction($S)", address.getAction());
        }
        builder.addStatement("context.startActivity(intent)");

        return MethodSpec.methodBuilder(address.getMethodElement().getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
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
