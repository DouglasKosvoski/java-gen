package br.edu.ifsc.javargtest;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import java.util.ArrayList;
import java.util.List;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

/**
 * Provides methods to generate various types of literals and check type characteristics.
 * Utilizes net.jqwik for arbitrary value generation.
 */
public class TypeGenerator {
  private ClassManager classManager;

  public TypeGenerator(ClassManager classManager) {
    this.classManager = classManager;
  }

  public Arbitrary<ClassOrInterfaceType> generateClassOrInterfaceTypes() throws ClassNotFoundException {
    List<ClassOrInterfaceType> classOrInterfaceTypeList = new ArrayList<>();

    for (String fullyQualifiedName : this.classManager.getFullyQualifiedClassNames()) {
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType();
        classOrInterfaceType.setName(fullyQualifiedName);
        classOrInterfaceTypeList.add(classOrInterfaceType);
    }

    return Arbitraries.of(classOrInterfaceTypeList);
  }

  @Provide
  public Arbitrary<PrimitiveType.Primitive> generatePrimitiveTypes() {
      return Arbitraries.of(PrimitiveType.Primitive.values());
  }

  @Provide
  public Arbitrary<PrimitiveType.Primitive> generatePrimitiveIntTypes() {
      return Arbitraries.of(PrimitiveType.intType().getType());
  }

  @Provide
  public Arbitrary<PrimitiveType.Primitive> generateMathematicalPrimitiveTypes() {
    return Arbitraries.of(
        PrimitiveType.Primitive.INT,
        PrimitiveType.Primitive.DOUBLE,
        PrimitiveType.Primitive.LONG,
        PrimitiveType.Primitive.SHORT
    );
  }

  public boolean isNumericType(Type type) {
    switch (type.asPrimitiveType().getType()) {
        case DOUBLE:
        case INT:
        case LONG:
        case BYTE:
        case SHORT:
            return true;
        default:
            return false;
    }
  }

  @Provide
  public Arbitrary<LiteralExpr> generatePrimitiveString() {
    MessageLogger.log(
        MessageLogger.Severity.TRACE,
        "generatePrimitiveString: Start"
    );

    return Arbitraries
        .strings()
        .withCharRange('a', 'z')
        .ofMinLength(1)
        .ofMaxLength(5)
        .map(StringLiteralExpr::new);
  }

  @Provide
  public Arbitrary<LiteralExpr> generatePrimitiveType(PrimitiveType type) {
      switch (type.getType()) {
          case BOOLEAN:
              return Arbitraries.of(true, false).map(BooleanLiteralExpr::new);
          case CHAR:
              return Arbitraries.chars().ascii().map(CharLiteralExpr::new);
          case DOUBLE:
              return Arbitraries
                  .doubles()
                  .between(0, 99999)
                  .map(d -> new DoubleLiteralExpr(String.valueOf(d)));
          case INT:
              return Arbitraries
                  .integers()
                  .map(i -> new IntegerLiteralExpr(String.valueOf(i)));
          case LONG:
              return Arbitraries
                  .longs()
                  .map(l -> new LongLiteralExpr(String.valueOf(l)));
          case BYTE:
              return Arbitraries
                  .bytes()
                  .map(bt -> new IntegerLiteralExpr(String.valueOf(bt)));
          case SHORT:
              return Arbitraries
                  .shorts()
                  .map(s -> new IntegerLiteralExpr(String.valueOf(s)));
          default:
              return Arbitraries.just(null);
      }
  }
}
