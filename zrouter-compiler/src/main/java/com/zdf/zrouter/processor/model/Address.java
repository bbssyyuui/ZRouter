package com.zdf.zrouter.processor.model;

import com.zdf.zrouter.anno.Action;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Url;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Created by xiaofeng on 2017/9/4.
 */

public class Address {

    private ExecutableElement methodElement;
    private TypeMirror activity;
    private String url;
    private String action;

    public Address(Element element) {
        methodElement = (ExecutableElement) element;
        Activity annoActivity = element.getAnnotation(Activity.class);
        Url annoUrl = element.getAnnotation(Url.class);
        Action annoAction = element.getAnnotation(Action.class);

        if (annoActivity != null) {
            try {
                annoActivity.value();
            } catch (MirroredTypeException mte) {
                activity = mte.getTypeMirror();
            }
        }

        if (annoUrl != null) {
            url = annoUrl.value();
        }

        if (annoAction != null) {
            action = annoAction.value();
        }
    }

    public ExecutableElement getMethodElement() {
        return methodElement;
    }

    public void setMethodElement(ExecutableElement methodElement) {
        this.methodElement = methodElement;
    }

    public TypeMirror getActivity() {
        return activity;
    }

    public void setActivity(TypeMirror activity) {
        this.activity = activity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
