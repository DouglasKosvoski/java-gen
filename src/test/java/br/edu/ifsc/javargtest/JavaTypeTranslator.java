/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.javargtest;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

/**
 * A utility class for translating Java reflection type names into JavaParser Type objects.
 * This class maps primitive types and object types to their respective JavaParser representations.
 */
public class JavaTypeTranslator {

  /**
   * Converts a given type name in string format to its corresponding JavaParser Type representation.
   *
   * @param typeName the name of the type to be converted (e.g., "int", "String").
   * @return the JavaParser Type object representing the given type name.
   */
  public static Type convertToParserType(String typeName) {
    switch (typeName.toLowerCase()) {
    case "int":
      return PrimitiveType.intType();
    case "float":
      return PrimitiveType.floatType();
    case "double":
      return PrimitiveType.doubleType();
    case "boolean":
      return PrimitiveType.booleanType();
    case "char":
      return PrimitiveType.charType();
    case "long":
      return PrimitiveType.longType();
    case "byte":
      return PrimitiveType.byteType();
    case "short":
      return PrimitiveType.shortType();
    default:
      // If the type is not a primitive, assume it's a class or interface type.
      return new ClassOrInterfaceType(null, typeName);
    }
  }
}