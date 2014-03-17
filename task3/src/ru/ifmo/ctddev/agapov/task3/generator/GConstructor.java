package ru.ifmo.ctddev.agapov.task3.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * Class, representing constructor of some class, presented by GClass instance
 */
public class GConstructor extends GAbstractMethod {
    /**
     * reflection constructor object
     */
    Constructor<?> constructor;

    /**
     * Constructs GConstructor from parent and Constructor object
     *
     * @param parent      class, to which this method refers
     * @param constructor reflection constructor object
     */
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
    protected void printBody(StringBuilder buffer) {
        buffer.append("      super(");
        for (int i = 0; i < getParamTypes().length; ++i) {
            if (i > 0) buffer.append(", ");
            buffer.append('a').append(i);
        }
        buffer.append(");\n");
    }

    @Override
    protected void printHeaderStart(StringBuilder buffer) {
        buffer.append(parent.getImplClassName());
    }

    @Override
    protected Type[] getExceptionTypes() {
        return constructor.getGenericExceptionTypes();
    }

    @Override
    protected Type[] getParamTypes() {
        constructor.getGenericExceptionTypes();
        return constructor.getGenericParameterTypes();
    }
}