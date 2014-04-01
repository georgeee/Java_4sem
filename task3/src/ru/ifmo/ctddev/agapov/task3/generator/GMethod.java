package ru.ifmo.ctddev.agapov.task3.generator;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Class, representing method of some class, presented by GClass instance
 */
public class GMethod extends GAbstractMethod {
    /**
     * reflection method object
     */
    Method method;

    /**
     * Constructs GMethod from parent and Method object
     *
     * @param parent class, to which this method refers
     * @param method reflection method object
     */
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
    public void printToBuffer(StringBuilder buffer) {
        buffer.append("   @Override\n");
        super.printToBuffer(buffer);
    }

    @Override
    protected void printBody(StringBuilder buffer) {
        Type returnType = method.getGenericReturnType();
        if (returnType != Void.TYPE) {
            String val = "";
//            boolean.class
            if (returnType == Boolean.TYPE) val = "false";
            else if (returnType == Byte.TYPE) val = "0";
            else if (returnType == Short.TYPE) val = "0";
            else if (returnType == Character.TYPE) val = "'\\0'";
            else if (returnType == Integer.TYPE) val = "0";
            else if (returnType == Long.TYPE) val = "0";
            else if (returnType == Float.TYPE) val = "0";
            else if (returnType == Double.TYPE) val = "0";
            else val = "null";
            buffer.append("      return ").append(val).append(";\n");
        }
    }

    @Override
    protected void printHeaderStart(StringBuilder buffer) {
        parent.printTypeParams(buffer, method);
        Type returnType = method.getGenericReturnType();
        parent.printType(buffer, returnType, getContextClass());
        buffer.append(' ').append(method.getName());
    }

    @Override
    protected Type[] getExceptionTypes() {
        return method.getGenericExceptionTypes();
    }

    @Override
    protected Type[] getParamTypes() {
        return method.getGenericParameterTypes();
    }
}
