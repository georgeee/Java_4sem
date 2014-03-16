package ru.ifmo.ctddev.agapov.task3.method;

import ru.ifmo.ctddev.agapov.task3.GClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * Created by georgeee on 15.03.14.
 */
public class GConstructor extends GAbstractMethod {
    Constructor<?> constructor;

    public GConstructor(GClass parent, Constructor<?> constructor) {
        super(parent);
        this.constructor = constructor;
    }

    @Override
    protected String getName() {
        return parent.getImplClassName();
    }

    @Override
    protected int getModifiers() {
        return constructor.getModifiers();
    }

    @Override
    protected Class<?> getContextClass() {
        return constructor.getDeclaringClass();
    }

    @Override
    void printBody(StringBuilder sb) {
        sb.append("      super(");
        for (int i = 0; i < getParamTypes().length; ++i) {
            if (i > 0) sb.append(", ");
            sb.append('a').append(i);
        }
        sb.append(");\n");
    }

    @Override
    void printHeaderStart(StringBuilder sb) {
        sb.append(parent.getImplClassName());
    }

    @Override
    Type[] getExceptionTypes() {
        return constructor.getGenericExceptionTypes();
    }

    @Override
    Type[] getParamTypes() {
        constructor.getGenericExceptionTypes();
        return constructor.getGenericParameterTypes();
    }
}