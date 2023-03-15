/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.javargtest;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
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

    System.out.println("DEBUG: ClassTable (getTypes) -> " + list.toString());

    return list;
  }

  public List<Class<?>> getClassTypes() throws ClassNotFoundException {
    List<Class<?>> list = new ArrayList<>();

    for (String s : mImports) {
      Class<?> t = Class.forName(s);
      list.add(t);
    }

    System.out.println("DEBUG: ClassTable (getTypes) -> " + list.toString());

    return list;
  }

  public List<Field> getCandidateFields(String type)
    throws ClassNotFoundException {
    List<Field> candidates = new ArrayList<>();

    for (String c : mImports) {
      List<Field> flds = getClassFields(c);

      // System.out.println("DEBUG: ClassTable (getCandidateFields) `List<Field>`-> " + flds);

      List<Field> collect = flds
        .stream()
        .filter(f -> f.getType().toString().equals(type))
        .collect(Collectors.toList());

      candidates.addAll(collect);
    }

    System.out.println(
      "DEBUG: ClassTable (getCandidateFields) -> " + candidates.toString()
    );

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

    System.out.println(
      "DEBUG: ClassTable (getCandidateMethods) -> " +
      candidatesMethod.toString()
    );

    return candidatesMethod;
  }

  public List<Constructor> getCandidateConstructors(String type)
    throws ClassNotFoundException {
    List<Constructor> candidatesConstructor = new ArrayList<>();

    List<Constructor> cntc = getClassConstructors(type);

    candidatesConstructor.addAll(cntc);
    System.out.println(
      "DEBUG: ClassTable (getCandidateConstructors) -> " +
      candidatesConstructor.toString()
    );
    return candidatesConstructor;
  }

  public List<Field> getClassFields(String cname)
    throws ClassNotFoundException, NoSuchFieldException, SecurityException {
    List<Field> list = new ArrayList<>();
    
    Class<?> cls = Class.forName(cname);
    Field f[] = cls.getFields();

    // Field field = .getDeclaredField("a");
    System.out.println(cls.getClassLoader().toString());
    // List<String> asd = new ArrayList<>();
    // asd.add(field.toGenericString());

    // System.out.println("==============================");
    
    // for (Field field : f) {
    //   // String declaring_class = field.getDeclaringClass().getName();
      
    //   try {
    //     Field ff = cls.getDeclaredField(field.getName());
    //     Type field_type = ff.getGenericType();

    //     String field_type2 = ff.getType().toGenericString();

    //     System.out.println("XXXXX: " + field_type);
    //     System.out.println("XXXXX: " + field_type2);
        
    //     if (field_type2 instanceof String) {
    //       System.out.println("É String");
    //     } else {
    //       System.out.println("Não é String");
    //     }

    //   } catch (NoSuchFieldException | SecurityException e) {
    //     e.printStackTrace();
    //   }
    // }

    list.addAll(Arrays.asList(f));

    System.out.println(
      "DEBUG: ClassTable (getClassFields) -> " + list.toString()
    );

    System.out.println("==============================");
    return list;
  }

  public List<String> getClassFieldTypes(String cname)
    throws ClassNotFoundException {
    List<String> list = getClassFields(cname)
      .stream()
      .map(f -> f.getGenericType().getTypeName())
      .collect(Collectors.toList());

    System.out.println(
      "DEBUG: ClassTable (getClassFieldTypes) -> " + list.toString()
    );

    return list;
  }

  public List<Method> getClassMethods(String cname)
    throws ClassNotFoundException {
    List<Method> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Method m[] = c.getDeclaredMethods();

    list.addAll(Arrays.asList(m));

    System.out.println(
      "DEBUG: ClassTable (getClassMethods) -> " + list.toString()
    );
    return list;
  }

  public List<Constructor> getClassConstructors(String cname)
    throws ClassNotFoundException {
    List<Constructor> list = new ArrayList<>();

    Class c = Class.forName(cname);

    Constructor ct[] = c.getDeclaredConstructors();

    list.addAll(Arrays.asList(ct));

    System.out.println(
      "DEBUG: ClassTable (getClassConstructors) -> " + list.toString()
    );
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

    System.out.println("DEBUG: ClassTable (superTypes) -> " + list.toString());
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

    System.out.println("DEBUG: ClassTable (subTypes) -> " + list.toString());
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

    System.out.println("DEBUG: ClassTable (subTypes2) -> " + list.toString());
    return list;
  }
}
