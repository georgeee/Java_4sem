package ru.ifmo.ctddev.agapov.task3.generator;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;

/**
 * Class token representation.
 * It's main purpose is to generate code of class' implementation, by calling {@link #toString()} generator
 */
public class GClass {
    /**
     * class token, which we should implement
     */
    private Class<?> parent;
    /**
     * array of imports for generated class instance
     */
    private Map<String, String> imports = new HashMap<>();
    /**
     * array of class' abstract methods
     */
    private ArrayList<GMethod> methods = new ArrayList<>();
    /**
     * array of class' constructors
     */
    private ArrayList<GConstructor> constructors = new ArrayList<>();
    /**
     * set of method's highest occurrence in inheritance/implementation graph, is in use to compute methods array
     */
    private Set<GMethod> found = new HashSet<>();
    /**
     * map of proper replacements for typeVariables from whole inheritance/implementation graph
     */
    private Map<Class<?>, Map<String, String>> genericVarAliasMap = new HashMap<>();

    /**
     * @param parent class token
     * @throws ImplerException if it's impossible to implement class or token refers not to interface or class
     */
    public GClass(Class<?> parent) throws ImplerException {
        this.parent = parent;
        if (parent.isPrimitive() || parent.isArray() || parent.isEnum())
            throw new ImplerException("Class " + parent.getName() + ": wrong type passed");
        if (Modifier.isFinal(parent.getModifiers()))
            throw new ImplerException("Class " + parent.getName() + ": can't implement final class");
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

    /**
     * Returns package of generated class (equals to parent class' package)
     *
     * @return name of package of generated class
     */
    public String getPackageName() {
        return parent.getPackage().getName();
    }

    /**
     * Returns name of generated implementation class (name of parent + Impl suffix)
     *
     * @return generated class's name
     */
    public String getImplClassName() {
        return parent.getSimpleName() + "Impl";
    }

    /**
     * This generator finds abstract methods of parent class, traversing over class inheritance/interface implementation graph
     *
     * @param parent class token, in which we should search (parameter for recursion)
     */
    private void findMethods(Class<?> parent) {
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

    /**
     * Looks if a replacement for generic's type var is needed
     *
     * @param clazz     context, in which typeParam was found
     * @param typeParam type var name
     * @return found replacement for type var, or itself, if none was found
     */
    protected String replacementLookup(Class<?> clazz, String typeParam) {
        if (clazz == null) return typeParam;
        Map<String, String> map = genericVarAliasMap.get(clazz);
        if (map == null) return typeParam;
        String res = map.get(typeParam);
        return res == null ? typeParam : res;
    }

    /**
     * Looks for typeVars in clazz's declaration, resolves proper replacements for them
     * (which would be put in generated implementation, if needed), and put resolution result into {@link #genericVarAliasMap }
     *
     * @param parent       clazz's parent
     * @param clazz
     * @param genericClass generic-typed version of clazz
     * @param buffer       temporary buffer, which will be used to effectively print types to string
     */
    private void addTokenReplacements(Class<?> parent, Class<?> clazz, Type genericClass, StringBuilder buffer) {
        if (clazz != null) {
            Map<String, String> map;
            if (!genericVarAliasMap.containsKey(clazz))
                genericVarAliasMap.put(clazz, map = new HashMap<>());
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
                        map.put(type.getName(), replacementLookup(parent, actualTypeVariable.getName()));
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

    /**
     * Recursively traverse class/interface inheritance/implementation graph, finds type variables and proper replacements for them
     *
     * @param clazz  class, in which we perform the search
     * @param buffer temporary buffer, which will be used to effectively print types to string
     */
    private void findTokenReplacements(Class<?> clazz, StringBuilder buffer) {
        if (clazz == null) return;
        Class<?> superClass = clazz.getSuperclass();
        Type genericSuperClass = clazz.getGenericSuperclass();
        addTokenReplacements(clazz, superClass, genericSuperClass, buffer);
        Class<?>[] interfaces = clazz.getInterfaces();
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            addTokenReplacements(clazz, interfaces[i], genericInterfaces == null ? null : genericInterfaces[i], buffer);
        }
        findTokenReplacements(superClass, buffer);
        for (int i = 0; i < interfaces.length; ++i) {
            findTokenReplacements(interfaces[i], buffer);
        }
    }

    /**
     * Finds all non-private constructors of class, puts them into {@link #constructors} list
     *
     * @throws ImplerException if class (not interface) has no non-private constructors at all
     */
    private void findConstructors() throws ImplerException {
        for (Constructor<?> constructor : parent.getDeclaredConstructors()) {
            if (Modifier.isPrivate(constructor.getModifiers())) continue;
            constructors.add(new GConstructor(this, constructor));
        }
        if (!parent.isInterface() && constructors.size() == 0)
            throw new ImplerException("Class " + parent.getName() + ": no constructors at all");
    }

    /**
     * Prints type to buffer
     *
     * @param buffer
     * @param type
     * @param parent class token, representing context, in which type is declared
     */
    void printType(StringBuilder buffer, Type type, Class<?> parent) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            printType(buffer, pt.getRawType(), parent);
            Type[] args = pt.getActualTypeArguments();
            if (args.length > 0) {
                buffer.append("<");
                printType(buffer, args[0], parent);
                for (int i = 1; i < args.length; ++i) {
                    buffer.append(", ");
                    printType(buffer, args[i], parent);
                }
                buffer.append("> ");
            }
        } else if (type instanceof WildcardType) {
            Type[] lower = ((WildcardType) type).getLowerBounds();
            Type[] upper = ((WildcardType) type).getUpperBounds();
            buffer.append("?");
            Type wType = null;
            if (lower.length > 0) {
                buffer.append(" super ");
                wType = lower[0];
            } else if (upper.length > 0 && upper[0] != Object.class) {
                buffer.append(" extends ");
                wType = upper[0];
            }
            if (wType != null)
                printType(buffer, wType, parent);
        } else if (type instanceof Class<?>) {
            buffer.append(((Class<?>) type).getCanonicalName());
        } else if (type instanceof GenericArrayType) {
            printType(buffer, ((GenericArrayType) type).getGenericComponentType(), parent);
            buffer.append("[]");
        } else if (type instanceof TypeVariable<?>) {
            String var = ((TypeVariable) type).getName();
            buffer.append(replacementLookup(parent, var));
        } else {
            buffer.append(type.toString());
        }
    }

    /**
     * Prints typeVariables to buffer
     * (with '<' and '>' surrounding, separated by ", ")
     *
     * @param buffer
     * @param typeVariables
     */
    void printTypeParams(StringBuilder buffer, TypeVariable<?>[] typeVariables) {
        if (typeVariables.length == 0)
            return;
        buffer.append('<').append(typeVariables[0]);
        for (int i = 1; i < typeVariables.length; ++i) buffer.append(", ").append(typeVariables[i]);
        buffer.append("> ");
    }

    /**
     * Prints typeVariables of clazz's declaration to buffer
     * (with '<' and '>' surrounding, separated by ", ")
     *
     * @param buffer
     * @param clazz
     */
    void printTypeParams(StringBuilder buffer, Class<?> clazz) {
        printTypeParams(buffer, clazz.getTypeParameters());
    }

    /**
     * Prints typeVariables of method's declaration to buffer
     * (with '<' and '>' surrounding, separated by ", ")
     *
     * @param buffer
     * @param method
     */
    void printTypeParams(StringBuilder buffer, Method method) {
        printTypeParams(buffer, method.getTypeParameters());
    }

    /**
     * Generates class implementation code
     *
     * @return generated code
     */
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
            constructor.printToBuffer(sb);
            sb.append('\n');
        }
        for (GMethod method : methods) {
            method.printToBuffer(sb);
            sb.append('\n');
        }

        sb.append('}');
        return sb.toString();
    }

    /**
     * File, to which we should save our implementation, according to it's package
     * and relative to root path
     * <p/>
     * This method also creates all missing directories in the path to file
     *
     * @param root directory, relative to which we should put implementation file
     * @return path to output file for generated class implementation
     */
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
