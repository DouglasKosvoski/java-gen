package br.edu.ifsc.javargtest;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author unknown
 *
 */
public class ClassTable {
  private List<String> mImports;

  public ClassTable(List<String> imports) {
    mImports = imports;
  }

  public List<String> getTypes() throws ClassNotFoundException {
    List<String> list = new ArrayList<>();

    for (String s : mImports) {
      list.add(Class.forName(s).getName());
    }
    return list;
  }

  public List<Field> getCandidateFields(String type)
      throws ClassNotFoundException {
    List<Field> candidates = new ArrayList<>();

    for (String c : mImports) {
      List<Field> flds = getClassFields(c);

      List<Field> collect = flds
          .stream()
          .filter(f -> f.getType().toString().equals(type))
          .collect(Collectors.toList());

      candidates.addAll(collect);
    }

    return candidates;
  }

  public List<String> getCandidateInterfaces(String type)
      throws ClassNotFoundException {
    List<String> candidates = new ArrayList<>();

    for (String c : mImports) {
      String interfaces = getClassInterfaces(c);
      if (!interfaces.isEmpty()) {
        candidates.add(interfaces);
      }
    }
    return candidates;
  }

  public List<Method> getCandidateMethods(String type)
      throws ClassNotFoundException {
    List<Method> candidatesMethod = new ArrayList<>();

    for (String c : mImports) {
      List<Method> mthd = getClassMethods(c);

      List<Method> collect = mthd
          .stream()
          .filter(m -> m.getReturnType().toString().equals(type))
          .collect(Collectors.toList());

      candidatesMethod.addAll(collect);
    }

    return candidatesMethod;
  }

  public List<Constructor> getCandidateConstructors(String type)
      throws ClassNotFoundException {
    List<Constructor> candidatesConstructor = new ArrayList<>();

    List<Constructor> cntc = getClassConstructors(type);

    candidatesConstructor.addAll(cntc);

    return candidatesConstructor;
  }

  public List<Field> getClassFields(String cname)
      throws ClassNotFoundException {
    List<Field> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Field f[] = c.getFields();

    list.addAll(Arrays.asList(f));

    return list;
  }

  public List<String> getClassFieldTypes(String cname)
      throws ClassNotFoundException {
    List<String> list = getClassFields(cname)
        .stream()
        .map(f -> f.getGenericType().getTypeName())
        .collect(Collectors.toList());

    return list;
  }

  public List<Method> getClassMethods(String cname)
      throws ClassNotFoundException {
    List<Method> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Method m[] = c.getDeclaredMethods();

    list.addAll(Arrays.asList(m));

    return list;
  }

  public List<Constructor> getClassConstructors(String cname)
      throws ClassNotFoundException {
    List<Constructor> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Constructor ct[] = c.getDeclaredConstructors();

    list.addAll(Arrays.asList(ct));

    return list;
  }

  public String getClassInterfaces(String cname)
      throws ClassNotFoundException {
    String interfaces = Arrays.toString(Class.forName(cname).getInterfaces());
    interfaces = interfaces.replace("[", "");
    interfaces = interfaces.replace("]", "");
    
    return interfaces;

    // System.out.println("cname -> " + cname.toString());
    // System.out.println("cname -> " + cname.toString());
    // System.out.println("field list -> " + list.toString());
    // System.out.println("class name -> " + c.toString());
    // System.out.println("class name is interface -> " + c.isInterface());
    // System.out.println("class name get interface -> " + c.getInterfaces());

    // Class<?> teste = Class.forName(cname);
    // Class<?>[] inters = teste.getInterfaces();
    // AnnotatedType[] testeMods = teste.getAnnotatedInterfaces();

    // Type[] testeMods2 = teste.getGenericInterfaces();
    // int modifiers = teste.getModifiers();

    // System.out.println("inters -> " + inters);
    // System.out.println("inters STR-> " + inters.toString());
    // System.out.println("testeMods -> " + testeMods.toString());
    // System.out.println("testeModsCC -> " + testeMods.getClass());
    // System.out.println("testeMods2 -> " + testeMods2.toString());
    // System.out.println("modifiers -> " + modifiers);
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

    for (String cl : this.mImports) {
      List<Class> st = superTypes(cl);

      if (st.contains(c)) {
        Class cla = Class.forName(cl);
        list.add(cla);
      }
    }

    return list;
  }
}
