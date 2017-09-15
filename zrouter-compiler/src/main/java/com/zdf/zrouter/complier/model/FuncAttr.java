package com.zdf.zrouter.complier.model;

import com.zdf.zrouter.anno.Action;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Anim;
import com.zdf.zrouter.anno.Class;
import com.zdf.zrouter.anno.Flag;
import com.zdf.zrouter.anno.Param;
import com.zdf.zrouter.anno.Path;
import com.zdf.zrouter.anno.Url;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Created by xiaofeng on 2017/9/4.
 */

public class FuncAttr {

    private ExecutableElement methodElement;
    private TypeMirror clazz;
    private String activity;
    private String path;
    private String url;
    private String action;
    private int flags;
    private int animIn;
    private int animOut;
    private Map<String, VariableElement> paramMap = new HashMap<>();

    public FuncAttr(Element element) {
        methodElement = (ExecutableElement) element;

        Class annoClass = element.getAnnotation(Class.class);
        Activity annoActivity = element.getAnnotation(Activity.class);
        Path annoPath = element.getAnnotation(Path.class);
        Url annoUrl = element.getAnnotation(Url.class);
        Action annoAction = element.getAnnotation(Action.class);
        Flag annoFlag = element.getAnnotation(Flag.class);
        Anim annoAnim = element.getAnnotation(Anim.class);

        if (annoClass != null) {
            try {
                annoClass.value();
            } catch (MirroredTypeException mte) {
                clazz = mte.getTypeMirror();
            }
        }

        if (annoActivity != null) {
            activity = annoActivity.value();
        }

        if (annoPath != null) {
            path = annoPath.value();
        }

        if (annoUrl != null) {
            url = annoUrl.value();
        }

        if (annoAction != null) {
            action = annoAction.value();
        }

        if (annoFlag != null) {
            flags = annoFlag.value();
        }

        if (annoAnim != null) {
            animIn = annoAnim.in();
            animOut = annoAnim.out();
        }

        List<? extends VariableElement> params = methodElement.getParameters();
        if (params != null) {
            for (VariableElement p : params) {
                Param annoParam = p.getAnnotation(Param.class);
                paramMap.put(annoParam.value(), p);
            }
        }
    }

    public ExecutableElement getMethodElement() {
        return methodElement;
    }

    public void setMethodElement(ExecutableElement methodElement) {
        this.methodElement = methodElement;
    }

    public TypeMirror getClazz() {
        return clazz;
    }

    public void setClazz(TypeMirror clazz) {
        this.clazz = clazz;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getAnimIn() {
        return animIn;
    }

    public void setAnimIn(int animIn) {
        this.animIn = animIn;
    }

    public int getAnimOut() {
        return animOut;
    }

    public void setAnimOut(int animOut) {
        this.animOut = animOut;
    }

    public Map<String, VariableElement> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, VariableElement> paramMap) {
        this.paramMap = paramMap;
    }
}
