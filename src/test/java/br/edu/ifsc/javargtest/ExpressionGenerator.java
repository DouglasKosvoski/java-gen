package br.edu.ifsc.javargtest;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import java.util.Map;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

public class ExpressionGenerator {
  private ClassManager mCT;

  private TypeGenerator mBase;

  private CoreExpressionGenerator mCore;

  public ExpressionGenerator(ClassManager ct, TypeGenerator base, CoreExpressionGenerator core) {
    mCT = ct;

    mBase = base;

    mCore = core;
  }

  @Provide
  public Arbitrary < BinaryExpr > genLogicExpression(Map < String, String > ctx) {
    Arbitrary < Expression > e = mCore.genExpression(
      ctx,
      PrimitiveType.booleanType()
    );

    Arbitrary < Expression > ex = mCore.genExpression(
      ctx,
      PrimitiveType.booleanType()
    );

    return e.map(
      exp -> new BinaryExpr(e.sample(), ex.sample(), genLogiOperator().sample())
    );
  }

  @Provide
  public Arbitrary < BinaryExpr > genArithExpression(
    Map < String, String > ctx,
    Type t
  ) {
    //String tp = t.asString();

    Arbitrary < Expression > e = mCore.genExpression(
      ctx,
      JavaTypeTranslator.convertToParserType(t.toString())
    );

    Arbitrary < Expression > ex = mCore.genExpression(
      ctx,
      JavaTypeTranslator.convertToParserType(t.toString())
    );

    return e.map(
      exp -> new BinaryExpr(exp, ex.sample(), genArithOperator().sample())
    );
  }

  @Provide
  public Arbitrary < BinaryExpr > genRelacionalBooleanFor(
    Map < String, String > ctx,
    VariableDeclarator
    var,
    Arbitrary < LiteralExpr > ex
  ) {
    Arbitrary < PrimitiveType.Primitive > t = mBase.generateMathematicalPrimitiveTypes();
    String tp = t.sample().toString();

    Arbitrary < Expression > e = mCore.genExpression(
      ctx,
      JavaTypeTranslator.convertToParserType(tp)
    );

    return e.map(
      exp ->
      new BinaryExpr(
        var.getNameAsExpression(),
        ex.sample(),
        genOperator().sample()
      )
    );
  }

  @Provide
  public Arbitrary < BinaryExpr > genArithBooleanFor(
    Map < String, String > ctx,
    VariableDeclarator
    var,
    Arbitrary < LiteralExpr > ex
  ) {
    Arbitrary < PrimitiveType.Primitive > t = mBase.generateMathematicalPrimitiveTypes();
    String tp = t.sample().toString();

    Arbitrary < Expression > e = mCore.genExpression(
      ctx,
      JavaTypeTranslator.convertToParserType(tp)
    );

    return e.map(
      exp ->
      new BinaryExpr(
        var.getNameAsExpression(),
        ex.sample(),
        genArithOperatorFor().sample()
      )
    );
  }

  @Provide
  public Arbitrary < BinaryExpr > genRelaExpression(Map < String, String > ctx) {
    Arbitrary < PrimitiveType.Primitive > t = mBase.generateMathematicalPrimitiveTypes();

    String tp = t.sample().toString();

    Arbitrary < Expression > e = mCore.genExpression(
      ctx,
      JavaTypeTranslator.convertToParserType(tp)
    );

    Arbitrary < Expression > ex = mCore.genExpression(
      ctx,
      JavaTypeTranslator.convertToParserType(tp)
    );

    return e.map(
      exp -> new BinaryExpr(exp, e.sample(), genRelaOperator().sample())
    );
  }

  //Bo
  public Arbitrary < BinaryExpr.Operator > genLogiOperator() {
    return Arbitraries.of(
      BinaryExpr.Operator.AND,
      BinaryExpr.Operator.OR,
      BinaryExpr.Operator.XOR
    );
  }

  //Au
  public Arbitrary < BinaryExpr.Operator > genRelaOperator() {
    return Arbitraries.of(
      BinaryExpr.Operator.EQUALS,
      BinaryExpr.Operator.GREATER,
      BinaryExpr.Operator.GREATER_EQUALS,
      BinaryExpr.Operator.LESS,
      BinaryExpr.Operator.LESS_EQUALS,
      BinaryExpr.Operator.NOT_EQUALS
    );
  }

  //Ma
  public Arbitrary < BinaryExpr.Operator > genArithOperator() {
    return Arbitraries.of(
      BinaryExpr.Operator.EQUALS,
      BinaryExpr.Operator.MULTIPLY,
      BinaryExpr.Operator.MINUS,
      BinaryExpr.Operator.PLUS,
      BinaryExpr.Operator.REMAINDER
    );
  }

  public Arbitrary < BinaryExpr.Operator > genBooOperator() {
    return Arbitraries.of(
      BinaryExpr.Operator.DIVIDE,
      BinaryExpr.Operator.MULTIPLY,
      BinaryExpr.Operator.MINUS,
      BinaryExpr.Operator.PLUS,
      BinaryExpr.Operator.REMAINDER
    );
  }

  public Arbitrary < BinaryExpr.Operator > genOperator() {
    return Arbitraries.of(
      BinaryExpr.Operator.LESS,
      BinaryExpr.Operator.LESS_EQUALS
    );
  }

  public Arbitrary < BinaryExpr.Operator > genArithOperatorFor() {
    return Arbitraries.of(
      BinaryExpr.Operator.PLUS,
      BinaryExpr.Operator.MULTIPLY
    );
  }
}