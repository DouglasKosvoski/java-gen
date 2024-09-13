package br.edu.ifsc.javargtest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to work with imports and analyze class methods and interfaces.
 *
 * @author Douglas Kosvoski
 */
public class InterfaceTable {

  private List<String> imports;

  public InterfaceTable(List<String> imports) {
    this.imports = imports;
  }

  /**
   * Displays the list of imports stored in the class.
   */
  public void showImports() {
    System.out.println("Printing imports:");
    for (String imp : imports) {
      System.out.println(imp);
    }
  }

  /**
   * Retrieves and prints all methods of the imported classes.
   *
   * @throws ClassNotFoundException if any of the classes in the imports list cannot be found.
   */
  public void printMethods() throws ClassNotFoundException {
    for (String imp : imports) {
      System.out.println("\nImport name: " + imp);
      Class<?> clazz = Class.forName(imp);
      Method[] methods = clazz.getDeclaredMethods();

      System.out.println("Listing available methods:");
      if (methods.length == 0) {
        System.out.println("No methods found for " + imp);
      } else {
        int count = 0;
        for (Method method : methods) {
          System.out.println(count + " -> " + method);
          count++;
        }
      }
    }
  }

  /**
   * Retrieves and prints detailed information about methods of the imported classes,
   * including method names, parameters, and return types.
   *
   * @throws ClassNotFoundException if any of the classes in the imports list cannot be found.
   */
  public void printMethodDetails() throws ClassNotFoundException {
    for (String imp : imports) {
      System.out.println("Import: " + imp);

      Class<?> clazz = Class.forName(imp);
      Method[] methods = clazz.getDeclaredMethods();

      if (methods.length == 0) {
        System.out.println("No methods found");
      } else {
        for (Method method : methods) {
          System.out.println("Method name -> " + method.getName());

          Class<?>[] paramTypes = method.getParameterTypes();
          if (paramTypes.length == 0) {
            System.out.println("No parameters found");
          } else {
            for (int i = 0; i < paramTypes.length; i++) {
              System.out.println("Parameter #" + i + " -> " + paramTypes[i]);
            }
          }

          System.out.println("Return type -> " + method.getReturnType());
          System.out.println();
        }
      }

      System.out.println("-------------------------------------------------------\n");
    }
  }

  /**
   * Finds and returns a list of classes that implement interfaces based on the provided type.
   *
   * @param type the type to be analyzed for interfaces
   * @return a list of class names that implement interfaces
   * @throws ClassNotFoundException if any of the classes in the imports list cannot be found.
   */
  public List<String> getCandidateInterfaces(String type) throws ClassNotFoundException {
    List<String> candidates = new ArrayList<>();

    for (String className : imports) {
      String interfaces = getClassInterfaces(className);

      if (!interfaces.isEmpty()) {
        candidates.add(interfaces);
      }
    }

    return candidates;
  }

  /**
   * Retrieves the interfaces implemented by the specified class.
   *
   * @param className the fully qualified name of the class
   * @return a string representation of the interfaces implemented by the class
   * @throws ClassNotFoundException if the class cannot be found.
   */
  public String getClassInterfaces(String className) throws ClassNotFoundException {
    String interfaces = Arrays.toString(Class.forName(className).getInterfaces());
    return interfaces.replace("[", "").replace("]", "");
  }
}
