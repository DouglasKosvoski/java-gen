package br.edu.ifsc.javargtest;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.*;
import java.util.List;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.stream.Collectors;

/**
 *
 * @author Douglas Kosvoski
 *
 */
public class InterfaceTable {

  private List<String> mImports;

  public InterfaceTable(List<String> imports) {
    mImports = imports;
  }

  public void show_imports() {
    for (String imp : mImports) {
      System.out.println(imp);
    }
  }

  public void get_methods() throws ClassNotFoundException {
    for (String imp : mImports) {
      System.out.println("\nImport name: " + imp);
      Class<?> c = Class.forName(imp);
      Method m[] = c.getDeclaredMethods();

      System.out.println("Listing available methods:");
      int count = 0;
      for (Method method : m) {
        System.out.println(count + " -> " + method.toString());
        count += 1;
      }
      if (count == 0) {
        System.out.println("No method found for " + imp);
      }
    }
  }

  public void get_methods_info() throws ClassNotFoundException {
    for (String imp : mImports) {
      System.out.println("Import: " + imp);

      Class<?> cls = Class.forName(imp);
      Method methlist[] = cls.getDeclaredMethods();
      int i, j;
      for (i = 0; i < methlist.length; i++) {
        Method m = methlist[i];
        System.out.println("method_name -> " + m.getName());
        // System.out.println("declaring_class -> " + m.getDeclaringClass());

        Class<?> pvec[] = m.getParameterTypes();
        for (j = 0; j < pvec.length; j++) {
          System.out.println("param_#" + j + " -> " + pvec[j]);
        }
        if (j == 0) {
          System.out.println("No parameters found");
        }

        // Class<?> evec[] = m.getExceptionTypes();
        // for (j = 0; j < evec.length; j++) {
        //   System.out.println("exception_types #" + j + " " + evec[j]);
        // }

        System.out.println("return_type -> " + m.getReturnType());
        System.out.println();
      }
      if (i == 0) {
        System.out.println("No methods found");
      }
      System.out.println("-------------------------------------------------------\n");
    }
  }
}
