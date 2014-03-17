package ru.ifmo.ctddev.agapov.task3.generator;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Abstract method. Nothing more, than just a base class for GMethod, GConstructor classes
 */
public abstract class GAbstractMethod {
    /**
     * @param parent class, for which instance is being created
     */
    protected GAbstractMethod(GClass parent) {
        this.parent = parent;
    }

    /**
     * class, for which instance is created
     */
    GClass parent;

    /**
     * Returns method's scope string
     *
     * @return "" if scope is package, scopeName + " " otherwise
     * (scopeName = public|private|protected)
     */
    protected String getScopeString() {
        if (Modifier.isPublic(getModifiers())) return "public ";
        if (Modifier.isProtected(getModifiers())) return "protected ";
        if (Modifier.isPrivate(getModifiers())) return "private ";
        return "";
    }

    /**
     * Returns name of method, or name of class for constructor
     *
     * @return name of method, or name of class for constructor
     */
    protected abstract String getName();

    /**
     * Modifiers of method/constructor
     *
     * @return modifiers bit mask
     */
    protected abstract int getModifiers();

    /**
     * Prints method/constructor parameters to buffer, separated by ", "
     * this method only prints real types, name of variables are generated as "a[0-9]+"
     *
     * @param buffer Buffer, where to print params
     */
    protected void printParams(StringBuilder buffer) {
        Type[] paramTypes = getParamTypes();
        int argCount = 0;
        for (int i = 0; i < paramTypes.length; ++i) {
            if (i > 0) buffer.append(", ");
            parent.printType(buffer, paramTypes[i], getContextClass());
            buffer.append(' ').append('a').append(argCount++);
        }
    }

    /**
     * Returns class, in which this method/constructor is declared
     *
     * @return method's owner class token
     */
    protected abstract Class<?> getContextClass();


    /**
     * Generates code for method/constructor's implementation and prints it to buffer
     *
     * @param buffer Buffer, to which we should print
     */
    public void printToBuffer(StringBuilder buffer) {
        buffer.append("   ");
        buffer.append(getScopeString());
        printHeaderStart(buffer);
        buffer.append('(');
        printParams(buffer);
        buffer.append(')');
        Type[] exceptionTypes = getExceptionTypes();
        if (exceptionTypes.length > 0) {
            buffer.append(" throws ");
            for (int i = 0; i < exceptionTypes.length; ++i) {
                if (i > 0) buffer.append(", ");
                parent.printType(buffer, exceptionTypes[i], getContextClass());
            }
        }
        buffer.append("{\n");
        printBody(buffer);
        buffer.append("   }\n");
    }

    /**
     * Generates body of method/constructor's implementation, prints it ot buffer
     *
     * @param buffer Buffer, to which we should print
     */
    protected abstract void printBody(StringBuilder buffer);

    /**
     * Generates header start (part between scope and parameters) of method/constructor's implementation, prints it ot buffer
     *
     * @param buffer Buffer, to which we should print
     */
    protected abstract void printHeaderStart(StringBuilder buffer);

    /**
     * Types of exceptions, throwing by method/constructor
     *
     * @return array of exceptions' types
     */
    protected abstract Type[] getExceptionTypes();

    /**
     * Types of method/constructor parameters
     *
     * @return array of parameters' types
     */
    protected abstract Type[] getParamTypes();

    /**
     * Same as {@link #printToBuffer(StringBuilder)}, by using temporary created buffer
     *
     * @return generated implementation of method/constructor
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        printToBuffer(sb);
        return sb.toString();
    }

    /**
     * Cached hashCode value
     */
    private int hashCode = 0;

    /**
     * Generates hashCode for method/constructor
     * uses parameters + name as parameters for hashing
     *
     * @return method/constructor's hashCode
     */
    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = getParamsString().hashCode() ^ getName().hashCode() ^ 0x5abc422e;
        }
        return hashCode;
    }

    /**
     * Same as {@link #printParams(StringBuilder)}, just creates a temporary buffer
     *
     * @return string, representing method's parameter list
     */
    private String getParamsString() {
        StringBuilder sb = new StringBuilder();
        printParams(sb);
        return sb.toString();
    }

    /**
     * Checks equality by method/constructor parameters and name
     *
     * @param obj object to check
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof GAbstractMethod) {
            GAbstractMethod method = (GAbstractMethod) obj;
            return getParamsString().equals(method.getParamsString()) && getName().equals(method.getName());
        }
        return false;
    }
}
