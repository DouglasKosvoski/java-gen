package br.edu.ifsc.javargtest;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class responsible for managing class-related information, such as fields, methods,
 * constructors, and relationships between classes (super types and subtypes).
 */
public class ClassManager {

    private final List<String> importClassNames;

    public ClassManager(List<String> importClassNames) {
        this.importClassNames = importClassNames;
    }

    /**
     * Returns the fully qualified names of the classes represented by the imported class names.
     *
     * @return a list of fully qualified class names.
     * @throws ClassNotFoundException if any of the class names cannot be found.
     */
    public List<String> getFullyQualifiedClassNames() throws ClassNotFoundException {
        List<String> classNames = new ArrayList<>();

        for (String className : importClassNames) {
            classNames.add(Class.forName(className).getName());
        }
        System.out.println("DEBUG: ClassManager (getFullyQualifiedClassNames) -> " + classNames.toString());

        return classNames;
    }

    /**
     * Returns the Class objects for the imported class names.
     *
     * @return a list of Class objects.
     * @throws ClassNotFoundException if any of the class names cannot be found.
     */
    public List<Class<?>> getClassObjects() throws ClassNotFoundException {
        List<Class<?>> classList = new ArrayList<>();

        for (String className : importClassNames) {
            Class<?> clazz = Class.forName(className);
            classList.add(clazz);
        }
        System.out.println("DEBUG: ClassManager (getClassObjects) -> " + classList.toString());

        return classList;
    }

    /**
     * Finds fields of the specified type across all imported classes.
     *
     * @param typeName the fully qualified name of the type.
     * @return a list of Fields matching the specified type.
     * @throws ClassNotFoundException if any of the class names cannot be found.
     */
    public List<Field> getFieldsOfType(String typeName) throws ClassNotFoundException {
        List<Field> matchingFields = new ArrayList<>();

        for (String className : importClassNames) {
            List<Field> classFields = getClassFields(className);

            List<Field> filteredFields = classFields.stream()
                    .filter(field -> field.getType().getName().equals(typeName))
                    .collect(Collectors.toList());

            matchingFields.addAll(filteredFields);
        }

        System.out.println("DEBUG: ClassManager (getFieldsOfType) -> " + matchingFields.toString());
        return matchingFields;
    }

    /**
     * Finds methods with the specified return type across all imported classes.
     *
     * @param returnTypeName the fully qualified name of the return type.
     * @return a list of Methods with the specified return type.
     * @throws ClassNotFoundException if any of the class names cannot be found.
     */
    public List<Method> getMethodsOfReturnType(String returnTypeName) throws ClassNotFoundException {
        List<Method> matchingMethods = new ArrayList<>();

        for (String className : importClassNames) {
            List<Method> classMethods = getClassMethods(className);

            List<Method> filteredMethods = classMethods.stream()
                    .filter(method -> method.getReturnType().getName().equals(returnTypeName))
                    .collect(Collectors.toList());

            matchingMethods.addAll(filteredMethods);
        }

        System.out.println("DEBUG: ClassManager (getMethodsOfReturnType) -> " + matchingMethods.toString());
        return matchingMethods;
    }

    /**
     * Finds constructors in the specified class.
     *
     * @param className the fully qualified name of the class.
     * @return a list of Constructors for the specified class.
     * @throws ClassNotFoundException if the class cannot be found.
     */
    public List<Constructor<?>> getClassConstructors(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        List<Constructor<?>> constructorList = Arrays.asList(constructors);

        System.out.println("DEBUG: ClassManager (getClassConstructors) -> " + constructorList.toString());
        return constructorList;
    }

    /**
     * Returns the fields of a given class.
     *
     * @param className the fully qualified name of the class.
     * @return a list of Fields of the specified class.
     * @throws ClassNotFoundException if the class cannot be found.
     */
    public List<Field> getClassFields(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        Field[] fields = clazz.getFields();

        List<Field> fieldList = Arrays.asList(fields);

        System.out.println("DEBUG: ClassManager (getClassFields) -> " + fieldList.toString());
        return fieldList;
    }

    /**
     * Returns the method signatures of a given class.
     *
     * @param className the fully qualified name of the class.
     * @return a list of Methods of the specified class.
     * @throws ClassNotFoundException if the class cannot be found.
     */
    public List<Method> getClassMethods(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        Method[] methods = clazz.getDeclaredMethods();

        List<Method> methodList = Arrays.asList(methods);

        System.out.println("DEBUG: ClassManager (getClassMethods) -> " + methodList.toString());
        return methodList;
    }

    /**
     * Returns the superclasses of a given class.
     *
     * @param className the fully qualified name of the class.
     * @return a list of superclasses of the specified class.
     * @throws ClassNotFoundException if the class cannot be found.
     */
    public List<Class<?>> getSuperTypes(String className) throws ClassNotFoundException {
        List<Class<?>> superTypes = new ArrayList<>();
        Class<?> clazz = Class.forName(className);

        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null) {
            superTypes.add(superClass);
            superClass = superClass.getSuperclass();
        }

        System.out.println("DEBUG: ClassManager (getSuperTypes) -> " + superTypes.toString());
        return superTypes;
    }

    /**
     * Returns the subclasses of a given class recursively.
     *
     * @param className the fully qualified name of the class.
     * @return a list of subclasses of the specified class.
     * @throws ClassNotFoundException if the class cannot be found.
     */
    public List<Class<?>> getSubTypesRecursive(String className) throws ClassNotFoundException {
        List<Class<?>> subTypes = new ArrayList<>();

        Class<?> clazz = Class.forName(className);
        subTypes.add(clazz);

        if (!className.equals("java.lang.Object")) {
            subTypes.addAll(getSubTypesRecursive(clazz.getSuperclass().getName()));
        }

        System.out.println("DEBUG: ClassManager (getSubTypesRecursive) -> " + subTypes.toString());
        return subTypes;
    }

    /**
     * Returns the classes that are subtypes of the specified class.
     *
     * @param className the fully qualified name of the class.
     * @return a list of classes that are subtypes of the specified class.
     * @throws ClassNotFoundException if the class cannot be found.
     */
    public List<Class<?>> getSubTypes(String className) throws ClassNotFoundException {
        List<Class<?>> subTypes = new ArrayList<>();

        Class<?> clazz = Class.forName(className);

        for (String importedClassName : this.importClassNames) {
            List<Class<?>> superTypes = getSuperTypes(importedClassName);

            if (superTypes.contains(clazz)) {
                Class<?> importedClass = Class.forName(importedClassName);
                subTypes.add(importedClass);
            }
        }

        System.out.println("DEBUG: ClassManager (getSubTypes) -> " + subTypes.toString());
        return subTypes;
    }
}
