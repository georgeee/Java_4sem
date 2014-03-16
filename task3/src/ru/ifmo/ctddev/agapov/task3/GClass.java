package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import ru.ifmo.ctddev.agapov.task3.method.GConstructor;
import ru.ifmo.ctddev.agapov.task3.method.GMethod;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by georgeee on 13.03.14.
 */
public class GClass {
    Class<?> parent;
    Map<String, String> imports = new HashMap<String, String>();
    ArrayList<GMethod> methods = new ArrayList<GMethod>();
    ArrayList<GConstructor> constructors = new ArrayList<GConstructor>();
    Set<GMethod> found = new HashSet<GMethod>();
    Map<Class<?>, Map<String, String>> genericVarAliasMap = new HashMap<Class<?>, Map<String, String>>();

    public GClass(Class<?> parent) throws ImplerException {
        this.parent = parent;
        if (parent.isPrimitive() || parent.isArray() || parent.isEnum())
            throw new ImplerException("Class " + parent.getName() + ": wrong type passed");
        if (Modifier.isFinal(parent.getModifiers())) throw new ImplerException("Class " + parent.getName() + ": can't implement final class");
        findMethods(parent);
        findConstructors();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        genericVarAliasMap.put(parent, hashMap);
        for (TypeVariable<?> type : parent.getTypeParameters()) {
            hashMap.put(type.getName(), type.getName());
        }
        StringBuilder sb = new StringBuilder();
        findTokenReplacements(parent, sb);
    }

    public String getPackageName() {
        return parent.getPackage().getName();
    }

    public String getImplClassName() {
        return parent.getSimpleName() + "Impl";
    }

    private void findMethods(Class<?> parent) throws ImplerException {
        for (Method method : parent.getDeclaredMethods()) {
            GMethod gMethod = new GMethod(this, method);
            if (!found.contains(gMethod)) {
                found.add(gMethod);
                if (Modifier.isAbstract(method.getModifiers()))
                    methods.add(gMethod);
            }
        }
        if (parent.getSuperclass() != null) findMethods(parent.getSuperclass());
        for (Class<?> cl : parent.getInterfaces()) findMethods(cl);
    }

    String replacementLookup(Class<?> cl, String typeParam, String defaultValue) {
        if (cl == null) return defaultValue;
        Map<String, String> map = genericVarAliasMap.get(cl);
        if (map == null) return defaultValue;
        String res = map.get(typeParam);
        return res == null ? defaultValue : res;
    }

    void addTokenReplacements(Class<?> parent, Class<?> clazz, Type genericClass, StringBuilder buffer) {
        if (clazz != null) {
            Map<String, String> map;
            if (!genericVarAliasMap.containsKey(clazz))
                genericVarAliasMap.put(clazz, map = new HashMap<String, String>());
            else
                map = genericVarAliasMap.get(clazz);
            TypeVariable<?>[] superClassTypeVars = clazz.getTypeParameters();
            if (genericClass != null && genericClass instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) genericClass;
                Type[] actualTypeArguments = pType.getActualTypeArguments();
                for (int i = 0; i < superClassTypeVars.length; ++i) {
                    TypeVariable<?> type = superClassTypeVars[i];
                    Type actual = actualTypeArguments[i];
                    if (actual instanceof TypeVariable<?>) {
                        TypeVariable<?> actualTypeVariable = (TypeVariable<?>) actual;
                        map.put(type.getName(), replacementLookup(parent, actualTypeVariable.getName(), actualTypeVariable.getName()));
                    } else {
                        buffer.delete(0, buffer.length());
                        printType(buffer, actual, null);
                        map.put(type.getName(), buffer.toString());
                    }
                }
            } else {
                for (TypeVariable<?> type : superClassTypeVars) {
                    map.put(type.getName(), Object.class.getCanonicalName());
                }
            }
        }
    }

    void findTokenReplacements(Class<?> cl, StringBuilder buffer) {
        if (cl == null) return;
        Class<?> superClass = cl.getSuperclass();
        Type genericSuperClass = cl.getGenericSuperclass();
        addTokenReplacements(cl, superClass, genericSuperClass, buffer);
        Class<?>[] interfaces = cl.getInterfaces();
        Type[] genericInterfaces = cl.getGenericInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            addTokenReplacements(cl, interfaces[i], genericInterfaces == null ? null : genericInterfaces[i], buffer);
        }
        findTokenReplacements(superClass, buffer);
        for (int i = 0; i < interfaces.length; ++i) {
            findTokenReplacements(interfaces[i], buffer);
        }
    }

    private void findConstructors() throws ImplerException {
        for (Constructor<?> constructor : parent.getDeclaredConstructors()) {
            if (Modifier.isPrivate(constructor.getModifiers())) continue;
            constructors.add(new GConstructor(this, constructor));
        }
        if (!parent.isInterface() && constructors.size() == 0)
            throw new ImplerException("Class " + parent.getName() + ": no constructors at all");
    }

    public void printType(StringBuilder sb, Type type, Class<?> parent) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            printType(sb, pt.getRawType(), parent);
            Type[] args = pt.getActualTypeArguments();
            if (args.length > 0) {
                sb.append("<");
                printType(sb, args[0], parent);
                for (int i = 1; i < args.length; ++i) {
                    sb.append(", ");
                    printType(sb, args[i], parent);
                }
                sb.append("> ");
            }
        } else if (type instanceof WildcardType) {
            Type[] lower = ((WildcardType) type).getLowerBounds();
            Type[] upper = ((WildcardType) type).getUpperBounds();
            sb.append("?");
            Type wType = null;
            if (lower.length > 0) {
                sb.append(" super ");
                wType = lower[0];
            } else if (upper.length > 0 && upper[0] != Object.class) {
                sb.append(" extends ");
                wType = upper[0];
            }
            if (wType != null)
                printType(sb, wType, parent);
        } else if (type instanceof Class<?>) {
            sb.append(((Class<?>) type).getCanonicalName());
        } else if (type instanceof GenericArrayType) {
            printType(sb, ((GenericArrayType) type).getGenericComponentType(), parent);
            sb.append("[]");
        } else if (type instanceof TypeVariable<?>) {
            String var = ((TypeVariable) type).getName();
            sb.append(replacementLookup(parent, var, var));
        } else {
            sb.append(type.toString());
        }
    }

    public void printTypeParams(StringBuilder sb, TypeVariable<?>[] tparams) {
        if (tparams.length == 0) return;
        sb.append('<').append(tparams[0]);
        for (int i = 1; i < tparams.length; ++i) sb.append(", ").append(tparams[i]);
        sb.append("> ");
    }

    public void printTypeParams(StringBuilder sb, Class<?> cl) {
        printTypeParams(sb, cl.getTypeParameters());
    }

    public void printTypeParams(StringBuilder sb, Method method) {
        printTypeParams(sb, method.getTypeParameters());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(getPackageName()).append(";\n\n");
        for (Map.Entry<String, String> importPath : imports.entrySet())
            sb.append("import ").append(importPath.getValue()).append(";\n");
        sb.append('\n').append("public class ").append(getImplClassName()).append(' ');
        printTypeParams(sb, parent);
        sb.append(parent.isInterface() ? "implements " : "extends ").append(parent.getSimpleName());
        printTypeParams(sb, parent);
        sb.append("{\n");

        for (GConstructor constructor : constructors) {
            constructor.printToSB(sb);
            sb.append('\n');
        }
        for (GMethod method : methods) {
            method.printToSB(sb);
            sb.append('\n');
        }

        sb.append('}');
        return sb.toString();
    }


    public File getOutputFile(File root) {
        String[] parts = getPackageName().split("\\.");
        String path = root.getPath();
        for (String part : parts) {
            path = path.concat(File.separator).concat(part);
        }
        new File(path).mkdirs();
        return new File(path.concat(File.separator).concat(getImplClassName() + ".java"));
    }

}
