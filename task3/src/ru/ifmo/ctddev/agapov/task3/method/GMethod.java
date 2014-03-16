package ru.ifmo.ctddev.agapov.task3.method;

import ru.ifmo.ctddev.agapov.task3.GClass;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by georgeee on 15.03.14.
 */
public class GMethod extends GAbstractMethod {

    Method method;

    public GMethod(GClass parent, Method method) {
        super(parent);
        this.method = method;
    }

    @Override
    protected String getName() {
        return method.getName();
    }

    @Override
    protected int getModifiers() {
        return method.getModifiers();
    }

    @Override
    protected Class<?> getContextClass() {
        return method.getDeclaringClass();
    }

    @Override
    public void printToSB(StringBuilder sb) {
        sb.append("   @Override\n");
        super.printToSB(sb);
    }

    @Override
    void printBody(StringBuilder sb) {
        Type returnType = method.getGenericReturnType();
        if (returnType != Void.TYPE) {
            String val = "";
            if (returnType == Boolean.TYPE) val = "false";
            else if (returnType == Byte.TYPE) val = "0";
            else if (returnType == Short.TYPE) val = "0";
            else if (returnType == Character.TYPE) val = "'\\0'";
            else if (returnType == Integer.TYPE) val = "0";
            else if (returnType == Long.TYPE) val = "0";
            else if (returnType == Float.TYPE) val = "0";
            else if (returnType == Double.TYPE) val = "0";
            else val = "null";
            sb.append("      return ").append(val).append(";\n");
        }
    }

    @Override
    void printHeaderStart(StringBuilder sb) {
        parent.printTypeParams(sb, method);
        Type returnType = method.getGenericReturnType();
        parent.printType(sb, returnType, getContextClass());
        sb.append(' ').append(method.getName());
    }

    @Override
    Type[] getExceptionTypes() {
        return method.getGenericExceptionTypes();
    }

    @Override
    Type[] getParamTypes() {
        return method.getGenericParameterTypes();
    }
}
