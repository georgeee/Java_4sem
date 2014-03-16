package ru.ifmo.ctddev.agapov.task3.method;

import ru.ifmo.ctddev.agapov.task3.GClass;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Created by georgeee on 15.03.14.
 */
public abstract class GAbstractMethod {

    protected GAbstractMethod(GClass parent) {
        this.parent = parent;
    }

    GClass parent;

    public String getScopeString() {
        if (Modifier.isPublic(getModifiers())) return "public ";
        if (Modifier.isProtected(getModifiers())) return "protected ";
        if (Modifier.isPrivate(getModifiers())) return "private ";
        return "";
    }

    protected abstract String getName();

    protected abstract int getModifiers();

    protected void printMethodParams(StringBuilder sb) {
        Type[] paramTypes = getParamTypes();
        int argCount = 0;
        for (int i = 0; i < paramTypes.length; ++i) {
            if (i > 0) sb.append(", ");
            parent.printType(sb, paramTypes[i], getContextClass());
            sb.append(' ').append('a').append(argCount++);
        }
    }

    protected abstract Class<?> getContextClass();

    public void printToSB(StringBuilder sb) {
        sb.append("   ");
        sb.append(getScopeString());
        printHeaderStart(sb);
        sb.append('(');
        printMethodParams(sb);
        sb.append(')');
        Type[] exceptionTypes = getExceptionTypes();
        if (exceptionTypes.length > 0) {
            sb.append(" throws ");
            for (int i = 0; i < exceptionTypes.length; ++i) {
                if (i > 0) sb.append(", ");
                parent.printType(sb, exceptionTypes[i], getContextClass());
            }
        }
        sb.append("{\n");
        printBody(sb);
        sb.append("   }\n");
    }

    abstract void printBody(StringBuilder sb);

    abstract void printHeaderStart(StringBuilder sb);

    abstract Type[] getExceptionTypes();

    abstract Type[] getParamTypes();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        printToSB(sb);
        return sb.toString();
    }

    private int hashCode = 0;

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = getMethodParams().hashCode() ^ getName().hashCode() ^ 0x5abc422e;
        }
        return hashCode;
    }

    public String getMethodParams() {
        StringBuilder sb = new StringBuilder();
        printMethodParams(sb);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof GAbstractMethod) {
            GAbstractMethod method = (GAbstractMethod) obj;
            return getMethodParams().equals(method.getMethodParams()) && getName().equals(method.getName());
        }
        return false;
    }
}
