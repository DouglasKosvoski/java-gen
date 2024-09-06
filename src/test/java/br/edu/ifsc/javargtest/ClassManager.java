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

    for (String className : this.importClassNames) {
        classNames.add(Class.forName(className).getName());
    }
    System.out.println("DEBUG: ClassManager (getFullyQualifiedClassNames) -> " + classNames.toString());

    return classNames;
  }

  public List<String> getTypes() throws ClassNotFoundException {
    List<String> list = new ArrayList<>();

    for (String s : this.importClassNames) {
      list.add(Class.forName(s).getName());
    }
    System.out.println("DEBUG: ClassManager (getTypes) -> " + list.toString());

    return list;
  }

  public List<Class> getClassTypes() throws ClassNotFoundException {
    List<Class> list = new ArrayList<>();

    for (String s : this.importClassNames) {
      Class t = Class.forName(s);
      list.add(t);
    }
    System.out.println("DEBUG: ClassManager (getTypes) -> " + list.toString());

    return list;
  }

  public List<Field> getCandidateFields(String type)
    throws ClassNotFoundException {
    List<Field> candidates = new ArrayList<>();

    for (String c : this.importClassNames) {
      List<Field> flds = getClassFields(c);

      List<Field> collect = flds
        .stream()
        .filter(f -> f.getType().toString().equals(type))
        .collect(Collectors.toList());

      candidates.addAll(collect);
    }

    System.out.println("DEBUG: ClassManager (getCandidateFields) -> " + candidates.toString());
    return candidates;
  }

  public List<Method> getCandidateMethods(String type)
    throws ClassNotFoundException {
    List<Method> candidatesMethod = new ArrayList<>();

    for (String c : this.importClassNames) {
      List<Method> mthd = getClassMethods(c);

      List<Method> collect = mthd
        .stream()
        .filter(m -> m.getReturnType().toString().equals(type))
        .collect(Collectors.toList());

        candidatesMethod.addAll(collect);
      }

    System.out.println("DEBUG: ClassManager (getCandidateMethods) -> " + candidatesMethod.toString());
    return candidatesMethod;
  }

  public List<Constructor> getCandidateConstructors(String type)
    throws ClassNotFoundException {
    List<Constructor> candidatesConstructor = new ArrayList<>();

    List<Constructor> cntc = getClassConstructors(type);

    candidatesConstructor.addAll(cntc);
    System.out.println("DEBUG: ClassManager (getCandidateConstructors) -> " + candidatesConstructor.toString());
    return candidatesConstructor;
  }

  public List<Field> getClassFields(String cname)
    throws ClassNotFoundException {
    List<Field> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Field f[] = c.getFields();

    list.addAll(Arrays.asList(f));

    System.out.println("DEBUG: ClassManager (getClassFields) -> " + list.toString());
    return list;
  }

  public List<String> getClassFieldTypes(String cname)
    throws ClassNotFoundException {
    List<String> list = getClassFields(cname)
      .stream()
      .map(f -> f.getGenericType().getTypeName())
      .collect(Collectors.toList());

    System.out.println("DEBUG: ClassManager (getClassFieldTypes) -> " + list.toString());
    return list;
  }

  public List<Method> getClassMethods(String cname)
    throws ClassNotFoundException {
    List<Method> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Method m[] = c.getDeclaredMethods();

    list.addAll(Arrays.asList(m));

    System.out.println("DEBUG: ClassManager (getClassMethods) -> " + list.toString());
    return list;
  }

  public List<Constructor> getClassConstructors(String cname)
    throws ClassNotFoundException {
    List<Constructor> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Constructor ct[] = c.getDeclaredConstructors();

    list.addAll(Arrays.asList(ct));

    System.out.println("DEBUG: ClassManager (getClassConstructors) -> " + list.toString());
    return list;
  }

  public List<Class> superTypes(String cname) throws ClassNotFoundException {
    List<Class> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Class st = c.getSuperclass();

    while (st != null) {
      list.add(st);
      c = st;
      st = c.getSuperclass();
    }

    System.out.println("DEBUG: ClassManager (superTypes) -> " + list.toString());
    return list;
  }

  /*
   *
   * Get subTypes from a given class name
   *
   */
  public List<Class> subTypes(String cname) throws ClassNotFoundException {
    List<Class> list = new ArrayList<>();

    System.out.println(cname);

    Class c = Class.forName(cname);

    list.add(c);

    if (!cname.equals("java.lang.Object")) {
      list.addAll(subTypes(c.getSuperclass().getName()));
    }

    System.out.println("DEBUG: ClassManager (subTypes) -> " + list.toString());
    return list;
  }

  /*
   *
   * Get superTypes from class of given name
   *
   */
  public List<Class> subTypes2(String cname) throws ClassNotFoundException {
    List<Class> list = new ArrayList<>();

    Class c = Class.forName(cname);

    for (String cl : this.importClassNames) {
      List<Class> st = superTypes(cl);

      if (st.contains(c)) {
        Class cla = Class.forName(cl);
        list.add(cla);
      }
    }

    System.out.println("DEBUG: ClassManager (subTypes2) -> " + list.toString());
    return list;
  }
}
