package com.zdf.zrouter.complier.model;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * Created by xiaofeng on 2017/9/4.
 */

public class FieldAttr {

    private VariableElement fieldElement;

    public FieldAttr(Element element) {
        fieldElement = (VariableElement) element;
    }

    public VariableElement getFieldElement() {
        return fieldElement;
    }

    public void setFieldElement(VariableElement fieldElement) {
        this.fieldElement = fieldElement;
    }
}
